package es.asv.weather.prediccion.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PrediccionDiariaTest {

    private static PrediccionDiaria conTemperaturas(int maxima, int minima) {
        return new PrediccionDiaria(LocalDate.of(2026, 10, 20), maxima, minima, List.of());
    }

    @Test
    void mediaEnCelsiusEsLaMediaDeMaximaYMinima() {
        assertThat(conTemperaturas(26, 14).mediaTemperatura(UnidadTemperatura.G_CEL)).isEqualTo(20.0);
    }

    @Test
    void mediaEnFahrenheitConvierteLaMediaEnCelsius() {
        assertThat(conTemperaturas(26, 14).mediaTemperatura(UnidadTemperatura.G_FAH)).isEqualTo(68.0);
    }

    @Test
    void mediaSeRedondeaAUnDecimal() {
        // 19.5 ºC = 67.1 ºF
        assertThat(conTemperaturas(25, 14).mediaTemperatura(UnidadTemperatura.G_FAH)).isEqualTo(67.1);
    }
}
