package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User getUser(Long id);

    Collection<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    void removeUser(Long id);
}
