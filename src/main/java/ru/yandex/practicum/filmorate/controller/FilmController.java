package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validateFilm(film);
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (film.getId() == null) throw new ValidationException("Некорректный идентификатор фильма");
        if (!films.containsKey(film.getId())) throw new ValidationException("Несуществующий фильм");
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
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
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
