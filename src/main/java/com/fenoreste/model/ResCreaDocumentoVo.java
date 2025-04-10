package com.fenoreste.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResCreaDocumentoVo implements Serializable {

    private boolean success;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private DataCrearDVo data;
    private String message;

    private static final long serialVersionUID = 1L;
}
