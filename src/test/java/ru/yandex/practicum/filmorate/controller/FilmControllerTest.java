package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private LocalValidatorFactoryBean validator;
    private FilmController controller;

    @BeforeEach
    void setUp() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();
        controller = new FilmController();
    }

    @Test
    void validFilmTest() {
        Film film = createFilm(1L, "Title", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());
    }

    @Test
    void invalidFilmTest() {
        Film film = createFilm(1L, "", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124));
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
        assertEquals("name", violations.iterator().next().getPropertyPath().toString());
    }

    //

    @Test
    void addValidFilmTest() {
        Film film = createFilm(1L, "Title", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124));
        controller.addFilm(film);
        assertEquals(1, controller.getFilms().size()); //заодно и Get проверяем
    }

    @Test
    void updateValidFilmTest() {
        Film film = createFilm(1L, "Title", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124));
        Film filmUpdate = createFilm(1L, "newTitle", "newDescription",
                LocalDate.of(2000, 1, 1), Duration.ofMinutes(98));
        controller.addFilm(film);
        controller.updateFilm(filmUpdate);
        Optional<Film> findFilm = Optional.of(controller.getFilms().stream()
                .filter(f -> f.getId().equals(1L)).findFirst().get());
        film = findFilm.get();
        assertEquals(1, controller.getFilms().size());
        assertEquals(1L, film.getId());
        assertEquals("newTitle", film.getName());
        assertEquals("newDescription", film.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), film.getReleaseDate());
        assertEquals(Duration.ofMinutes(98), film.getDuration());
    }

    //

    private Film createFilm(Long id, String name, String description, LocalDate releaseDate, Duration duration) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }
}
