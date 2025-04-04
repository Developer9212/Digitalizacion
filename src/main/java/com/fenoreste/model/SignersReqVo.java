package com.fenoreste.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignersReqVo implements Serializable {

   private String document_id;
   private boolean workflow;
   private boolean use_whatsapp;
   private List<Signer> signers;


    private static final long serialVersionUID = 1L;
}
