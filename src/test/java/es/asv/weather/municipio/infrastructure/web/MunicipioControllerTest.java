package es.asv.weather.municipio.infrastructure.web;

import es.asv.weather.municipio.application.BuscarMunicipios;
import es.asv.weather.municipio.domain.Municipio;
import es.asv.weather.shared.infrastructure.aemet.AemetException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(MunicipioController.class)
class MunicipioControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private BuscarMunicipios buscarMunicipios;

    @Test
    void devuelveLosMunicipiosConCodigoYNombre() {
        given(buscarMunicipios.buscar("Ag")).willReturn(List.of(new Municipio("03002", "Agost")));

        assertThat(mvc.get().uri("/api/municipios?nombre=Ag"))
                .hasStatusOk()
                .bodyJson().isEqualTo("""
                        [{"codigo": "03002", "nombre": "Agost"}]
                        """);
    }

    @Test
    void respondeBadRequestSinNombre() {
        assertThat(mvc.get().uri("/api/municipios?nombre=")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void respondeBadGatewaySiAemetFalla() {
        given(buscarMunicipios.buscar("Ag")).willThrow(new AemetException("caído"));

        assertThat(mvc.get().uri("/api/municipios?nombre=Ag")).hasStatus(HttpStatus.BAD_GATEWAY);
    }
}
