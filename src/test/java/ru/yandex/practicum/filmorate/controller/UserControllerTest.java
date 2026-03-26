package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController controller;

    @BeforeEach
    void setUp() {
        controller = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void addValidUserTest() {
        User user = createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1));
        controller.addUser(user);
        assertEquals(1, controller.getUsers().size());  //заодно и Get проверяем
    }

    @Test
    void addUserWithInvalidEmailTest() {
        User user = createUser(1L, "valid-email.test", "login", "name",
                LocalDate.of(2010, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    void addUserWithInvalidLoginTest() {
        User user = createUser(1L, "valid@email.test", "", "name",
                LocalDate.of(2010, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(user));
    }

    @Test
    void updateValidUserTest() {
        User user = createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1));
        User userUpdate = createUser(1L, "new@email.test", "newLogin", "newName",
                LocalDate.of(2011, 1, 1));
        controller.addUser(user);
        controller.updateUser(userUpdate);
        Optional<User> findUser = Optional.of(controller.getUsers().stream()
                .filter(u -> u.getId().equals(1L)).findFirst().get());
        user = findUser.get();
        assertEquals(1, controller.getUsers().size());
        assertEquals(1L, user.getId());
        assertEquals("new@email.test", user.getEmail());
        assertEquals("newLogin", user.getLogin());
        assertEquals("newName", user.getName());
        assertEquals(LocalDate.of(2011, 1, 1), user.getBirthday());
    }

    @Test
    void getUsersTest() {
        controller.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addUser(createUser(2L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addUser(createUser(3L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        assertEquals(3, controller.getUsers().size());
    }

    @Test
    void getUserByIdTest() {
        controller.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        assertEquals(1L, controller.getUser(1L).get().getId());
    }

    @Test
    void getUserFriendsTest() {
        controller.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addUser(createUser(2L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addFriend(1L, 2L);
        assertEquals(1, controller.getUser(1L).get().getFriends().size());
        assertEquals(1, controller.getUser(2L).get().getFriends().size());
        assertTrue(controller.getUser(1L).get().getFriends().contains(2L));
        assertTrue(controller.getUser(2L).get().getFriends().contains(1L));
    }

    @Test
    void deleteFriendTest() {
        controller.addUser(createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addUser(createUser(2L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1)));
        controller.addFriend(1L, 2L);
        assertTrue(controller.getUser(1L).get().getFriends().contains(2L));
        assertTrue(controller.getUser(2L).get().getFriends().contains(1L));
        controller.deleteFriend(1L, 2L);
        assertFalse(controller.getUser(1L).get().getFriends().contains(2L));
        assertFalse(controller.getUser(2L).get().getFriends().contains(1L));
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
