package es.asv.weather.municipality.infrastructure;

import es.asv.weather.municipality.application.SearchMunicipalities;
import es.asv.weather.municipality.domain.MunicipalityRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class MunicipalityConfig {

    @Bean
    SearchMunicipalities searchMunicipalities(MunicipalityRepository municipalityRepository) {
        return new SearchMunicipalities(municipalityRepository);
    }
}
