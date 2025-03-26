package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class CrearDReqVo implements Serializable {

    private String name;
    private String type;
    private String template_id;
    private List<List<Map<String, Object>>>  sequence;


    private static final long serialVersionUID = 1L;
}
