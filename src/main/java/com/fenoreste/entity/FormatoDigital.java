package com.fenoreste.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "formatos_digitales_datos")
@Cacheable(false)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class FormatoDigital implements Serializable {

    @Id
    private Integer idkey;
    private String variable;
    private String valor;
    private String etiqueta;

    private static final long serialVersionUID = 1L;
}
