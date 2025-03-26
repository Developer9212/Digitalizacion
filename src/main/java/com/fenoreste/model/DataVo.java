package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataVo implements Serializable {

    private String _id;
    private String nombre;
    private String url;

    private static final long serialVersionUID = 1L;


}
