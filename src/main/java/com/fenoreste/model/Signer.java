package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Signer implements Serializable {

    private String fullname;
    private String email;
    private String type= "FIRMA";
    private String role = "FIRMANTE";

    private static final long serialVersionUID = 1L;
}
