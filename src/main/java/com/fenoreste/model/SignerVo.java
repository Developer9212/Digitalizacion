package com.fenoreste.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignerVo implements Serializable {

     private String email;
     private String fullname;
     private String phone;
     private String user_id;
     private String user_email;
     private String status ="pending";
     private String status_identity="active";
     private String organization_id = "5bd09a7c08228b112371bdc3";
     private String flow_id;
     private String flow_name;

    private static final long serialVersionUID = 1L;
}
