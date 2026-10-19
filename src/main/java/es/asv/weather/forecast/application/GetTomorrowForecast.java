package es.asv.weather.forecast.application;

import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastNotFoundException;
import es.asv.weather.forecast.domain.ForecastRepository;

import java.time.Clock;
import java.time.LocalDate;

public class GetTomorrowForecast {

    private final ForecastRepository forecastRepository;
    private final Clock clock;

    // Clock is injected so "tomorrow" is testable and depends on the configured time zone
    public GetTomorrowForecast(ForecastRepository forecastRepository, Clock clock) {
        this.forecastRepository = forecastRepository;
        this.clock = clock;
    }

    public DailyForecast get(String municipalityCode) {
        LocalDate tomorrow = LocalDate.now(clock).plusDays(1);
        return forecastRepository.findByMunicipalityAndDate(municipalityCode, tomorrow)
                .orElseThrow(() -> new ForecastNotFoundException(municipalityCode, tomorrow));
    }
}
