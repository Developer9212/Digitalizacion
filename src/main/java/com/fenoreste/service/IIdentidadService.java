package com.fenoreste.service;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.IdentidadCreada;

import java.util.Date;
import java.util.List;

public interface IIdentidadService {

    public IdentidadCreada guardar(IdentidadCreada identidad);
    public IdentidadCreada buscarPorId(String ididentidad);
    public List<IdentidadCreada> buscarPorOpaFecha(AuxiliarPK auxiliarPK, Date fecha);


}
