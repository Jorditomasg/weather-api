package es.asv.weather.municipio.infrastructure;

import es.asv.weather.municipio.application.BuscarMunicipios;
import es.asv.weather.municipio.domain.MunicipioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MunicipioConfig {

    @Bean
    BuscarMunicipios buscarMunicipios(MunicipioRepository municipioRepository) {
        return new BuscarMunicipios(municipioRepository);
    }
}
