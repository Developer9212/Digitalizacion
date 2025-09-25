package com.fenoreste.controller;


import com.fenoreste.api_legalario.ApisHttp;
import com.fenoreste.entity.User;
import com.fenoreste.model.LoginVoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/login" })
public class TestUnit {

    @Autowired
    private ApisHttp apisHttp;

    @GetMapping
    public ResponseEntity<?>login(){
        LoginVoResponse loginVo = new LoginVoResponse();
        try {
           loginVo = apisHttp.login();
        }catch(Exception e) {
            e.printStackTrace();
            System.out.println(":::::::::::Error al genera login:::::::"+e.getMessage());
        }

        return new ResponseEntity<>(loginVo, HttpStatus.OK);
    }

}
