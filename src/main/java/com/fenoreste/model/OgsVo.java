package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OgsVo implements Serializable {
    private int idorigen;
    private int idgrupo;
    private int idsocio;

    private static final long serialVersionUID = 1L;
}
