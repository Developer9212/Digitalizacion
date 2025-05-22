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

    @Id
    private String ididentidad;
    private Integer idorigenp;
    private Integer idproducto;
    private Integer idauxiliar;    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha_creada;
    private boolean confirmada;
}
