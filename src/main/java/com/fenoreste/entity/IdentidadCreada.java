package com.fenoreste.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "identidades_creadas")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class IdentidadCreada {

    @EmbeddedId
    private AuxiliarPK auxiliarPK;
    private String ididentidad;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;
    private boolean confirmada;
}
