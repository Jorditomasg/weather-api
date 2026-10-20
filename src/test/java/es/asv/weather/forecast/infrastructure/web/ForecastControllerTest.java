package es.asv.weather.forecast.infrastructure.web;

import es.asv.weather.forecast.application.GetTomorrowForecast;
import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastNotFoundException;
import es.asv.weather.forecast.domain.PrecipitationProbability;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebMvcTest(ForecastController.class)
class ForecastControllerTest {

    private static final DailyForecast FORECAST = new DailyForecast(LocalDate.of(2026, 10, 20), 26, 14,
            List.of(new PrecipitationProbability(25, "00-06"), new PrecipitationProbability(0, "06-12")));

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private GetTomorrowForecast getTomorrowForecast;

    @Test
    void returnsForecastInCelsiusByDefaultWithContractFieldNames() {
        given(getTomorrowForecast.get("03002")).willReturn(FORECAST);

        assertThat(mvc.get().uri("/api/municipalities/03002/forecast"))
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
    void returnsForecastInRequestedUnit() {
        given(getTomorrowForecast.get("03002")).willReturn(FORECAST);

        assertThat(mvc.get().uri("/api/municipalities/03002/forecast?unit=G_FAH"))
                .hasStatusOk()
                .bodyJson()
                .hasPathSatisfying("$.mediaTemperatura", value -> assertThat(value).isEqualTo(68.0))
                .hasPathSatisfying("$.unidadTemperatura", value -> assertThat(value).isEqualTo("G_FAH"));
    }

    @Test
    void returnsBadRequestWithUnknownUnit() {
        assertThat(mvc.get().uri("/api/municipalities/03002/forecast?unit=G_KEL")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void returnsBadRequestWithMalformedCode() {
        assertThat(mvc.get().uri("/api/municipalities/abc/forecast")).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void returnsNotFoundWhenThereIsNoForecast() {
        given(getTomorrowForecast.get("03002"))
                .willThrow(new ForecastNotFoundException("03002", LocalDate.of(2026, 10, 20)));

        assertThat(mvc.get().uri("/api/municipalities/03002/forecast")).hasStatus(HttpStatus.NOT_FOUND);
    }

    @Test
    void returnsServiceUnavailableWithRetryAfterWhenAemetRateLimits() {
        given(getTomorrowForecast.get("03002")).willThrow(HttpClientErrorException.create(
                HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", HttpHeaders.EMPTY, new byte[0], null));

        assertThat(mvc.get().uri("/api/municipalities/03002/forecast"))
                .hasStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .headers().hasValue(HttpHeaders.RETRY_AFTER, "60");
    }
}
