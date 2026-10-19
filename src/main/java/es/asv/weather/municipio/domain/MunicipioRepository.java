package es.asv.weather.municipio.domain;

import java.util.List;

public interface MunicipioRepository {

    List<Municipio> findAll();
}
