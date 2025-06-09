package com.fenoreste.dao;

import com.fenoreste.entity.Tabla;
import com.fenoreste.entity.TablaPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TablaDao extends JpaRepository<Tabla, TablaPK>{

    @Query(value = "SELECT * FROM tablas WHERE idtabla='digitalizacion' AND dato1=?1 LIMIT 1",nativeQuery = true)
    public Tabla TablaPorIdProducto(String dato1);
}
