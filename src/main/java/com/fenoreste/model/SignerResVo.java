package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignerResVo implements Serializable {

    private String id;
    private String fullname;
    private String email;
    private String phone;
    private String notes;
    private String status;
    private String signer_type;
    private String role;
    private Integer sign_order;
    private String next_to_sign;
    private String sign_all_pages;
    private String created_at;
    private String updated_at;
    private String user_document_id;

    private static final long serialVersionUID = 1L;
}
