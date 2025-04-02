package com.fenoreste.util;

import com.fenoreste.entity.PersonaPK;
import com.fenoreste.model.OgsVo;
import com.fenoreste.model.OpaVo;
import org.springframework.stereotype.Service;

@Service
public class Util {

    public OpaVo opa(String cadena){
        OpaVo opa=new OpaVo();
        try {
            opa.setIdorigenp(Integer.parseInt(cadena.substring(0, 6)));
            System.out.println(cadena.substring(0, 6));
            opa.setIdproducto(Integer.parseInt(cadena.substring(6, 11)));
            opa.setIdauxiliar(Integer.parseInt(cadena.substring(11, 19)));
        } catch (Exception e) {
            System.out.println("Error al deserealizar opa:"+e.getMessage());
        }
        return opa;
    }

    public OgsVo ogs(String cadena){
        OgsVo ogs=new OgsVo();
        try {
            ogs.setIdorigen(Integer.parseInt(cadena.substring(0, 6)));
            ogs.setIdgrupo(Integer.parseInt(cadena.substring(6, 8)));
            ogs.setIdsocio(Integer.parseInt(cadena.substring(8, 14)));
        } catch (Exception e) {
            System.out.println("Error al deserealizar ogs:"+e.getMessage());
        }
        return ogs;
    }


    public String ogs(PersonaPK personaPK){
        String ogs = "";
        try {
            ogs = String.format("%06d",personaPK.getIdorigen()) + String.valueOf(personaPK.getIdgrupo())+String.format("%06d",personaPK.getIdsocio());
        } catch (Exception e) {
            System.out.println("Error al deserealizar personaPk:"+e.getMessage());
        }
        return ogs;
    }
}
