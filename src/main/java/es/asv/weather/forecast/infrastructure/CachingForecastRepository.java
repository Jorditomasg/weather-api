package es.asv.weather.forecast.infrastructure;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Ticker;
import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * AEMET limits requests per minute and only updates forecasts a few times a day, so each forecast is kept:
 * repeated lookups and unit changes never reach AEMET, and concurrent requests for the same municipality share
 * a single call. After {@code refreshAfter} the stale forecast keeps being served while a new one loads in the
 * background; if AEMET fails, the stale one stays (the key includes the date, so it is never served for another day).
 * A missing forecast is not cached, so it shows up as soon as AEMET publishes it.
 */
class CachingForecastRepository implements ForecastRepository {

    // One entry per municipality and day; Spain has about 8,100 municipalities
    private static final long MAX_ENTRIES = 10_000;

    private final LoadingCache<Key, DailyForecast> cache;

    CachingForecastRepository(ForecastRepository delegate, Duration refreshAfter) {
        this(delegate, refreshAfter, Ticker.systemTicker(), ForkJoinPool.commonPool());
    }

    CachingForecastRepository(ForecastRepository delegate, Duration refreshAfter, Ticker ticker, Executor executor) {
        this.cache = Caffeine.newBuilder()
                .refreshAfterWrite(refreshAfter)
                .maximumSize(MAX_ENTRIES)
                .ticker(ticker)
                .executor(executor)
                .build(key -> delegate.findByMunicipalityAndDate(key.municipalityCode(), key.date()).orElse(null));
    }

    @Override
    public Optional<DailyForecast> findByMunicipalityAndDate(String municipalityCode, LocalDate date) {
        return Optional.ofNullable(cache.get(new Key(municipalityCode, date)));
    }

    private record Key(String municipalityCode, LocalDate date) {
    }
}
