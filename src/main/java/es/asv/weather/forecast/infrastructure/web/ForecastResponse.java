package es.asv.weather.forecast.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.PrecipitationProbability;
import es.asv.weather.forecast.domain.TemperatureUnit;

import java.util.List;

// JSON field names are fixed by the API contract
record ForecastResponse(
        @JsonProperty("mediaTemperatura") double averageTemperature,
        @JsonProperty("unidadTemperatura") String temperatureUnit,
        @JsonProperty("probPrecipitacion") List<PrecipitationProbabilityResponse> precipitationProbabilities) {

    static ForecastResponse from(DailyForecast forecast, TemperatureUnit unit) {
        return new ForecastResponse(
                forecast.averageTemperature(unit),
                unit.code(),
                forecast.precipitationProbabilities().stream().map(PrecipitationProbabilityResponse::from).toList());
    }

    record PrecipitationProbabilityResponse(
            @JsonProperty("probabilidad") int probability,
            @JsonProperty("periodo") String period) {

        static PrecipitationProbabilityResponse from(PrecipitationProbability probability) {
            return new PrecipitationProbabilityResponse(probability.probability(), probability.period());
        }
    }
}
