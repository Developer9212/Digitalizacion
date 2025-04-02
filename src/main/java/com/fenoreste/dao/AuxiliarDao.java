package com.fenoreste.dao;

import com.fenoreste.entity.Auxiliar;
import com.fenoreste.entity.AuxiliarPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuxiliarDao extends JpaRepository<Auxiliar, AuxiliarPK> {
}
