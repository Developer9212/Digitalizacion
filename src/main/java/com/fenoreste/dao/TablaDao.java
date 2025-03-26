package com.fenoreste.dao;

import com.fenoreste.entity.Tabla;
import com.fenoreste.entity.TablaPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TablaDao extends JpaRepository<Tabla, TablaPK>{

}
