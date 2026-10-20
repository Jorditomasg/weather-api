package es.asv.weather.forecast.infrastructure;

import es.asv.weather.forecast.application.GetTomorrowForecast;
import es.asv.weather.forecast.domain.ForecastRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Duration;
import java.time.ZoneId;

@Configuration
class ForecastConfig {

    @Bean
    GetTomorrowForecast getTomorrowForecast(ForecastRepository forecastRepository,
                                            @Value("${forecast.time-zone}") ZoneId timeZone,
                                            @Value("${forecast.cache.ttl}") Duration cacheTtl) {
        return new GetTomorrowForecast(new CachingForecastRepository(forecastRepository, cacheTtl), Clock.system(timeZone));
    }
}
