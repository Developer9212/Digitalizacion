package com.fenoreste.service;

import com.fenoreste.dao.IdentidadCreadaDao;
import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.IdentidadCreada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class IdentidadCreadaServiceImpl implements IIdentidadService{

    @Autowired
    private IdentidadCreadaDao identidadCreadaDao;

    @Override
    public IdentidadCreada guardar(IdentidadCreada identidad) {
        return identidadCreadaDao.save(identidad);
    }

    @Override
    public IdentidadCreada buscarPorId(String ididentidad) {
        return identidadCreadaDao.findByIdidentidad(ididentidad);
    }

    @Override
    public List<IdentidadCreada> buscarPorOpaFecha(AuxiliarPK auxiliarPK, Date fecha) {
        return identidadCreadaDao.todasPorOpaFecha(auxiliarPK.getIdorigenp(),auxiliarPK.getIdproducto(),auxiliarPK.getIdauxiliar(),fecha);
    }
}
