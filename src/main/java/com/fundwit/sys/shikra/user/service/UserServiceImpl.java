package com.fundwit.sys.shikra.user.service;

import com.fundwit.sys.shikra.authentication.LoginUser;
import com.fundwit.sys.shikra.exception.ResourceNotFoundException;
import com.fundwit.sys.shikra.user.persistence.po.Identity;
import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.persistence.repository.IdentityRepository;
import com.fundwit.sys.shikra.user.persistence.repository.UserRepository;
import com.fundwit.sys.shikra.user.pojo.RegisterRequest;
import com.fundwit.sys.shikra.user.pojo.UserAccountInfo;
import com.fundwit.sys.shikra.util.IdWorker;
import org.h2.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService{
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);
    public static final String LOCAL_CREDENTIAL = "LOCAL_CREDENTIAL";

    private IdWorker idWorker;
    private UserRepository userRepository;
    private IdentityRepository identityRepository;
    private PasswordEncoder passwordEncoder;
    private PasswordObfuscator passwordObfuscator;

    public UserServiceImpl(
            UserRepository userRepository,
            IdentityRepository identityRepository,
            IdWorker idWorker,
            PasswordEncoder passwordEncoder,
            PasswordObfuscator passwordObfuscator){
        this.userRepository = userRepository;
        this.identityRepository = identityRepository;
        this.idWorker = idWorker;
        this.passwordEncoder = passwordEncoder;
        this.passwordObfuscator = passwordObfuscator;
    }

    @Override
    public boolean existsByUsername(String username) {
        return this.userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.userRepository.existsByEmail(email);
    }

    @Override
    public Mono<LoginUser> authenticate(String principal, String password) throws AuthenticationException {
        User user = this.userRepository.findByUsernameOrEmail(principal, principal).orElseThrow(()-> new AuthenticationServiceException("user not found"));
        Identity identity = identityRepository.findByUserIdAndType(user.getId(), LOCAL_CREDENTIAL);
        String  obfuscatedPassword = this.passwordObfuscator.obfuscate(password, user.getSalt());
        if(identity==null || !passwordEncoder.matches(obfuscatedPassword, identity.getCredential())) {
            throw new AuthenticationServiceException("authentication failed");
        }

        LoginUser loginUser = new LoginUser();
        loginUser.setId(user.getId());
        loginUser.setEmail(user.getEmail());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getNickname());

        return Mono.just(loginUser);
    }

    @Override
    @Transactional
    public Mono<User> createUser(RegisterRequest registerRequest) {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setNickname(StringUtils.isNullOrEmpty(registerRequest.getNickname()) ?
                        registerRequest.getUsername() : registerRequest.getNickname());
        user.setEmail(registerRequest.getEmail());

        user.setId(idWorker.nextId());
        user.setSalt(UUID.randomUUID().toString().replace("-",""));
        user.setActive(true);
        user.setCreateAt(new Date());
        user.setLastUpdateAt(user.getCreateAt());

        User savedUser = userRepository.saveAndFlush(user);

        Identity identity = new Identity();
        identity.setId(idWorker.nextId());
        identity.setUserId(savedUser.getId());
        identity.setType(LOCAL_CREDENTIAL);
        String  obfuscatedPassword = this.passwordObfuscator.obfuscate(registerRequest.getPassword(), user.getSalt());
        identity.setCredential(this.passwordEncoder.encode(obfuscatedPassword));

        identity.setCreateAt(new Date());
        identity.setLastUpdateAt(identity.getCreateAt());

        Identity savedIdentity = identityRepository.saveAndFlush(identity);
        Assert.notNull(savedIdentity);
        return Mono.just(savedUser);
    }

    @Override
    public Mono<User> findUser(Long userId) {
        Assert.notNull(userId, "parameter 'userId' must not null");
        // getOne()  EntityNotFoundException

        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User", "id", userId+""));
        return Mono.just(user);
    }

    @Override
    public Flux<User> listUser(){
        return Flux.fromIterable(userRepository.findAll());
    }

    @Override
    public Mono<User> updateUser(Long userId, UserAccountInfo userInfo){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException(User.class.getSimpleName(), "id", String.valueOf(userId)));

        user.setUsername(userInfo.getUsername());
        user.setNickname(userInfo.getNickname());
        user.setEmail(userInfo.getEmail());
        user.setPhone(userInfo.getPhone());

        user.setLastUpdateAt(new Date());
        userRepository.saveAndFlush(user);
        return Mono.just(user);
    }

    @Override
    public Mono<Void> deleteUser(Long userId) {
        // EmptyResultDataAccessException
        userRepository.deleteById(userId);
        return Mono.empty();
    }
}
