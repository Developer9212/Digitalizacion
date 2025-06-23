package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.FolioTarjeta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolioTarjetaDao extends JpaRepository<FolioTarjeta, AuxiliarPK> {

}
