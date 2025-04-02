package com.fenoreste.service;

import com.fenoreste.dao.AuxiliarDao;
import com.fenoreste.entity.Auxiliar;
import com.fenoreste.entity.AuxiliarPK;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuxiliarServiceImpl implements IAuxiliarService {

    @Autowired
    private AuxiliarDao auxiliarDao;

    @Override
    public Auxiliar buscarPorId(AuxiliarPK auxiliarPK) {
        return auxiliarDao.findById(auxiliarPK).orElse(null);
    }
}
