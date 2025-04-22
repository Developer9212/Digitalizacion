package com.fenoreste.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SequenceResVo implements Serializable {

    private Integer question;
    private Integer question_id;
    private String name;
    private String mandatory;

    private static final long serialVersionUID = 1L;
}
