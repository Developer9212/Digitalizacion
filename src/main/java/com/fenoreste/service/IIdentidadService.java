package com.fenoreste.service;

import com.fenoreste.entity.IdentidadCreada;

public interface IIdentidadService {

    public IdentidadCreada guardar(IdentidadCreada identidad);
    public IdentidadCreada buscar(String ididentidad);


}
