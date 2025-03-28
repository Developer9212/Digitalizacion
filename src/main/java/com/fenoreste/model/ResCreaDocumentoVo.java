package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResCreaDocumentoVo implements Serializable {

    private boolean success;
    private DataCrearDVo data;
    private String message;

    private static final long serialVersionUID = 1L;
}
