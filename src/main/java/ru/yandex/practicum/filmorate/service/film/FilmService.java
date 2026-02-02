package ru.yandex.practicum.filmorate.service.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.SoughtObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Optional<Film> getFilmById(Long id) {
        return Optional.ofNullable(filmStorage.getFilm(id))
                .or(() -> {
                    throw new SoughtObjectNotFoundException("Фильм не найден");
                });
    }

    public Collection<Film> getPopularFilms(Long count) {
        return filmStorage.getFilms().stream()
                .sorted((f1, f2) -> Long.compare(f2.getLikes().size(), f1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        film.setId(getNextId());
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (film.getId() == null) {
            throw new SoughtObjectNotFoundException("Фильм не найден");
        }
        return filmStorage.updateFilm(film);
    }

    public Film likeFilm(Long filmId, Long userId) {
        Optional<Film> film = getFilmById(filmId);
        User user = userStorage.getUser(userId);
        if (film.isPresent() && user != null) {
            film.get().getLikes().add(user.getId());
        } else {
            throw new SoughtObjectNotFoundException("Некорректный идентификатор фильма или пользователя");
        }
        return film.orElse(null);
    }

    public void removeFilm(Long id) {
        if (filmStorage.getFilm(id) != null) filmStorage.removeFilm(id);
        else throw new SoughtObjectNotFoundException("Некорректный идентификатор фильма");
    }

    public void deleteLikeFilm(Long filmId, Long userId) {
        Optional<Film> film = getFilmById(filmId);
        User user = userStorage.getUser(userId);
        if (film.isPresent() && user != null) {
            film.get().getLikes().remove(user.getId());
        } else {
            throw new SoughtObjectNotFoundException("Некорректный идентификатор фильма или пользователя");
        }
    }

    private void validateFilm(Film film) {
        if (film.getName().isEmpty()) throw new ValidationException("Необходимо заполнить название фильма");
        if (film.getDescription().length() > 200) throw new ValidationException("Описание фильма не должно "
                + "превышать 200 символов");
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28)))
            throw new ValidationException("На момент этой даты не было ни одного фильма =)");
        if (film.getDuration().isNegative()) throw new ValidationException("Дружочек, у тебя длина "
                + "фильма отрицательная");
    }

    private long getNextId() {
        long currentMaxId = filmStorage.getFilms()
                .stream()
                .mapToLong(Film::getId)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
