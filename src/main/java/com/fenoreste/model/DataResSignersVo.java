package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataResSignersVo implements Serializable {

    private List<SignerResVo> signers;
    private UserDocumentResVo user_document;


    private static final long serialVersionUID = 1L;
}
