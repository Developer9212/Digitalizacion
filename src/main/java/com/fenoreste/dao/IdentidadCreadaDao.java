package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.IdentidadCreada;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface IdentidadCreadaDao extends CrudRepository<IdentidadCreada, AuxiliarPK> {

    public IdentidadCreada findByIdidentidad(String ididentidad);
    @Query(value = "SELECT * FROM identidad_creada WHERE idorigenp=?1 AND idproducto=?2 AND idauxiliar=?3 AND date(fecha_creada)=?4",nativeQuery = true)
    public List<IdentidadCreada> todasPorOpaFecha(Integer idorigenp, Integer idproducto, Integer idauxiliar, Date fecha);

}
