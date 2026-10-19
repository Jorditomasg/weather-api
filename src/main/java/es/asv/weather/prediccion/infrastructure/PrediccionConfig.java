package es.asv.weather.prediccion.infrastructure;

import es.asv.weather.prediccion.application.ObtenerPrediccionManana;
import es.asv.weather.prediccion.domain.PrediccionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
class PrediccionConfig {

    @Bean
    ObtenerPrediccionManana obtenerPrediccionManana(PrediccionRepository prediccionRepository,
                                                    @Value("${prediccion.zona-horaria}") ZoneId zonaHoraria) {
        return new ObtenerPrediccionManana(prediccionRepository, Clock.system(zonaHoraria));
    }
}
