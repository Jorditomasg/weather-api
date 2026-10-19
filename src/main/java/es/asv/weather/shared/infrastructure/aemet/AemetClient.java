package es.asv.weather.shared.infrastructure.aemet;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;

/**
 * AEMET answers in two steps: first an envelope with the data URL, then the data itself,
 * served as text/plain in ISO-8859-15 (the String converter honours that charset).
 */
@Component
public class AemetClient {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    public AemetClient(RestClient.Builder restClientBuilder,
                       JsonMapper jsonMapper,
                       @Value("${aemet.base-url}") String baseUrl,
                       @Value("${aemet.api-key}") String apiKey) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).defaultHeader("api_key", apiKey).build();
        this.jsonMapper = jsonMapper;
    }

    public <T> T get(String path, TypeReference<T> type) {
        AemetEnvelope envelope = restClient.get().uri(path).retrieve().body(AemetEnvelope.class);
        if (envelope == null || envelope.status() != 200) {
            throw new AemetException("AEMET returned an error for " + path + ": " + envelope);
        }

        String data = restClient.get().uri(URI.create(envelope.dataUrl())).retrieve().body(String.class);
        return jsonMapper.readValue(data, type);
    }

    record AemetEnvelope(
            @JsonProperty("descripcion") String description,
            @JsonProperty("estado") int status,
            @JsonProperty("datos") String dataUrl) {
    }
}
