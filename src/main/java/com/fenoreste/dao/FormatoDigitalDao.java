package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.FormatoDigital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FormatoDigitalDao extends JpaRepository<FormatoDigital,Integer> {

    @Query(value = "SELECT * FROM formatos_digitales_datos WHERE idorigenp=?1 AND idproducto=?2 AND idauxiliar=?3 ORDER BY idkey ASC",nativeQuery = true)
    public List<FormatoDigital>listarFormatoDigital(Integer idorigenp, Integer idproducto,Integer idauxiliar);
}
