package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.IdentidadCreada;
import org.springframework.data.repository.CrudRepository;

public interface IdentidadCreadaDao extends CrudRepository<IdentidadCreada, AuxiliarPK> {

    public IdentidadCreada findByIdidentidad(String ididentidad);
}
