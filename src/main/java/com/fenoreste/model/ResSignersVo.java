package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResSignersVo implements Serializable {

    private boolean success;
    private DataResSignersVo dataResSignersVoss;
    private String message;

    private static final long serialVersionUID = 1L;
}

