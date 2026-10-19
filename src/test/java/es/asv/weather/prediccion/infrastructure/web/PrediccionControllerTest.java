package es.asv.weather.prediccion.infrastructure.web;

import es.asv.weather.prediccion.application.ObtenerPrediccionManana;
import es.asv.weather.prediccion.domain.PrediccionDiaria;
import es.asv.weather.prediccion.domain.PrediccionNoEncontradaException;
import es.asv.weather.prediccion.domain.ProbabilidadPrecipitacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(PrediccionController.class)
class PrediccionControllerTest {

    private static final PrediccionDiaria PREDICCION = new PrediccionDiaria(LocalDate.of(2026, 10, 20), 26, 14,
            List.of(new ProbabilidadPrecipitacion(25, "00-06"), new ProbabilidadPrecipitacion(0, "06-12")));

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private ObtenerPrediccionManana obtenerPrediccionManana;

    @Test
    void devuelveLaPrediccionEnCelsiusPorDefecto() {
        given(obtenerPrediccionManana.obtener("03002")).willReturn(PREDICCION);

        assertThat(mvc.get().uri("/api/municipios/03002/prediccion"))
                .hasStatusOk()
                .bodyJson().isEqualTo("""
                        {
                          "mediaTemperatura": 20.0,
                          "unidadTemperatura": "G_CEL",
                          "probPrecipitacion": [
                            {"probabilidad": 25, "periodo": "00-06"},
                            {"probabilidad": 0, "periodo": "06-12"}
                          ]
                        }
                        """);
    }

    @Test
    void devuelveLaPrediccionEnLaUnidadPedida() {
        given(obtenerPrediccionManana.obtener("03002")).willReturn(PREDICCION);

        assertThat(mvc.get().uri("/api/municipios/03002/prediccion?unidad=G_FAH"))
                .hasStatusOk()
                .bodyJson().extractingPath("$.mediaTemperatura").isEqualTo(68.0);
    }

    @Test
    void respondeBadRequestConUnidadDesconocida() {
        assertThat(mvc.get().uri("/api/municipios/03002/prediccion?unidad=G_KEL")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void respondeBadRequestConCodigoMalFormado() {
        assertThat(mvc.get().uri("/api/municipios/abc/prediccion")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void respondeNotFoundSiNoHayPrediccion() {
        given(obtenerPrediccionManana.obtener("03002"))
                .willThrow(new PrediccionNoEncontradaException("03002", LocalDate.of(2026, 10, 20)));

        assertThat(mvc.get().uri("/api/municipios/03002/prediccion")).hasStatus(HttpStatus.NOT_FOUND);
    }
}
