package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;
    private Set<Long> likes = new HashSet<>();

    @JsonProperty("duration")
    public long getDurationMinutes() {
        return duration.toMinutes();
    }

    @JsonProperty("duration")
    public void setDurationMinutes(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }
}
