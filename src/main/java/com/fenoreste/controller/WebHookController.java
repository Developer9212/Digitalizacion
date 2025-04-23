package com.fenoreste.controller;


import com.fenoreste.model.ConfirmaIdentidadReqVo;
import com.fenoreste.model.ConfirmaIdentidadVo;
import com.fenoreste.service.DigitalizacionServiceGeneral;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({"/api/webhook/v1"})
@Slf4j
public class WebHookController {

    @Autowired
    private DigitalizacionServiceGeneral digitalizacionService;

    @PostMapping(value = "confirma")
    public ConfirmaIdentidadVo confirmaIdentidadFirma(@RequestBody String cadena) {
        log.info(":::::::::::::::::Usando WebHook:"+cadena+"::::::::::::::::::::");

        Gson gson = new Gson();
        JsonObject json = gson.fromJson(cadena, JsonObject.class);
        ConfirmaIdentidadReqVo confirma = new ConfirmaIdentidadReqVo();

        if(json.get("type").getAsString().equals("identity.completed")){
            confirma.setId(json.get("onboarding_id").getAsString());
            if(json.get("status").getAsString().equals("completed")){
                confirma.setEstatus(true);
            }
            return digitalizacionService.confirmaIdentidadVo(confirma);
        }else if(json.get("type").getAsString().equals("document.sign")){
            JsonObject jsonDocument = json.getAsJsonObject("document");
            confirma.setId(jsonDocument.get("id").getAsString());
            confirma.setEstatus(jsonDocument.get("completed").getAsBoolean());
            return digitalizacionService.confirmaIdentidadVo(confirma);
        }
        return null;
    }



}
