package es.asv.weather.forecast.infrastructure.web;

import es.asv.weather.forecast.application.GetTomorrowForecast;
import es.asv.weather.forecast.domain.ForecastNotFoundException;
import es.asv.weather.forecast.domain.TemperatureUnit;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ForecastController {

    private final GetTomorrowForecast getTomorrowForecast;

    ForecastController(GetTomorrowForecast getTomorrowForecast) {
        this.getTomorrowForecast = getTomorrowForecast;
    }

    @Operation(summary = "Tomorrow's forecast for a municipality")
    @GetMapping("/api/municipalities/{code}/forecast")
    ForecastResponse get(
            @Parameter(example = "03002") @PathVariable @Pattern(regexp = "\\d{5}") String code,
            @Parameter(schema = @Schema(allowableValues = {"G_CEL", "G_FAH"}))
            @RequestParam(defaultValue = "G_CEL") TemperatureUnit unit) {
        return ForecastResponse.from(getTomorrowForecast.get(code), unit);
    }

    @ExceptionHandler(ForecastNotFoundException.class)
    ProblemDetail forecastNotFound(ForecastNotFoundException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, e.getMessage());
    }
}
