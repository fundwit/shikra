package com.fundwit.sys.shikra.user.it;

import com.fundwit.sys.shikra.user.mock.MockBasicAuthenticationProvider;
import com.fundwit.sys.shikra.user.persistence.po.User;
import com.fundwit.sys.shikra.user.persistence.repository.UserRepository;
import com.fundwit.sys.shikra.user.pojo.UserAccountInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({MockBasicAuthenticationProvider.class})
public class UserControllerTest {
    protected WebTestClient client;
    @LocalServerPort
    private int port;
    @Autowired
    UserRepository userRepository;

    @Before
    public void setUp() throws UnsupportedEncodingException {
        String header = Base64Utils.encodeToString(("mockUser:mockUserPassword").getBytes("UTF-8"));

        this.client = WebTestClient.bindToServer().baseUrl("http://localhost:" + this.port)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic "+header)
                .responseTimeout(Duration.ofMinutes(5))
                //.defaultHeader("")
                .build();
    }

    @After
    public void tearDown() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    private void createUser(UserAccountInfo userAccountInfo) {
        User user = new User();
        BeanUtils.copyProperties(userAccountInfo, user);
        user.setActive(true);

        client.post().uri("users").syncBody(userAccountInfo).exchange()
                .expectStatus().isCreated()
                .expectBody(User.class).value(u -> assertNotNull(u));
    }

    @Test
    public void testlistUserSimple() {
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(0);

        UserAccountInfo userAccountInfo = new UserAccountInfo();
        userAccountInfo.setUsername("test1");
        userAccountInfo.setNickname("Teset1");
        userAccountInfo.setEmail("test1@test.com");

        this.createUser(userAccountInfo);
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(1).value(p -> {
            User user = p.get(0);
            assertNotNull(user);
            assertEquals(userAccountInfo.getUsername(), user.getUsername());
        });

        UserAccountInfo userAccountInfo2 = new UserAccountInfo();
        userAccountInfo2.setUsername("test2");
        userAccountInfo2.setNickname("Teset2");
        userAccountInfo2.setEmail("test2@test.com");
        this.createUser(userAccountInfo2);
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(2).value(p -> {
            User user = p.get(0);
            assertNotNull(user);
            assertEquals(userAccountInfo.getUsername(), user.getUsername());

            User user2 = p.get(1);
            assertNotNull(user);
            assertEquals(userAccountInfo2.getUsername(), user2.getUsername());
        });
    }

    @Test
    public void testCreateAndGetUserSimple() {
        UserAccountInfo userAccountInfo = new UserAccountInfo();
        userAccountInfo.setUsername("test3");
        userAccountInfo.setNickname("Teset3");
        userAccountInfo.setEmail("test3@test.com");

        User user = new User();
        BeanUtils.copyProperties(userAccountInfo, user);
        user.setActive(true);

        List<User> userWrapper = new ArrayList<>();
        client.post().uri("users").syncBody(userAccountInfo).exchange()
                .expectStatus().isCreated()
                .expectBody(User.class).value(u -> {
            assertNotNull(u);
            assertEquals(user.getUsername(), u.getUsername());
            assertEquals(user.getEmail(), u.getEmail());
            assertEquals(user.getNickname(), u.getNickname());
            assertNotNull(u.getId());
            assertNull(u.getSalt());
            assertNotNull(u.getCreateAt());
            assertNull(u.getPhone());
            assertNotNull(u.getLastUpdateAt());
            assertTrue(u.isActive());
            userWrapper.add(u);
        });

        client.get().uri("users/{id}", userWrapper.get(0).getId()).exchange()
                .expectStatus().isOk()
                .expectBody(User.class).value(u -> {
            assertNotNull(u);
            assertEquals(user.getUsername(), u.getUsername());
            assertEquals(user.getEmail(), u.getEmail());
            assertEquals(user.getNickname(), u.getNickname());
            assertNotNull(u.getId());
            assertNull(u.getSalt());
            assertNotNull(u.getCreateAt());
            assertNull(u.getPhone());
            assertNotNull(u.getLastUpdateAt());
            assertTrue(u.isActive());
        });
    }

    @Test
    public void testUpdateUserSimple() {
        UserAccountInfo userAccountInfo = new UserAccountInfo();
        userAccountInfo.setUsername("test4");
        userAccountInfo.setNickname("Teset4");
        userAccountInfo.setEmail("test4@test.com");
        this.createUser(userAccountInfo);

        final List<User> currentUser = new ArrayList<>();
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(1).value(p -> {
            User user = p.get(0);
            currentUser.add(user);
            assertNotNull(user);
            assertEquals(userAccountInfo.getUsername(), user.getUsername());
        });


        UserAccountInfo userAccountInfo2 = new UserAccountInfo();
        userAccountInfo2.setUsername("test4_update");
        userAccountInfo2.setNickname("Teset4_update");
        userAccountInfo2.setEmail("test4_update@test.com");

        client.put().uri("users/{id}", currentUser.get(0).getId()).syncBody(userAccountInfo2).exchange()
                .expectStatus().isOk().expectBody(User.class).value(user -> {
            assertNotNull(user);
            assertEquals(userAccountInfo2.getUsername(), user.getUsername());
            assertEquals(userAccountInfo2.getNickname(), user.getNickname());
            assertEquals(userAccountInfo2.getEmail(), user.getEmail());

            assertEquals(currentUser.get(0).getCreateAt(), user.getCreateAt());
            assertEquals(currentUser.get(0).getId(), user.getId());
            assertEquals(currentUser.get(0).getSalt(), user.getSalt());

            assertTrue(currentUser.get(0).getLastUpdateAt().getTime() < user.getLastUpdateAt().getTime());
        });
    }

    @Test
    public void testDeleteUserSimple() {
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(0);

        UserAccountInfo userAccountInfo = new UserAccountInfo();
        userAccountInfo.setUsername("test5");
        userAccountInfo.setNickname("Teset5");
        userAccountInfo.setEmail("test5@test.com");

        final List<User> currentUser = new ArrayList<>();
        this.createUser(userAccountInfo);
        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(1).value(p -> {
            User user = p.get(0);
            assertNotNull(user);
            assertEquals(userAccountInfo.getUsername(), user.getUsername());
            currentUser.add(user);
        });

        client.delete().uri("users/{id}", currentUser.get(0).getId()).exchange()
                .expectStatus().isNoContent();

        client.get().uri("users").exchange()
                .expectStatus().isOk().expectBodyList(User.class).hasSize(0);

        client.get().uri("users/{id}", currentUser.get(0).getId()).exchange()
                .expectStatus().isNotFound();
    }
}