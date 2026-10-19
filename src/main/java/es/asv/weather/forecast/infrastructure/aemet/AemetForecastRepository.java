package es.asv.weather.forecast.infrastructure.aemet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import es.asv.weather.forecast.domain.DailyForecast;
import es.asv.weather.forecast.domain.ForecastRepository;
import es.asv.weather.forecast.domain.PrecipitationProbability;
import es.asv.weather.shared.infrastructure.aemet.AemetClient;
import org.springframework.stereotype.Repository;
import tools.jackson.core.type.TypeReference;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class AemetForecastRepository implements ForecastRepository {

    private static final TypeReference<List<AemetMunicipalityForecast>> FORECASTS = new TypeReference<>() {
    };
    // AEMET mixes 24, 12 and 6 hour periods ("00-24", "00-12", "00-06"...); we keep the 6 hour ones
    private static final Pattern PERIOD = Pattern.compile("(\\d{2})-(\\d{2})");

    private final AemetClient aemetClient;

    public AemetForecastRepository(AemetClient aemetClient) {
        this.aemetClient = aemetClient;
    }

    @Override
    public Optional<DailyForecast> findByMunicipalityAndDate(String municipalityCode, LocalDate date) {
        return aemetClient.get("/api/prediccion/especifica/municipio/diaria/" + municipalityCode, FORECASTS).stream()
                .flatMap(municipalityForecast -> municipalityForecast.forecast().days().stream())
                .filter(day -> day.localDate().equals(date))
                .findFirst()
                .map(AemetDay::toDomain);
    }

    private static boolean isSixHourPeriod(String period) {
        Matcher matcher = PERIOD.matcher(period == null ? "" : period);
        return matcher.matches() && Integer.parseInt(matcher.group(2)) - Integer.parseInt(matcher.group(1)) == 6;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetMunicipalityForecast(@JsonProperty("prediccion") AemetForecast forecast) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetForecast(@JsonProperty("dia") List<AemetDay> days) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetDay(
            @JsonProperty("fecha") String date,
            @JsonProperty("temperatura") AemetTemperature temperature,
            @JsonProperty("probPrecipitacion") List<AemetPrecipitationProbability> precipitationProbabilities) {

        // AEMET sends the date as "2026-10-20T00:00:00"
        LocalDate localDate() {
            return LocalDate.parse(date.substring(0, 10));
        }

        DailyForecast toDomain() {
            List<PrecipitationProbability> probabilities = precipitationProbabilities.stream()
                    .filter(probability -> probability.value() != null && isSixHourPeriod(probability.period()))
                    .map(probability -> new PrecipitationProbability(probability.value(), probability.period()))
                    .toList();
            return new DailyForecast(localDate(), temperature.max(), temperature.min(), probabilities);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetTemperature(@JsonProperty("maxima") int max, @JsonProperty("minima") int min) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AemetPrecipitationProbability(Integer value, @JsonProperty("periodo") String period) {
    }
}
