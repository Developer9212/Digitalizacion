package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class DataCrearDVo implements Serializable {
    private String id;
    private String organization_id;
    private String organization_document_id;
    private String user_id;
    private String name;
    private String external_id;
    private List<Map<String, Object>> sequence; // Permite manejar cualquier estructura
    private boolean completed;
    private String signature_progress;
    private String workflow;
    private String updated_at;
    private String created_at;
    private String config_temp;
    private String file_path;

    private static final long serialVersionUID = 1L;
}
