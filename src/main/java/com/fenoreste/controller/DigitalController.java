package com.fenoreste.controller;

import com.fenoreste.model.IdentidadVoResponse;
import com.fenoreste.model.ResCreaDocumentoVo;
import com.fenoreste.service.DigitalizacionServiceGeneral;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({"/api/digitalizacion/v1"})
@Slf4j
public class DigitalController {

    @Autowired
    private DigitalizacionServiceGeneral digitalizacionService;

    @GetMapping("/identidad-crear")
    private IdentidadVoResponse identidadCrear(@RequestParam(name = "ogs") String ogs) {
        return digitalizacionService.identidadCrear(ogs);
    }

    @GetMapping("/documento-crear")
    private ResCreaDocumentoVo creaDocumento(@RequestParam(name = "opa") String opa) {
        return digitalizacionService.resCreaDocumentoVo(opa);
    }
}
