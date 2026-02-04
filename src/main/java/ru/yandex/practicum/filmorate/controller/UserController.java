package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.SoughtObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    //получаем всех пользователей
    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsers();
    }

    //получаем пользователь по id
    @GetMapping("/{userId}")
    public Optional<User> getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    //получаем всех друзей пользователя
    @GetMapping("/{id}/friends")
    public Collection<User> getFriends(@PathVariable Long id) {
        return userService.getUserFriends(id);
    }

    //получаем пересекающихся друзей
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> getFriendsCommon(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.getFriendsCommon(id, otherId);
    }

    //добавляем пользователя
    @PostMapping
    public User addUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    //изменяем пользователя
    @PutMapping
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    //добавляем друга пользователю
    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        return userService.addFriend(id, friendId);
    }

    //удаляем пользователя по id
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }

    //удаляем друга у пользователя
    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private Map<String, String> objectNotFound(final SoughtObjectNotFoundException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    private Map<String, String> handleRuntimeException(final RuntimeException e) {
        log.error(e.getMessage(), e);
        return Map.of("error", "Что-то пошло не по плану");
    }
}
