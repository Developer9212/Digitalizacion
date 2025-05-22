package com.fenoreste.dao;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.entity.DigitalDoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DigitalDocDao extends JpaRepository<DigitalDoc, AuxiliarPK> {

    @Query(value = "SELECT * FROM digital_doc_legalario WHERE iddocto_creado=?1",nativeQuery = true)
    public DigitalDoc BuscarPorIdDocumentoCreado(String iddoc);

}
