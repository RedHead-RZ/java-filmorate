package ru.yandex.practicum.filmorate.service.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(new InMemoryUserStorage());
    }

    @Test
    void getUserById() {
        userService.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        assertEquals(1L, userService.getUserById(1L).get().getId());
    }

    @Test
    void getUsersTest() {
        userService.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addUser(createUser(2L, "valid2@email.test", "login2", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addUser(createUser(3L, "valid3@email.test", "login3", "name",
                LocalDate.of(2010, 1, 1)));
        assertEquals(3, userService.getUsers().size());
    }

    @Test
    void updateUserTest() {
        userService.addUser(createUser(1L, "valid3@email.test", "login3", "name",
                LocalDate.of(2010, 1, 1)));
        userService.updateUser(createUser(1L, "other@email.test", "OtherLogin3", "OtherName",
                LocalDate.of(2010, 1, 1)));
        User user = userService.getUserById(1L).get();
        assertEquals("OtherLogin3", user.getLogin());
        assertEquals("OtherName", user.getName());
        assertEquals("other@email.test", user.getEmail());
    }

    @Test
    void addFriendTest() {
        userService.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addUser(createUser(2L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addFriend(1L, 2L);
        assertEquals(1, userService.getUserById(1L).get().getFriends().size());
        assertEquals(1, userService.getUserById(2L).get().getFriends().size());
        assertTrue(userService.getUserById(1L).get().getFriends().contains(2L));
        assertTrue(userService.getUserById(2L).get().getFriends().contains(1L));
    }

    @Test
    void deleteFriendTest() {
        userService.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addUser(createUser(2L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        userService.addFriend(1L, 2L);
        assertTrue(userService.getUserById(1L).get().getFriends().contains(2L));
        assertTrue(userService.getUserById(2L).get().getFriends().contains(1L));
        userService.deleteFriend(1L, 2L);
        assertFalse(userService.getUserById(1L).get().getFriends().contains(2L));
        assertFalse(userService.getUserById(2L).get().getFriends().contains(1L));
    }

    private User createUser(Long id, String email, String login, String name, LocalDate birthday) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthday);
        return user;
    }
}
