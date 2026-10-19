package es.asv.weather.forecast.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DailyForecastTest {

    private static DailyForecast withTemperatures(int max, int min) {
        return new DailyForecast(LocalDate.of(2026, 10, 20), max, min, List.of());
    }

    @Test
    void averageInCelsiusIsMidpointOfMaxAndMin() {
        assertThat(withTemperatures(26, 14).averageTemperature(TemperatureUnit.CELSIUS)).isEqualTo(20.0);
    }

    @Test
    void averageInFahrenheitConvertsCelsiusAverage() {
        assertThat(withTemperatures(26, 14).averageTemperature(TemperatureUnit.FAHRENHEIT)).isEqualTo(68.0);
    }

    @Test
    void averageIsRoundedToOneDecimal() {
        // 19.5 ºC = 67.1 ºF
        assertThat(withTemperatures(25, 14).averageTemperature(TemperatureUnit.FAHRENHEIT)).isEqualTo(67.1);
    }
}
