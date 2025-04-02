package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmaIdentidadVo implements Serializable {

    private boolean succes;
    private String message;

    private static final long serialVersionUID = 1L;
}
