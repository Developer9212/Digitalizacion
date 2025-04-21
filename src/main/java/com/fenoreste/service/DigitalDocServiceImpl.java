package com.fenoreste.service;

import com.fenoreste.dao.DigitalDocDao;
import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.DigitalDoc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DigitalDocServiceImpl implements IDigitalDocService{

    @Autowired
    private DigitalDocDao digitalDocDao;

    @Override
    public DigitalDoc buscarDigitalDocPorId(AuxiliarPK auxiliarPK) {
        return digitalDocDao.findById(auxiliarPK).orElse(null);
    }

    @Override
    public void insertarDigitalDoc(DigitalDoc digitalDoc) {
         digitalDocDao.save(digitalDoc);
    }

    @Override
    public DigitalDoc buscaPorIdIdentidad(String ididentidad) {
        return digitalDocDao.findByIdidentidad(ididentidad);
    }

    @Override
    public DigitalDoc buscaPorIdDocto(String iddoc) {
        return digitalDocDao.BuscarPorIdDocumentoCreado(iddoc);
    }
}
