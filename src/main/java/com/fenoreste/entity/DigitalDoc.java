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
    private boolean ok_identidad=false;
    private String iddocto_creado;
    private boolean ok_docto_creado;
    private boolean enviado_firmantes = false;
    private boolean firmado;
    private String mensajeFinal;

    private static final long serialVersionUID = 1L;
}
