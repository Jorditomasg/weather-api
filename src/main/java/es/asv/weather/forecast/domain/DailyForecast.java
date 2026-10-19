package es.asv.weather.forecast.domain;

import java.time.LocalDate;
import java.util.List;

/**
 * Forecast of a municipality for one day. Temperatures are stored in Celsius, as AEMET publishes them;
 * conversion to other units happens when they are read.
 */
public record DailyForecast(
        LocalDate date,
        int maxTemperatureCelsius,
        int minTemperatureCelsius,
        List<PrecipitationProbability> precipitationProbabilities) {

    public DailyForecast {
        precipitationProbabilities = List.copyOf(precipitationProbabilities);
    }

    // AEMET does not publish a daily mean: the midrange is the usual meteorological criterion
    public double averageTemperature(TemperatureUnit unit) {
        double averageCelsius = (maxTemperatureCelsius + minTemperatureCelsius) / 2.0;
        return Math.round(unit.fromCelsius(averageCelsius) * 10) / 10.0;
    }
}
