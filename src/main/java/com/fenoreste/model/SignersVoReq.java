package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SignersVoReq implements Serializable {

    private List<SignerVo> signers;

    private static final long serialVersionUID = 1L;
}
