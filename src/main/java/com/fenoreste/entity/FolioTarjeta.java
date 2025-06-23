package com.fenoreste.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name="ws_siscoop_folios_tarjetas")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FolioTarjeta implements Serializable {

    @EmbeddedId
    private AuxiliarPK pk;
    private String idtarjeta;
    @Temporal(TemporalType.DATE)
    private Date fecha_hora;
    private boolean asignada;
    private boolean activa;
    private boolean bloqueada;

    private static final long serialVersionUID = 1L;
}
