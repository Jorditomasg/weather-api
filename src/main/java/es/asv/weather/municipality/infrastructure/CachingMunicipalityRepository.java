package es.asv.weather.municipality.infrastructure;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Ticker;
import es.asv.weather.municipality.domain.Municipality;
import es.asv.weather.municipality.domain.MunicipalityRepository;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * The municipality list barely changes and AEMET takes seconds to serve it, so it is kept in memory.
 * After {@code refreshAfter} the stale list keeps being served while a new one loads in the background;
 * if AEMET fails, the stale list stays (there is deliberately no expiry).
 */
class CachingMunicipalityRepository implements MunicipalityRepository {

    // AEMET returns the whole list in one call, so the cache holds a single entry
    private static final String ALL = "all";

    private final LoadingCache<String, List<Municipality>> cache;

    CachingMunicipalityRepository(MunicipalityRepository delegate, Duration refreshAfter) {
        this(delegate, refreshAfter, Ticker.systemTicker(), ForkJoinPool.commonPool());
    }

    CachingMunicipalityRepository(MunicipalityRepository delegate, Duration refreshAfter, Ticker ticker, Executor executor) {
        this.cache = Caffeine.newBuilder()
                .refreshAfterWrite(refreshAfter)
                .ticker(ticker)
                .executor(executor)
                .build(key -> delegate.findAll());
    }

    /** Loads the list in the background so the first user does not wait for AEMET. */
    void warmUp() {
        cache.refresh(ALL);
    }

    @Override
    public List<Municipality> findAll() {
        return cache.get(ALL);
    }
}
