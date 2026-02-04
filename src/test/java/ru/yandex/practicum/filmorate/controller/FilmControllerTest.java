package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController controller;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        controller = new FilmController(new FilmService(new InMemoryFilmStorage(), userStorage));
    }

    @Test
    void addFilmWithInvalidNameTest() {
        Film film = createFilm(1L, "", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124));
        assertThrows(ValidationException.class, () -> controller.addFilm(film));
    }

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

    @Test
    void getFilmByIdTest() {
        controller.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        Film film = controller.getFilm(1L).isPresent() ? controller.getFilm(1L).get() : null;
        assertNotNull(film);
        assertEquals(1L, film.getId());
        assertEquals("Name", film.getName());
    }

    @Test
    void getFilmsTest() {
        controller.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        controller.addFilm(createFilm(2L, "Name-1", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        controller.addFilm(createFilm(3L, "Name-2", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        assertEquals(3, controller.getFilms().size());
    }

    @Test
    void getFilmsPopularTest() {
        controller.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        controller.addFilm(createFilm(2L, "Name-1", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        controller.addFilm(createFilm(3L, "Name-2", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        assertEquals(2, controller.getFilmsPopular(2L).size());
    }

    @Test
    void updateFilmTest() {
        controller.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        controller.updateFilm(createFilm(1L, "OtherName", "OtherDescription",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        Film film = controller.getFilm(1L).get();
        assertEquals(1, controller.getFilms().size());
        assertEquals(1L, film.getId());
        assertEquals("OtherName", film.getName());
        assertEquals("OtherDescription", film.getDescription());
    }

    @Test
    void likeFilmTest() {
        controller.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        userStorage.addUser(createUser(10L, "gg@gg.com", "login", "name",
                LocalDate.of(1995, 2, 5)));
        controller.likeFilm(1L, 10L);
        Film film = controller.getFilm(1L).get();
        assertEquals(1, film.getLikes().size());
        controller.deleteLikeFilm(1L, 10L);
        assertEquals(0, controller.getFilm(1L).get().getLikes().size());
    }

    private Film createFilm(Long id, String name, String description, LocalDate releaseDate, Duration duration) {
        Film film = new Film();
        film.setId(id);
        film.setName(name);
        film.setDescription(description);
        film.setReleaseDate(releaseDate);
        film.setDuration(duration);
        return film;
    }

    private User createUser(Long id, String email, String login, String name, LocalDate birthDate) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setLogin(login);
        user.setName(name);
        user.setBirthday(birthDate);
        return user;
    }
}
