package es.asv.weather.forecast.domain;

import java.time.LocalDate;
import java.util.Optional;

public interface ForecastRepository {

    Optional<DailyForecast> findByMunicipalityAndDate(String municipalityCode, LocalDate date);
}
