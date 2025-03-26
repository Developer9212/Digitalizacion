package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVoResponse implements Serializable {

    private String clientId;
    private String clientSecret;
    private long expires_in;

    private static final long serialVersionUID = 1L;
}
