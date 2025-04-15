package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SignersVoReq implements Serializable {

    private boolean send_invite = true;
    private List<SignerReqVo> signers;

    private static final long serialVersionUID = 1L;
}
