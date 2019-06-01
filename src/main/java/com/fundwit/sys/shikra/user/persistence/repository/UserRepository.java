package com.fundwit.sys.shikra.user.persistence.repository;

import com.fundwit.sys.shikra.user.persistence.po.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String name);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}
