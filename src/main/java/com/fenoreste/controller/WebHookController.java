package com.fenoreste.controller;


import com.fenoreste.model.ConfirmaIdentidadReqVo;
import com.fenoreste.model.ConfirmaIdentidadVo;
import com.fenoreste.service.DigitalizacionServiceGeneral;
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

    @PostMapping(value = "confirma/identidad")
    public ConfirmaIdentidadVo confirmaIdentidad(@RequestBody ConfirmaIdentidadReqVo confirmaIdentidadVo) {
        log.info(":::::::::::::::::Identidad confirmada:"+confirmaIdentidadVo.getIdidentidad()+"::::::::::::::::::::");
        return digitalizacionService.confirmaIdentidadVo(confirmaIdentidadVo);
    }


    @PostMapping(value = "proceso/firma")
    public ConfirmaIdentidadVo procesoFirma(@RequestBody String json) {
        log.info(":::::::::::::::::Firma realizada para idDocumento"+json+"::::::::::::::::::::");
        return digitalizacionService.confirmaFirmacontrato(json);
    }

}
