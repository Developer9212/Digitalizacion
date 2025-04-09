package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SequenceResVo implements Serializable {

    private Integer question;
    private Integer question_id;
    private String name;
    private String mandatory;
    private String url;
    private String value;

    private static final long serialVersionUID = 1L;
}
