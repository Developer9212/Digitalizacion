package com.fenoreste.controller;

import com.fenoreste.entity.AuxiliarPK;
import com.fenoreste.model.IdentidadVoResponse;
import com.fenoreste.model.OpaVo;
import com.fenoreste.model.ResCreaDocumentoVo;
import com.fenoreste.model.ResSignersVo;
import com.fenoreste.service.DigitalizacionServiceGeneral;
import com.fenoreste.util.Util;
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
    private IdentidadVoResponse identidadCrear(@RequestHeader("usuario")String user,@RequestParam(name = "ogs") String ogs) {
        log.info("ssssssssssssssss:"+user);
        return digitalizacionService.identidadCrear(ogs,"");
    }

    @GetMapping("/documento-crear")
    private ResCreaDocumentoVo creaDocumento(@RequestParam(name = "opa") String opa) {
        log.info(":::::::::::::::Creando documento para opTjk45tX_9023RillQcdCvdrewQtnjol742hyu a:"+opa+":::::::::::::::::::");
        return digitalizacionService.resCreaDocumentoVo(opa);
    }

    @GetMapping("/enviarFirmantes")
    private ResSignersVo nviarFirmantes(@RequestParam(name = "opa") String opa) {
        log.info(":::::::::::::::Enviar Firmantes para opa:"+opa+":::::::::::::::::::");

        OpaVo opa1 = new Util().opa(opa);
        AuxiliarPK auxiliarPk = new AuxiliarPK(opa1.getIdorigenp(),opa1.getIdproducto(),opa1.getIdauxiliar());
        return digitalizacionService.enviarFirmantes(auxiliarPk);
    }
}
