package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDocumentResVo implements Serializable {

    private String id;
    private String organization_id;
    private String organization_document_id;
    private String user_id;
    private String name;
    private String external_id;
    private List<SequenceResVo> sequence;
    private boolean completed;
    private Integer signature_progress;
    private boolean workflow;
    private String updated_at;
    private String created_at;
    private String config_temp;
    private String file_path;

    private static final long serialVersionUID = 1L;
}
