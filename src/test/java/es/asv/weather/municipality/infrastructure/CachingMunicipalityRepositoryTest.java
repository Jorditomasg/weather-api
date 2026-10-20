package es.asv.weather.municipality.infrastructure;

import es.asv.weather.municipality.domain.Municipality;
import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CachingMunicipalityRepositoryTest {

    private static final Duration REFRESH_AFTER = Duration.ofHours(24);

    private final List<Municipality> oldList = List.of(new Municipality("03002", "Agost"));
    private final List<Municipality> newList = List.of(new Municipality("03002", "Agost"), new Municipality("03003", "Agres"));

    private final AtomicLong nanos = new AtomicLong();
    // each AEMET call consumes the next answer
    private final Deque<Supplier<List<Municipality>>> aemetAnswers = new ArrayDeque<>();
    private int aemetCalls;

    // same-thread executor: background refreshes finish before the next assertion
    private final CachingMunicipalityRepository repository = new CachingMunicipalityRepository(
            () -> {
                aemetCalls++;
                return aemetAnswers.removeFirst().get();
            },
            REFRESH_AFTER, nanos::get, Runnable::run);

    @Test
    void callsAemetOnlyOnceWhileFresh() {
        aemetAnswers.add(() -> oldList);

        repository.findAll();
        repository.findAll();

        assertThat(aemetCalls).isEqualTo(1);
    }

    @Test
    void reloadsListAfterRefreshPeriod() {
        aemetAnswers.add(() -> oldList);
        aemetAnswers.add(() -> newList);
        repository.findAll();

        nanos.addAndGet(REFRESH_AFTER.plusSeconds(1).toNanos());
        repository.findAll();

        assertThat(repository.findAll()).isEqualTo(newList);
    }

    @Test
    void keepsServingStaleListWhenRefreshFails() {
        aemetAnswers.add(() -> oldList);
        aemetAnswers.add(() -> {
            throw new AemetException("AEMET down");
        });
        repository.findAll();

        nanos.addAndGet(REFRESH_AFTER.plusSeconds(1).toNanos());
        repository.findAll();

        assertThat(repository.findAll()).isEqualTo(oldList);
    }

    @Test
    void propagatesFailureWhenNothingIsCachedYet() {
        aemetAnswers.add(() -> {
            throw new AemetException("AEMET down");
        });

        assertThatThrownBy(repository::findAll).isInstanceOf(AemetException.class);
    }
}
