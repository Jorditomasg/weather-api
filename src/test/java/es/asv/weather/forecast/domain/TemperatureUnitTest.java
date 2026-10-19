package es.asv.weather.forecast.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TemperatureUnitTest {

    @Test
    void resolvesUnitFromContractCode() {
        assertThat(TemperatureUnit.fromCode("G_CEL")).isEqualTo(TemperatureUnit.CELSIUS);
        assertThat(TemperatureUnit.fromCode("G_FAH")).isEqualTo(TemperatureUnit.FAHRENHEIT);
    }

    @Test
    void rejectsUnknownCode() {
        assertThatThrownBy(() -> TemperatureUnit.fromCode("CELSIUS")).isInstanceOf(IllegalArgumentException.class);
    }
}
