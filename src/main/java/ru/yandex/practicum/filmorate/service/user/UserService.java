package ru.yandex.practicum.filmorate.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SoughtObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(userStorage.getUser(id))
                .or(() -> {
                    throw new SoughtObjectNotFoundException("Пользователя с таким Id не сущесвтует");
                });
    }

    public Collection<User> getUsers() {
        return userStorage.getUsers();
    }

    public Collection<User> getUserFriends(Long id) {
        return userStorage.getUser(id).getFriends().stream()
                .map(userStorage::getUser).collect(Collectors.toSet());
    }

    public Collection<User> getFriendsCommon(Long userId, Long otherUserId) {
        return userStorage.getUser(userId).getFriends().stream()
                .filter(userStorage.getUser(otherUserId).getFriends()::contains)
                .map(userStorage::getUser).collect(Collectors.toSet());
    }

    public User addUser(User user) {
        validateUser(user);
        user.setId(getNextId());
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        getUserById(user.getId());
        return userStorage.updateUser(user);
    }

    public User addFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User friend = userStorage.getUser(friendId);
        if (user != null && friend != null) {
            user.getFriends().add(friendId);
            friend.getFriends().add(id);
        } else
            throw new SoughtObjectNotFoundException("Не найден пользователь(и) с указаным идентификатором");
        return user;
    }

    public void removeUser(Long id) {
        if (userStorage.getUser(id) != null) {
            userStorage.removeUser(id);
        } else
            throw new SoughtObjectNotFoundException("Не найден пользователь с указанным идентификатором");
    }

    public void deleteFriend(Long id, Long friendId) {
        if (userStorage.getUser(id) == null || userStorage.getUser(friendId) == null) {
            throw new SoughtObjectNotFoundException("Не найден пользователь(и) с указаным идентификатором");
        }
        userStorage.getUser(id).getFriends().remove(friendId);
        userStorage.getUser(friendId).getFriends().remove(id);
    }

    private void validateUser(User user) {
        if (user.getLogin().isEmpty() || user.getLogin().contains(" "))
            throw new ValidationException("Некорректный логин");
        if (user.getBirthday().isAfter(LocalDate.now()))
            throw new ValidationException("Ты еще не родился. Попробуй попозже");
        if (user.getName() == null || user.getName().isEmpty()) user.setName(user.getLogin());
        Pattern pattern = Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@" + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$");
        if (user.getEmail() == null || user.getEmail().isEmpty() || !pattern.matcher(user.getEmail()).matches())
            throw new ValidationException("Некорректный формат E-mail");
    }

    private long getNextId() {
        long currentMaxId = userStorage.getUsers().stream().mapToLong(User::getId).max().orElse(0);
        return ++currentMaxId;
    }
}
