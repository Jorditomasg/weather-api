package es.asv.weather.shared.infrastructure.aemet;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;

/**
 * AEMET responde en dos pasos: primero un sobre con la URL de los datos y después los datos,
 * servidos como text/plain en ISO-8859-15 (el conversor de String respeta ese charset).
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

    public <T> T get(String path, TypeReference<T> tipo) {
        AemetRespuesta respuesta = restClient.get().uri(path).retrieve().body(AemetRespuesta.class);
        if (respuesta == null || respuesta.estado() != 200) {
            throw new AemetException("AEMET respondió con error en " + path + ": " + respuesta);
        }

        String datos = restClient.get().uri(URI.create(respuesta.datos())).retrieve().body(String.class);
        return jsonMapper.readValue(datos, tipo);
    }

    record AemetRespuesta(String descripcion, int estado, String datos) {
    }
}
