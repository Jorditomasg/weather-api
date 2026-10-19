package es.asv.weather.forecast.infrastructure.aemet;

import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.PrecipitationProbability;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AemetForecastRepositoryTest {

    private static final Charset ISO_8859_15 = Charset.forName("ISO-8859-15");

    // Trimmed real AEMET response for Agost (03002)
    private static final String AGOST_FORECAST = """
            [{
              "nombre": "Agost",
              "prediccion": {"dia": [
                {
                  "fecha": "2026-10-19T00:00:00",
                  "temperatura": {"maxima": 23, "minima": 14},
                  "probPrecipitacion": [{"value": 0, "periodo": "00-24"}]
                },
                {
                  "fecha": "2026-10-20T00:00:00",
                  "temperatura": {"maxima": 26, "minima": 14, "dato": []},
                  "probPrecipitacion": [
                    {"value": 35, "periodo": "00-24"},
                    {"value": 30, "periodo": "00-12"},
                    {"value": 10, "periodo": "12-24"},
                    {"value": 25, "periodo": "00-06"},
                    {"value": 0, "periodo": "06-12"},
                    {"value": 10, "periodo": "12-18"},
                    {"value": 0, "periodo": "18-24"}
                  ]
                }
              ]}
            }]
            """;

    private final RestClient.Builder restClientBuilder = RestClient.builder();
    private final MockRestServiceServer aemet = MockRestServiceServer.bindTo(restClientBuilder).build();
    private final AemetForecastRepository repository = new AemetForecastRepository(
            new AemetClient(restClientBuilder, JsonMapper.builder().build(), "https://aemet.test", "key"));

    @BeforeEach
    void aemetReturnsAgostForecast() {
        aemet.expect(requestTo("https://aemet.test/api/prediccion/especifica/municipio/diaria/03002"))
                .andRespond(withSuccess("""
                        {"descripcion": "exito", "estado": 200, "datos": "https://aemet.test/sh/data"}
                        """, MediaType.APPLICATION_JSON));
        aemet.expect(requestTo("https://aemet.test/sh/data"))
                .andRespond(withSuccess()
                        .contentType(new MediaType("text", "plain", ISO_8859_15))
                        .body(AGOST_FORECAST.getBytes(ISO_8859_15)));
    }

    @Test
    void returnsRequestedDayWithOnlySixHourPeriods() {
        assertThat(repository.findByMunicipalityAndDate("03002", LocalDate.of(2026, 10, 20)))
                .contains(new DailyForecast(LocalDate.of(2026, 10, 20), 26, 14, List.of(
                        new PrecipitationProbability(25, "00-06"),
                        new PrecipitationProbability(0, "06-12"),
                        new PrecipitationProbability(10, "12-18"),
                        new PrecipitationProbability(0, "18-24"))));
    }

    @Test
    void returnsEmptyWhenAemetHasNoDataForThatDay() {
        assertThat(repository.findByMunicipalityAndDate("03002", LocalDate.of(2026, 10, 30))).isEmpty();
    }
}
