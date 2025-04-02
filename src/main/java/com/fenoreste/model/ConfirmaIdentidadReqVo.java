package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmaIdentidadReqVo implements Serializable {

    private String ididentidad;

    private static final long serialVersionUID = 1L;
}
