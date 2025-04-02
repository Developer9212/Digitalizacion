package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.DigitalDoc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DigitalDocDao extends JpaRepository<DigitalDoc, AuxiliarPK> {

    public DigitalDoc findByIdidentidad(String ididentidad);

}
