package es.asv.weather.municipality.domain;

import java.util.List;

public interface MunicipalityRepository {

    List<Municipality> findAll();
}
