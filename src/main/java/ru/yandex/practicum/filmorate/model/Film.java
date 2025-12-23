package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    @JsonProperty("duration")
    public long getDurationMinutes() {
        return duration.toMinutes();
    }

    @JsonProperty("duration")
    public void setDurationMinutes(long minutes) {
        this.duration = Duration.ofMinutes(minutes);
    }
}
