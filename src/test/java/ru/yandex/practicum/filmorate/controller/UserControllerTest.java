package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private LocalValidatorFactoryBean validator;
    private UserController controller;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        controller = new UserController();
    }

    @Test
    void validUserTest() {
        User user = createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidUserEmailTest() {
        User user = createUser(1L, "invalid-test", "login", "name",
                LocalDate.of(2010, 1, 1));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("email", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void invalidUserNameTest() {
        User user = createUser(1L, "valid@email.test", "", "name",
                LocalDate.of(2010, 1, 1));

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
        assertEquals("login", violations.iterator().next().getPropertyPath().toString());
    }

    @Test
    void addValidUserTest() {
        User user = createUser(1L, "valid@email.test", "login", "name",
                LocalDate.of(2010, 1, 1));
        controller.addUser(user);
        assertEquals(1, controller.getUsers().size());  //заодно и Get проверяем
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
