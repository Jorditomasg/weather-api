package es.asv.weather.forecast.infrastructure;

import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastRepository;
import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CachingForecastRepositoryTest {

    private static final Duration TTL = Duration.ofMinutes(30);
    private static final LocalDate TOMORROW = LocalDate.of(2026, 10, 21);

    private final DailyForecast agost = new DailyForecast(TOMORROW, 25, 14, List.of());

    private final AtomicLong nanos = new AtomicLong();
    private final List<String> aemetCalls = new ArrayList<>();
    private ForecastRepository aemet = (code, date) -> {
        aemetCalls.add(code);
        return Optional.of(agost);
    };

    private final CachingForecastRepository repository =
            new CachingForecastRepository((code, date) -> aemet.findByMunicipalityAndDate(code, date), TTL, nanos::get);

    @Test
    void callsAemetOnlyOncePerMunicipalityAndDayWhileFresh() {
        repository.findByMunicipalityAndDate("03002", TOMORROW);
        repository.findByMunicipalityAndDate("03002", TOMORROW);
        repository.findByMunicipalityAndDate("03003", TOMORROW);

        assertThat(aemetCalls).containsExactly("03002", "03003");
    }

    @Test
    void callsAemetAgainOnceExpired() {
        repository.findByMunicipalityAndDate("03002", TOMORROW);

        nanos.addAndGet(TTL.plusSeconds(1).toNanos());
        repository.findByMunicipalityAndDate("03002", TOMORROW);

        assertThat(aemetCalls).hasSize(2);
    }

    @Test
    void doesNotCacheMissingForecast() {
        aemet = (code, date) -> {
            aemetCalls.add(code);
            return Optional.empty();
        };

        assertThat(repository.findByMunicipalityAndDate("03002", TOMORROW)).isEmpty();
        repository.findByMunicipalityAndDate("03002", TOMORROW);

        assertThat(aemetCalls).hasSize(2);
    }

    @Test
    void doesNotCacheFailures() {
        aemet = (code, date) -> {
            throw new AemetException("AEMET down");
        };
        assertThatThrownBy(() -> repository.findByMunicipalityAndDate("03002", TOMORROW))
                .isInstanceOf(AemetException.class);

        aemet = (code, date) -> Optional.of(agost);
        assertThat(repository.findByMunicipalityAndDate("03002", TOMORROW)).contains(agost);
    }
}
