package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getUsers() {
        log.info("Получаем всех пользователей");
        return users.values();
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        validateUser(user);
        user.setId(getNextId());
        log.info("Добавляем нового пользователя: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (user.getId() == null) throw new ValidationException("Некорректный идентификатор пользователя");
        if (!users.containsKey(user.getId())) throw new ValidationException("Несуществующий пользователь");
        validateUser(user);
        log.info("Изменяем данные по пользователю: {}", user);
        users.put(user.getId(), user);
        return user;
    }

    private void validateUser(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" "))
            throw new ValidationException("Некорректный логин");
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Ты еще не родился. Попробуй попозже");
        if (user.getName().isEmpty())
            user.setName(user.getLogin());
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
