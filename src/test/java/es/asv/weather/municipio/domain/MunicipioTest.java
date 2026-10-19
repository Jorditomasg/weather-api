package es.asv.weather.municipio.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MunicipioTest {

    private final Municipio agost = new Municipio("03002", "Agost");

    @Test
    void nombreEmpiezaPorPrefijoSinDistinguirMayusculas() {
        assertThat(agost.nombreEmpiezaPor("ag")).isTrue();
        assertThat(agost.nombreEmpiezaPor("AG")).isTrue();
    }

    @Test
    void nombreEmpiezaPorPrefijoSinDistinguirTildes() {
        Municipio avila = new Municipio("05019", "Ávila");

        assertThat(avila.nombreEmpiezaPor("avi")).isTrue();
        assertThat(agost.nombreEmpiezaPor("Ág")).isTrue();
    }

    @Test
    void noCoincideSiElPrefijoNoEstaAlPrincipio() {
        assertThat(agost.nombreEmpiezaPor("gost")).isFalse();
    }

    @Test
    void noPermiteCodigoNiNombreVacios() {
        assertThatThrownBy(() -> new Municipio(" ", "Agost")).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Municipio("03002", null)).isInstanceOf(IllegalArgumentException.class);
    }
}
