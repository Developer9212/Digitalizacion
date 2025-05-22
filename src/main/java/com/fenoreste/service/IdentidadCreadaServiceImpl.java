package com.fenoreste.service;

import com.fenoreste.dao.IdentidadCreadaDao;
import com.fenoreste.entity.IdentidadCreada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdentidadCreadaServiceImpl implements IIdentidadService{

    @Autowired
    private IdentidadCreadaDao identidadCreadaDao;

    @Override
    public IdentidadCreada guardar(IdentidadCreada identidad) {
        return identidadCreadaDao.save(identidad);
    }

    @Override
    public IdentidadCreada buscar(String ididentidad) {
        return identidadCreadaDao.findByIdidentidad(ididentidad);
    }
}
