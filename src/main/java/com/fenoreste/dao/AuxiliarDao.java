package com.fenoreste.dao;

import com.fenoreste.entity.Auxiliar;
import com.fenoreste.entity.AuxiliarPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AuxiliarDao extends JpaRepository<Auxiliar, AuxiliarPK> {

    @Query(value = "SELECT * FROM auxiliares WHERE idorigen=?1 AND idgrupo=?2 AND idsocio = ?3 AND idproducto=?4 AND estatus=2",
                    nativeQuery = true)
    public Auxiliar bucarPorOgsYProducto(Integer idorigen, Integer idgrupo, Integer idsocio, Integer idproducto);
}
