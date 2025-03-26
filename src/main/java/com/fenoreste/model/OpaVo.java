package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpaVo implements Serializable {
    private int idorigenp;
    private int idproducto;
    private int idauxiliar;

    private static final long serialVersionUID = 1L;

}
