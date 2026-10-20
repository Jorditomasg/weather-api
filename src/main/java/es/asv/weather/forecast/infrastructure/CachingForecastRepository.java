package es.asv.weather.forecast.infrastructure;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.github.benmanes.caffeine.cache.Ticker;
import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Optional;

/**
 * AEMET limits requests per minute and only updates forecasts a few times a day, so each forecast is kept
 * for {@code ttl}: repeated lookups and unit changes never reach AEMET, and concurrent requests for the same
 * municipality share a single call. A missing forecast is not cached, so it shows up as soon as AEMET publishes it.
 */
class CachingForecastRepository implements ForecastRepository {

    // One entry per municipality and day; Spain has about 8,100 municipalities
    private static final long MAX_ENTRIES = 10_000;

    private final LoadingCache<Key, DailyForecast> cache;

    CachingForecastRepository(ForecastRepository delegate, Duration ttl) {
        this(delegate, ttl, Ticker.systemTicker());
    }

    CachingForecastRepository(ForecastRepository delegate, Duration ttl, Ticker ticker) {
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(MAX_ENTRIES)
                .ticker(ticker)
                .build(key -> delegate.findByMunicipalityAndDate(key.municipalityCode(), key.date()).orElse(null));
    }

    @Override
    public Optional<DailyForecast> findByMunicipalityAndDate(String municipalityCode, LocalDate date) {
        return Optional.ofNullable(cache.get(new Key(municipalityCode, date)));
    }

    private record Key(String municipalityCode, LocalDate date) {
    }
}
