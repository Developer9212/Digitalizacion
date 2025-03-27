package com.fenoreste.service;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.FormatoDigital;

import java.util.List;

public interface IFormatoDigitalService {

    public List<FormatoDigital> buscarListaPorId(AuxiliarPK auxiliarPK);

}
