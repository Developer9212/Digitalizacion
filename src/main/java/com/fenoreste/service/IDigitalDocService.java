package com.fenoreste.service;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.DigitalDoc;

public interface IDigitalDocService {

    public DigitalDoc buscarDigitalDocPorId(AuxiliarPK auxiliarPK);
    public void insertarDigitalDoc(DigitalDoc digitalDoc);
    public DigitalDoc buscaPorIdIdentidad(String ididentidad);
}
