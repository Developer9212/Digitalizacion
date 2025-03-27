package com.fenoreste.service;

import com.fenoreste.dao.FormatoDigitalDao;
import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.FormatoDigital;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class FormatoDigitalServiceImpl implements IFormatoDigitalService {

    @Autowired
    private FormatoDigitalDao formatoDigitalDao;


    @Override
    public List<FormatoDigital> buscarListaPorId(AuxiliarPK auxiliarPK) {
        return formatoDigitalDao.listarFormatoDigital(auxiliarPK.getIdorigenp(),auxiliarPK.getIdproducto(),auxiliarPK.getIdauxiliar());
    }
}
