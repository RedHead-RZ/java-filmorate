package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmServiceTest {

    private FilmService filmService;
    private UserStorage userStorage;

    @BeforeEach
    public void setUp() {
        userStorage = new InMemoryUserStorage();
        filmService = new FilmService(new InMemoryFilmStorage(), userStorage);
    }

    @Test
    void getFilmTest() {
        filmService.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        filmService.addFilm(createFilm(2L, "Name-2", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        filmService.addFilm(createFilm(3L, "Name-3", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        assertEquals(3, filmService.getFilms().size());
    }

    @Test
    void getFilmByIdTest() {
        filmService.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        assertEquals(1L, filmService.getFilmById(1L).get().getId());
    }

    @Test
    void getPopularFilmsTest() {
        filmService.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        filmService.addFilm(createFilm(2L, "Name-1", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        filmService.addFilm(createFilm(3L, "Name-2", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        assertEquals(2, filmService.getPopularFilms(2L).size());
    }

    @Test
    void updateFilmTest() {
        filmService.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        filmService.updateFilm(createFilm(1L, "OtherName", "OtherDescription",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        Film film = filmService.getFilmById(1L).get();
        assertEquals(1, filmService.getFilms().size());
        assertEquals(1L, film.getId());
        assertEquals("OtherName", film.getName());
        assertEquals("OtherDescription", film.getDescription());
    }

    @Test
    void likeFilmTest() {
        filmService.addFilm(createFilm(1L, "Name", "Description",
                LocalDate.of(1986, 1, 1), Duration.ofMinutes(124)));
        userStorage.addUser(createUser(10L, "gg@gg.com", "login", "name",
                LocalDate.of(1995, 2, 5)));
        filmService.likeFilm(1L, 10L);
        Film film = filmService.getFilmById(1L).get();
        assertEquals(1, film.getLikes().size());
        filmService.deleteLikeFilm(1L, 10L);
        assertEquals(0, filmService.getFilmById(1L).get().getLikes().size());
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
