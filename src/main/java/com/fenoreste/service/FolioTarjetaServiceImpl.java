package com.fenoreste.service;

import com.fenoreste.dao.FolioTarjetaDao;
import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.FolioTarjeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolioTarjetaServiceImpl implements IFolioTarjetaService{

    @Autowired
    private FolioTarjetaDao folioTarjetaDao;

    @Override
    public FolioTarjeta buscarPorId(AuxiliarPK pk) {
        return folioTarjetaDao.findById(pk).orElse(null);
    }
}
