package com.fenoreste.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "digital_doc_legalario")
@Cacheable(false)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DigitalDoc implements Serializable {

    @EmbeddedId
    private AuxiliarPK auxiliarPK;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_captura;
    private String status;
    private String ididentidad;
    private boolean ok_identidad;
    private String iddocto_creado;
    private boolean ok_docto_creado;
    private boolean firmado;

    private static final long serialVersionUID = 1L;
}
