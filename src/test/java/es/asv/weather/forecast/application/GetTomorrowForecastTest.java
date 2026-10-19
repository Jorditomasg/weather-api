package es.asv.weather.forecast.application;

import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastNotFoundException;
import es.asv.weather.forecast.domain.ForecastRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GetTomorrowForecastTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");
    private final DailyForecast forecastFor20th = new DailyForecast(LocalDate.of(2026, 10, 20), 26, 14, List.of());
    private final ForecastRepository onlyHasThe20th =
            (code, date) -> date.equals(forecastFor20th.date()) ? Optional.of(forecastFor20th) : Optional.empty();

    @Test
    void requestsTheNextDayForecast() {
        Clock on19th = Clock.fixed(Instant.parse("2026-10-19T10:00:00Z"), MADRID);

        assertThat(new GetTomorrowForecast(onlyHasThe20th, on19th).get("03002")).isEqualTo(forecastFor20th);
    }

    @Test
    void computesTomorrowInConfiguredTimeZoneNotUtc() {
        // 23:30 UTC on the 18th is already the 19th in Madrid, so tomorrow is the 20th
        Clock almostMidnightUtc = Clock.fixed(Instant.parse("2026-10-18T23:30:00Z"), MADRID);

        assertThat(new GetTomorrowForecast(onlyHasThe20th, almostMidnightUtc).get("03002")).isEqualTo(forecastFor20th);
    }

    @Test
    void throwsWhenThereIsNoForecastForTomorrow() {
        Clock on19th = Clock.fixed(Instant.parse("2026-10-19T10:00:00Z"), MADRID);
        var getTomorrowForecast = new GetTomorrowForecast((code, date) -> Optional.empty(), on19th);

        assertThatThrownBy(() -> getTomorrowForecast.get("03002")).isInstanceOf(ForecastNotFoundException.class);
    }
}
