package es.asv.weather.prediccion.application;

import es.asv.weather.prediccion.domain.PrediccionDiaria;
import es.asv.weather.prediccion.domain.PrediccionNoEncontradaException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ObtenerPrediccionMananaTest {

    private static final ZoneId MADRID = ZoneId.of("Europe/Madrid");
    private final PrediccionDiaria prediccionDel20 = new PrediccionDiaria(LocalDate.of(2026, 10, 20), 26, 14, List.of());

    @Test
    void pideLaPrediccionDelDiaSiguiente() {
        Clock dia19 = Clock.fixed(Instant.parse("2026-10-19T10:00:00Z"), MADRID);
        var obtener = new ObtenerPrediccionManana(
                (codigo, fecha) -> fecha.equals(LocalDate.of(2026, 10, 20)) ? Optional.of(prediccionDel20) : Optional.empty(),
                dia19);

        assertThat(obtener.obtener("03002")).isEqualTo(prediccionDel20);
    }

    @Test
    void calculaMananaEnLaZonaHorariaConfiguradaYNoEnUtc() {
        // 23:30 UTC del día 18 ya es día 19 en Madrid, así que "mañana" es el 20
        Clock casiMedianocheUtc = Clock.fixed(Instant.parse("2026-10-18T23:30:00Z"), MADRID);
        var obtener = new ObtenerPrediccionManana(
                (codigo, fecha) -> fecha.equals(LocalDate.of(2026, 10, 20)) ? Optional.of(prediccionDel20) : Optional.empty(),
                casiMedianocheUtc);

        assertThat(obtener.obtener("03002")).isEqualTo(prediccionDel20);
    }

    @Test
    void lanzaExcepcionSiNoHayPrediccionParaManana() {
        Clock dia19 = Clock.fixed(Instant.parse("2026-10-19T10:00:00Z"), MADRID);
        var obtener = new ObtenerPrediccionManana((codigo, fecha) -> Optional.empty(), dia19);

        assertThatThrownBy(() -> obtener.obtener("03002")).isInstanceOf(PrediccionNoEncontradaException.class);
    }
}
