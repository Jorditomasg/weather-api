package es.asv.weather.municipio.application;

import es.asv.weather.municipio.domain.Municipio;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BuscarMunicipiosTest {

    private final Municipio agres = new Municipio("03003", "Agres");
    private final Municipio abades = new Municipio("40001", "Abades");
    private final Municipio agost = new Municipio("03002", "Agost");

    private final BuscarMunicipios buscarMunicipios = new BuscarMunicipios(() -> List.of(agres, abades, agost));

    @Test
    void devuelveSoloLosMunicipiosQueEmpiezanPorElPrefijo() {
        assertThat(buscarMunicipios.buscar("Ag")).containsExactlyInAnyOrder(agres, agost);
    }

    @Test
    void devuelveLosResultadosOrdenadosPorNombre() {
        assertThat(buscarMunicipios.buscar("A")).containsExactly(abades, agost, agres);
    }

    @Test
    void devuelveListaVaciaSiNingunoCoincide() {
        assertThat(buscarMunicipios.buscar("Zz")).isEmpty();
    }

    @Test
    void noPermitePrefijoVacio() {
        assertThatThrownBy(() -> buscarMunicipios.buscar(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
