package es.asv.weather.forecast.domain;

import java.time.LocalDate;

public class ForecastNotFoundException extends RuntimeException {

    public ForecastNotFoundException(String municipalityCode, LocalDate date) {
        super("No forecast for municipality " + municipalityCode + " on " + date);
    }
}
