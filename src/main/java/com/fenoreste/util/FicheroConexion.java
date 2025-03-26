/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fenoreste.util;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 *
 * @author wilmer
 */
@Service
public class FicheroConexion {

    String fileConfig = "digitalizacion.config";
    String nbd = "";
    String ipbd = "";

    public FicheroConexion() {
        readTextFile();
    }   
    
    private void readTextFile() {
        if (getFile() != null) {
            try {
                FileReader fr = new FileReader(getFile());
                BufferedReader br = new BufferedReader(fr);
                String linea;
                while ((linea = br.readLine()) != null) {
                    leer_lineas(linea);
                }

            } catch (Exception e) {
                System.out.println("Excepcion leyendo txt" + fileConfig + ": " + e);
            }
        } else {
            System.out.println("No se encontro el fichero.");
        }
    }
    
    public String getUserHome() {
        return System.getProperty("user.home");
    }

    public String getSeparator() {
        return System.getProperty("file.separator");
    }

    private File getFile() {
        String sf = getUserHome() + getSeparator() +fileConfig;
        File f = new File(sf);
        if (f.exists()) {
            return f;
        } else {
            System.out.println("El fichero no existe: " + sf);
            return null;
        }
    }
        

   private void leer_lineas(String linea) {
        if (linea.contains("base_de_datos")) {
            nbd = linea.split("=")[1];
        }
        if (linea.contains("direccion_servidor")) {
            ipbd = linea.split("=")[1];
        }
    }

    public String getDatabase() {
        return nbd;
    }

    public String getHost() {
        return ipbd;
    }
}
