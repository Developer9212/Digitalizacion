package com.fenoreste.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fenoreste.api_legalario.ApisHttp;
import com.fenoreste.entity.*;
import com.fenoreste.model.*;
import com.fenoreste.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Slf4j
public class DigitalizacionServiceGeneral {

    private String idTabla = "digitalizacion";

    @Autowired
    private Util util;

    @Autowired
    private IPersonaService personaService;

    @Autowired
    private ITablaService tablaService;

    @Autowired
    private IAmortizacionService amortizacionService;

    @Autowired
    private IFormatoDigitalService formatoDigitalService;

    @Autowired
    private IDigitalDocService digitalDocService;

    @Autowired
    private IAuxiliarService auxiliarService;

    @Autowired
    private IIdentidadService identidadService;

    @Autowired
    private IFolioTarjetaService folioTarjetaService;

    @Autowired
    private ApisHttp apisHttp;


    private Tabla tabla;
    private TablaPK tablaPK;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public IdentidadVoResponse identidadCrear(String ogs, String opa) {
        IdentidadVoResponse identidadVoResponse = new IdentidadVoResponse();
        try {

            OpaVo opaVo = util.opa(opa);
            AuxiliarPK auxiliarPK = new AuxiliarPK(opaVo.getIdorigenp(), opaVo.getIdproducto(), opaVo.getIdauxiliar());
            List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);
            if (!formatos.isEmpty()) {
                OgsVo ogsVo = util.ogs(ogs);
                PersonaPK personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                Persona persona = personaService.buscarPorId(personaPK);

                if (persona != null) {
                    tablaPK = new TablaPK(idTabla, "signer_data");
                    tabla = tablaService.buscarPorId(tablaPK);
                    StringBuilder sb = new StringBuilder(8);
                    for (int i = 0; i < 8; i++) {
                        int idx = random.nextInt(CHARACTERS.length());
                        sb.append(CHARACTERS.charAt(idx));
                    }
                    String correo = persona.getEmail();// "julio" + sb.toString() + "@gmail.com";

                    log.info("El correo es " + correo);

                    SignerReqVo signer = new SignerReqVo();
                    signer.setEmail(correo);//persona.getEmail());
                    signer.setPhone(persona.getCelular());
                    signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                    signer.setStatus("pending");
                    signer.setStatus_identity("active");
                    signer.setFlow_id(tabla.getDato3());
                    signer.setOrganization_id(tabla.getDato2());

                    SignersVoReq signersVoReq = new SignersVoReq();
                    List<SignerReqVo> lista = new ArrayList<>();
                    lista.add(signer);

                    String ogsAval1 = "";
                    String ogsAval2 = "";
                    String ogsAval3 = "";
                    String ogsCodeudor = "";

                    for (int i = 0; i < formatos.size(); i++) {
                        FormatoDigital formato = formatos.get(i);
                        //Vamos a enviar a firmantes verificamos que personas debemos enviar
                        if (formato.getEtiqueta().contains("ogs_codeudor") && !formato.getValor().isEmpty()) {
                            ogsCodeudor = formato.getValor().replace("-", "").trim();
                            log.info("Ogs codeudor: " + ogsCodeudor);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval1") && !formato.getValor().isEmpty()) {
                            ogsAval1 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval1: " + ogsAval1);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval2") && !formato.getValor().isEmpty()) {
                            ogsAval2 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval2: " + ogsAval2);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval3") && !formato.getValor().isEmpty()) {
                            ogsAval3 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval3: " + ogsAval3);
                        }
                    }


                    if (!ogsCodeudor.isEmpty()) {
                        ogsVo = util.ogs(ogsCodeudor);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        SignerReqVo signerCodeudor = new SignerReqVo();

                        signerCodeudor.setEmail(persona.getEmail());
                        signerCodeudor.setPhone(persona.getCelular());
                        signerCodeudor.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signerCodeudor.setStatus("pending");
                        signerCodeudor.setStatus_identity("active");
                        signerCodeudor.setFlow_id(tabla.getDato3());
                        signerCodeudor.setOrganization_id(tabla.getDato2());
                        lista.add(signerCodeudor);
                    }

                    if (!ogsAval1.isEmpty()) {
                        ogsVo = util.ogs(ogsAval1);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        SignerReqVo signerAval1 = new SignerReqVo();

                        signerAval1.setEmail(persona.getEmail());
                        signerAval1.setPhone(persona.getCelular());
                        signerAval1.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signerAval1.setStatus("pending");
                        signerAval1.setStatus_identity("active");
                        signerAval1.setFlow_id(tabla.getDato3());
                        signerAval1.setOrganization_id(tabla.getDato2());
                        lista.add(signerAval1);
                    }

                    if (!ogsAval2.isEmpty()) {
                        ogsVo = util.ogs(ogsAval2);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        SignerReqVo signerAval2 = new SignerReqVo();

                        signerAval2.setEmail(persona.getEmail());
                        signerAval2.setPhone(persona.getCelular());
                        signerAval2.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signerAval2.setStatus("pending");
                        signerAval2.setStatus_identity("active");
                        signerAval2.setFlow_id(tabla.getDato3());
                        signerAval2.setOrganization_id(tabla.getDato2());
                        lista.add(signerAval2);
                    }

                    if (!ogsAval3.isEmpty()) {
                        ogsVo = util.ogs(ogsAval3);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        SignerReqVo signerAval3 = new SignerReqVo();

                        signerAval3.setEmail(persona.getEmail());
                        signerAval3.setPhone(persona.getCelular());
                        signerAval3.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signerAval3.setStatus("pending");
                        signerAval3.setStatus_identity("active");
                        signerAval3.setFlow_id(tabla.getDato3());
                        signerAval3.setOrganization_id(tabla.getDato2());
                        lista.add(signerAval3);
                    }

                    signersVoReq.setSigners(lista);
                    log.info("::Lista de identidades a crear:" + lista);

                    //Buscamos si existe un token en tablas
                    tablaPK = new TablaPK(idTabla, "token");
                    tabla = tablaService.buscarPorId(tablaPK);

                    if (tabla != null) {
                        //token();
                        identidadVoResponse = apisHttp.identidadCrear(signersVoReq);
                        /*if (!identidadVoResponse.isSuccess() && identidadVoResponse.getMessage().contains("AUTHORIZATION_ERROR")) {
                            token();
                            identidadVoResponse = apisHttp.identidadCrear(signersVoReq);
                        }*///bloque comentado el 06/11/2025 porque cambia scope y vencimiento de token


                    } else {
                        log.warn(":::::::::::::::::No existe parametrizacion token::::::::::::");
                    }
                } else {
                    log.info("::::::::::::::Persona no existe con id:" + ogs);

                }
            } else {
                log.info("::::::::::::::No se han llenado variables para validacion de identidad:::::::::::::::::::");
            }

        } catch (Exception e) {
            log.error("::::::::::::Error al crear validacion de identidad:::::::::::" + e.getMessage());
        }
        return identidadVoResponse;
    }

    //Metodo llamado desde SAICoop
    public ResCreaDocumentoVo resCreaDocumentoVo(String opa) {
        ResCreaDocumentoVo resp = new ResCreaDocumentoVo();
        DigitalDoc digitalDoc = new DigitalDoc();
        try {
            OpaVo opas = util.opa(opa);
            AuxiliarPK auxiliarPK = new AuxiliarPK(opas.getIdorigenp(), opas.getIdproducto(), opas.getIdauxiliar());

            //Verificamos que el folio exista en auxiliares y que este en estatus capturado
            Auxiliar a = auxiliarService.buscarPorId(auxiliarPK);
            log.info(":::::Estatus opa:" + a.getEstatus());
            if (a != null) {
                tablaPK = new TablaPK(idTabla, "capital_en_riesgo");
                tabla = tablaService.buscarPorId(tablaPK);
                log.info("::::::::::::Monto aut:" + a.getMontoautorizado() + ",Capital en riesgo:" + tabla.getDato2());

                //Buscamos el producto para tomar capital en riesgo
                PersonaPK personaPK = new PersonaPK(a.getIdorigen(), a.getIdgrupo(), a.getIdsocio());
                Auxiliar ahorro = auxiliarService.buscarPorOgsyProducto(personaPK, Integer.parseInt(tabla.getDato1()));

                if (ahorro != null) {
                    //Calculamos el capital en riesgo
                    Double capitalEnRiesgo = a.getMontoautorizado().doubleValue() - ahorro.getSaldo().doubleValue();

                    if(capitalEnRiesgo <= Double.parseDouble(tabla.getDato2())){
                            if (a.getEstatus() == 1) {
                                List<List<Map<String, Object>>> sequence1 = new ArrayList<>();
                                //Si esta el auxiliar ahora buscamos que esten las variables listas para el contrato
                                List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);
                                if (formatos.size() > 0) {

                                    //Ya nos aseguramos que ya esten las variables en la tabla ahora si creamos la identidad
                                    //Antes llenamos la tabla para preparar respuestas de legalario
                                    digitalDoc.setAuxiliarPK(a.getPk());
                                    digitalDoc.setFecha_captura(new Date());
                                    digitalDoc.setStatus("Pendiente");
                                    digitalDoc.setFirmado(false);
                                    digitalDoc.setOk_identidad(false);

                                    //Una ves guardado el registros enviamos la creacion de identidad
                                    Persona persona = personaService.buscarPorId(personaPK);
                                    String ogs = util.ogs(persona.getPk());

                                    /*Corremos la lista de formatos digitales datos solo para asegurarnos que quien elaboro no sea
                                    la misma persona que autorizo*/
                                    boolean banderaAutorizacion = false;
                                    String autorizo = "";
                                    String elaboro="";
                                    for(int i =0;i< formatos.size();i++){
                                        FormatoDigital formato = formatos.get(i);
                                         if(formato.getEtiqueta().trim().replace(">","").replace("<","").replace("|","").equalsIgnoreCase("elaboro")){
                                                elaboro = formato.getValor().trim();
                                        }
                                        if(formato.getEtiqueta().trim().replace(">","").replace("<","").replace("|","").equalsIgnoreCase("autorizo")){
                                            autorizo = formato.getValor().trim();
                                        }
                                        if(!elaboro.isEmpty() && !autorizo.isEmpty()){
                                            break;
                                        }

                                    }
                                    if (!elaboro.trim().equalsIgnoreCase(autorizo.trim())) {
                                        digitalDocService.insertarDigitalDoc(digitalDoc);
                                        banderaAutorizacion = true;
                                    }

                                    if(banderaAutorizacion){
                                        //Consumimos metodo de crear Identidad
                                        IdentidadVoResponse identidadVoResponse = identidadCrear(ogs, opa);

                                        ObjectMapper mapper = new ObjectMapper();
                                        JsonNode dataArray = mapper.valueToTree(identidadVoResponse.getData());

                                        if (identidadVoResponse.isSuccess()) {
                                            digitalDoc = digitalDocService.buscarDigitalDocPorId(a.getPk());
                                            JsonNode firstElement = dataArray.get(0);
                                            String id = firstElement.get("_id").asText();
                                            digitalDoc.setTidentidades(identidadVoResponse.getData().size());
                                            digitalDoc.setMensajeFinal("Identidad creada:" + new Date());
                                            digitalDocService.insertarDigitalDoc(digitalDoc);

                                            String identidades = "";
                                            for (int i = 0; i < identidadVoResponse.getData().size(); i++) {
                                                IdentidadCreada identidadCreada = new IdentidadCreada();
                                                JsonNode dataArray1 = mapper.valueToTree(identidadVoResponse.getData());
                                                JsonNode firstElement1 = dataArray1.get(i);
                                                String id1 = firstElement1.get("_id").asText();
                                                identidadCreada.setIdorigenp(auxiliarPK.getIdorigenp());
                                                identidadCreada.setIdproducto(auxiliarPK.getIdproducto());
                                                identidadCreada.setIdauxiliar(auxiliarPK.getIdauxiliar());
                                                identidadCreada.setFecha_creada(new Date());
                                                identidadCreada.setIdidentidad(id1);
                                                identidadCreada.setConfirmada(false);
                                                identidadService.guardar(identidadCreada);
                                                identidades = identidades + id1 + "|";
                                            }

                                            log.info("::::::::::Se envio la identidad::::::::::::");
                                            resp.setSuccess(true);
                                            resp.setMessage("Identidad creada con exito:" + identidades);
                                        } else {
                                            log.error(":::::::::::::::::::::" + identidadVoResponse.getMessage() + ":::::::::::::::::::");
                                            digitalDoc.setMensajeFinal(identidadVoResponse.getMessage() + ":" + new Date());
                                            digitalDocService.insertarDigitalDoc(digitalDoc);
                                            resp.setSuccess(false);
                                            resp.setMessage(identidadVoResponse.getMessage());
                                        }
                                    }else{
                                        log.info("::::::::::::::Persona que elaboro no puede autorizar credito:::::::::::::");
                                        resp.setMessage("Persona que elaboro no puede autorizar credito");
                                    }

                                }
                            } else {
                                log.info("::::::::::::::Estatus folio debe ser autorizado:::::::::::::");
                                resp.setMessage("Estatus folio debe ser autorizado");

                        }
                    } else {
                        log.info("::::::::::::::Monto en solicitud debe ser <= " + tabla.getDato2() + ":::::::::::::");
                        resp.setMessage("Monto en solicitud debe ser <= " + tabla.getDato2());
                    }

                }else{
                    log.info("::::::::::::::Producto para validacion de capital en riesgo no configurado o estatus no valido: " + tabla.getDato1() + ":::::::::::::");
                    resp.setMessage("Producto para validacion de capital en riesgo no configurado: " + tabla.getDato1());
                }

            } else {
                log.info(":::::::::::::::No existe folio capturado::::::::::::::::");
                resp.setMessage("El folio no existe");
            }


        } catch (Exception e) {
            log.error("Error al crear Documento para opa:" + opa + "," + e.getMessage() + ":::::::::::::::");
            resp.setMessage("Error al crear Documento:" + e.getMessage());
            digitalDoc.setMensajeFinal(e.getMessage() + ":" + new Date());

        }
        return resp;
    }


    public ConfirmaIdentidadVo confirmaIdentidadVo(ConfirmaIdentidadReqVo confirmaIdentidadReqVo) {
        ConfirmaIdentidadVo confirmaIdentidadVo = new ConfirmaIdentidadVo();
        DigitalDoc digitalDoc = new DigitalDoc();
        AuxiliarPK aPk = new AuxiliarPK();
        try {

            boolean isIdentidadConfirmada = false;
            int totalIdentidadesConfirmadas = 0;


            IdentidadCreada identidad = identidadService.buscarPorId(confirmaIdentidadReqVo.getId());
            if (identidad != null && !identidad.isConfirmada()) {
                CrearDReqVo crearDReqVo = new CrearDReqVo();
                aPk = new AuxiliarPK(identidad.getIdorigenp(), identidad.getIdproducto(), identidad.getIdauxiliar());
                digitalDoc.setAuxiliarPK(aPk);
                digitalDoc = digitalDocService.buscarDigitalDocPorId(aPk);

                //vamos a obtener el total de identidades que tienen que firmar este contrato
                int totalIdentidades = 0;//

                if (digitalDoc != null && !digitalDoc.isEnviado_firmantes()) {
                    log.info("Se encuentra la identidad y vamos a confirmarla");
                    identidad.setConfirmada(true);
                    identidadService.guardar(identidad);
                    totalIdentidades = digitalDoc.getTidentidades();


                    //Buscamos las identidades para este opa y que fue creada en esta fecha
                    List<IdentidadCreada> identidadesTodas = identidadService.buscarPorOpaFecha(aPk, digitalDoc.getFecha_captura());
                    if (!identidadesTodas.isEmpty()) {
                        //corremos toda la lista de identidades que se encontraron para el opa
                        for (int i = 0; i < identidadesTodas.size(); i++) {
                            IdentidadCreada identidadCreada = identidadesTodas.get(i);
                            if (identidadCreada.isConfirmada()) {
                                totalIdentidadesConfirmadas = totalIdentidadesConfirmadas + 1;
                            }
                        }

                        log.info(":::::::::::Total identidades confirmadas:" + totalIdentidades + ",Confirmadas:" + totalIdentidadesConfirmadas);
                        if (totalIdentidades == totalIdentidadesConfirmadas) {
                            isIdentidadConfirmada = true;
                        }

                        if (isIdentidadConfirmada) {
                            //digitalDocService.insertarDigitalDoc(digitalDoc);
                            //Una ves guardada la identidad vamos a enviar las variables para el documento
                            log.info(":::::::::::::Vamos a buscar el registro opa:::::" + digitalDoc.getAuxiliarPK());
                            List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(digitalDoc.getAuxiliarPK());


                            List<List<Map<String, Object>>> sequence = new ArrayList<>();
                            boolean banderaDatos = false;


                            tabla = tablaService.buscarPorIdProductoArray(aPk.getIdproducto());
                            Auxiliar a = auxiliarService.buscarPorId(digitalDoc.getAuxiliarPK());
                            PersonaPK personaPK = new PersonaPK(a.getIdorigen(), a.getIdgrupo(), a.getIdsocio());
                            Persona p = personaService.buscarPorId(personaPK);

                            crearDReqVo.setName(p.getNombre() + " " + p.getAppaterno() + " " + p.getApmaterno() + "-" + String.format("%06d", a.getPk().getIdorigenp()) + String.format("%05d", a.getPk().getIdproducto()) + String.format("%08d", a.getPk().getIdauxiliar()));
                            crearDReqVo.setTemplate_id(tabla.getDato2());

                            for (int i = 0; i < formatos.size(); i++) {
                                FormatoDigital formato = formatos.get(i);
                                String etiqueta = formato.getEtiqueta().replaceAll("[<>]", "").replaceAll("\\|$", "").replace("|", "_").replace("__", "_").replace(".sql", "");
                                //log.info("Etiqueta::::::::::::"+etiqueta+",valor:"+formato.getValor());
                                if (!etiqueta.toUpperCase().contains("AMORTIZACIONES")) {
                                    if (etiqueta.toUpperCase().contains("NUM_CUENTA_TDD")) {
                                        Auxiliar auxTDD = auxiliarService.buscarPorId(digitalDoc.getAuxiliarPK());
                                        personaPK = new PersonaPK(auxTDD.getIdorigen(), auxTDD.getIdgrupo(), auxTDD.getIdsocio());
                                        auxTDD = auxiliarService.buscarPorOgsyProducto(personaPK, 133);
                                        String opaTDD = "";
                                        if (auxTDD != null) {
                                            opaTDD = String.format("%06d", auxTDD.getPk().getIdorigenp()) + String.format("%05d", auxTDD.getPk().getIdproducto()) + String.format("%08d", auxTDD.getPk().getIdauxiliar());
                                        } else {
                                            auxTDD = auxiliarService.buscarPorOgsyProducto(personaPK, 110);
                                            opaTDD = String.format("%06d", auxTDD.getPk().getIdorigenp()) + String.format("%05d", auxTDD.getPk().getIdproducto()) + String.format("%08d", auxTDD.getPk().getIdauxiliar());
                                        }

                                        sequence.add(Collections.singletonList(createItem(formato.getIdkey(), etiqueta.replace("_+$", "").trim(), opaTDD.trim())));
                                    } else {
                                        sequence.add(Collections.singletonList(createItem(formato.getIdkey(), etiqueta.replace("_+$", "").trim(), formato.getValor().trim())));
                                    }
                                } else {
                                    //log.info("La etiqueta es:"+etiqueta+","+formato.getValor());
                                    String[] lineas = formato.getValor().split("\\n");
                                    List<Amortizacion> listaAmortizaciones = amortizacionService.buscarTodasPorId(digitalDoc.getAuxiliarPK());
                                    Map<String, List<Object>> columnasMap = new LinkedHashMap<>();
                                    // Inicializa todas las listas para las columnas
                                    columnasMap.put("NUM_P", new ArrayList<>());
                                    columnasMap.put("FECHA_CORTE_PAGO", new ArrayList<>());
                                    columnasMap.put("ABONO_PRINCIPAL", new ArrayList<>());
                                    columnasMap.put("ANUALIDAD", new ArrayList<>());
                                    columnasMap.put("SALDO_INSOLUTO", new ArrayList<>());
                                    columnasMap.put("INTERES_ORDINARIO", new ArrayList<>());
                                    columnasMap.put("IVA_INTERESES", new ArrayList<>());
                                    columnasMap.put("TOTAL_PAGAR", new ArrayList<>());

                                    for (String linea : lineas) {
                                        // Separar por |
                                        String[] columnas = linea.split("\\|");
                                        columnasMap.get("NUM_P").add(columnas[0]);
                                        columnasMap.get("FECHA_CORTE_PAGO").add(columnas[1]);
                                        columnasMap.get("ABONO_PRINCIPAL").add(columnas[2]);
                                        columnasMap.get("ANUALIDAD").add(columnas[3]);
                                        columnasMap.get("SALDO_INSOLUTO").add(columnas[4]);
                                        columnasMap.get("INTERES_ORDINARIO").add(columnas[5]);
                                        columnasMap.get("IVA_INTERESES").add(columnas[6]);
                                        columnasMap.get("TOTAL_PAGAR").add(columnas[7]);
                                        log.info("Valor de anualidad:" + columnas[3] + ",saldo insoluto:" + columnas[4] + ",interes ordinario:" + columnas[5],
                                                "Iva Intereses:" + columnas[6] + ",total a pagar:" + columnas[7]);
                                    }


                                    Map<String, Object> amortizacionData = new HashMap<>();
                                    amortizacionData.put("key", formato.getIdkey());
                                    amortizacionData.put("name", "amortizaciones");
                                    amortizacionData.put("value", columnasMap);
                                    sequence.add(Collections.singletonList(amortizacionData));
                                }
                                banderaDatos = true;
                            }

                            if (banderaDatos) {
                                log.info("::::::::Vamos a crear el documento:::::::::::::");
                                crearDReqVo.setType("template");
                                crearDReqVo.setSequence(sequence);
                                ResCreaDocumentoVo resp = apisHttp.crearDocumento(crearDReqVo);
                                /*if (!resp.isSuccess() && resp.getMessage().contains("AUTHORIZATION_ERROR")) {
                                    token();
                                    resp = apisHttp.crearDocumento(crearDReqVo);
                                }*/

                                log.info(":::::::::::::::::::::Se creo el documento::::::::::::::::::");
                                //Una ves creado el documento actualizamos la tabla y enviamos a los firmantes
                                if (resp.isSuccess()) {
                                    digitalDoc.setIddocto_creado(resp.getData().getId());
                                    digitalDoc.setOk_docto_creado(true);
                                    confirmaIdentidadVo.setSucces(true);
                                    confirmaIdentidadVo.setMessage(resp.getMessage());
                                    digitalDoc.setOk_identidad(true);
                                    digitalDoc.setMensajeFinal("Documento creado exitosamente" + ":" + new Date());

                                    log.info("::::::::::::::Documento enviado,enviaremos a firmantes::::::::::::::::::::");

                                    ResSignersVo resSignersVo = enviarFirmantes(digitalDoc.getAuxiliarPK());

                                    if (resSignersVo.isSuccess()) {
                                        confirmaIdentidadVo.setSucces(true);
                                        confirmaIdentidadVo.setMessage(resSignersVo.getMessage());
                                        digitalDoc.setMensajeFinal(resSignersVo.getMessage() + ":" + new Date());
                                        log.info(":::::::::::::::::Mensaje envio a firmantes:" + resSignersVo.getMessage() + "::::::::::::::::::::::");
                                        digitalDoc.setEnviado_firmantes(true);
                                    } else {
                                        digitalDoc.setMensajeFinal(resSignersVo.getMessage());
                                        digitalDoc.setEnviado_firmantes(false);
                                    }
                                } else {
                                    confirmaIdentidadVo.setMessage(resp.getMessage());
                                    digitalDoc.setMensajeFinal(resp.getMessage() + ":" + new Date());
                                }
                            }
                        } else {
                            log.info("::::::::::::Se confirmo la identidad pero no se completan todas::::::::");
                            digitalDoc.setMensajeFinal("Se confirmo la identidad pero no se completan todas::::");
                            digitalDoc.setOk_identidad(false);
                            digitalDoc.setOk_docto_creado(false);
                            confirmaIdentidadVo.setMessage("Identidad confirmada:" + confirmaIdentidadReqVo.getId());
                        }

                        digitalDoc.setAuxiliarPK(aPk);
                        digitalDocService.insertarDigitalDoc(digitalDoc);
                    } else {
                        log.info("::::::::::::No se encuentran identidades para opa:" + aPk + ":::::::");
                        confirmaIdentidadVo.setMessage("No se encuentran identidades relacionadas al folio");
                    }


                } else {
                    log.info("::::::::::::Es el documento no existe o ya se envio a firma anteriormente::::::::::::::");
                    confirmaIdentidadVo.setMessage("Documento no existe o ya se envio a firma anteriormente");
                }
            } else {
                log.info("::::::::::::Identidad no existe o ya se confirmo anteriormente::::::::::::::");
                confirmaIdentidadVo.setMessage("Identidad no existe o ya se confirmo anteriormente");
            }

        } catch (Exception e) {
            log.error("::::::::::::Error al confirmar identidad:::::::::::::" + e.getMessage() + ",identidad:" + confirmaIdentidadReqVo.getId());
            confirmaIdentidadVo.setMessage("Error al confirmar identidad:::::::::::::" + e.getMessage() + ",identidad:" + confirmaIdentidadReqVo.getId());

        }


        return confirmaIdentidadVo;
    }


    public ResSignersVo enviarFirmantes(AuxiliarPK opa) {
        ResSignersVo resSignersVo = new ResSignersVo();
        try {
            //OpaVo opaVo = util.opa(opa);
            //AuxiliarPK auxiliarPK = new AuxiliarPK(opaVo.getIdorigenp(), opaVo.getIdproducto(), opaVo.getIdauxiliar());
            DigitalDoc digitalDoc = digitalDocService.buscarDigitalDocPorId(opa);
            if (digitalDoc != null && !digitalDoc.isEnviado_firmantes()) {
                if (!digitalDoc.isFirmado()) {
                    SignersReqVo signersReqVo = new SignersReqVo();
                    signersReqVo.setDocument_id(digitalDoc.getIddocto_creado());
                    signersReqVo.setWorkflow(true);
                    signersReqVo.setUse_whatsapp(false);
                    DataResSignersVo dataRes = new DataResSignersVo();
                    List<Signer> signers = new ArrayList<Signer>();

                    Auxiliar a = auxiliarService.buscarPorId(opa);
                    PersonaPK personaPK = new PersonaPK(a.getIdorigen(), a.getIdgrupo(), a.getIdsocio());
                    Persona persona = personaService.buscarPorId(personaPK);

                    List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(opa);//auxiliarPK);


                    String ogsAval1 = "";
                    String ogsAval2 = "";
                    String ogsAval3 = "";
                    String ogsCodeudor = "";

                    for (int i = 0; i < formatos.size(); i++) {
                        FormatoDigital formato = formatos.get(i);
                        //Vamos a enviar a firmantes verificamos que personas debemos enviar
                        if (formato.getEtiqueta().contains("ogs_codeudor") && !formato.getValor().isEmpty()) {
                            ogsCodeudor = formato.getValor().replace("-", "").trim();
                            log.info("Ogs codeudor: " + ogsCodeudor);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval1") && !formato.getValor().isEmpty()) {
                            ogsAval1 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval1: " + ogsAval1);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval2") && !formato.getValor().isEmpty()) {
                            ogsAval2 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval2: " + ogsAval2);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval3") && !formato.getValor().isEmpty()) {
                            ogsAval3 = formato.getValor().replace("-", "").trim();
                            log.info("Ogs aval3: " + ogsAval3);
                        }
                    }

                    Signer signer = new Signer();
                    OgsVo ogsVo = new OgsVo();

                    if (!persona.getEmail().isEmpty()) {
                        log.info("::::::Correo socio:" + persona.getEmail());
                        signer.setEmail(persona.getEmail().trim());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    }

                    if (!ogsCodeudor.isEmpty()) {
                        ogsVo = util.ogs(ogsCodeudor);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        log.info(":::::Ogs codeudor:" + persona.getPk().getIdorigen() + "-" + persona.getPk().getIdgrupo() + "-" + persona.getPk().getIdsocio() + ",correo:" + persona.getEmail());
                        Signer signer1 = new Signer();
                        signer1.setEmail(persona.getEmail());
                        signer1.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer1.setRole("FIRMANTE");
                        signer1.setType("FIRMA");
                        signers.add(signer1);
                    }

                    if (!ogsAval1.isEmpty()) {

                        ogsVo = util.ogs(ogsAval1);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        log.info(":::::Ogs aval1:" + persona.getPk().getIdorigen() + "-" + persona.getPk().getIdgrupo() + "-" + persona.getPk().getIdsocio() + ",correo:" + persona.getEmail());
                        Signer signer2 = new Signer();
                        signer2.setEmail(persona.getEmail());
                        signer2.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer2.setRole("FIRMANTE");
                        signer2.setType("FIRMA");
                        signers.add(signer2);
                    }

                    if (!ogsAval2.isEmpty()) {
                        ogsVo = util.ogs(ogsAval2);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        log.info(":::::Ogs aval 2:" + persona.getPk().getIdorigen() + "-" + persona.getPk().getIdgrupo() + "-" + persona.getPk().getIdsocio() + ",correo:" + persona.getEmail());
                        Signer signer3 = new Signer();
                        signer3.setEmail(persona.getEmail());
                        signer3.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer3.setRole("FIRMANTE");
                        signer3.setType("FIRMA");
                        signers.add(signer3);
                    }

                    if (!ogsAval3.isEmpty()) {
                        ogsVo = util.ogs(ogsAval3);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        log.info(":::::Ogs aval 3:" + persona.getPk().getIdorigen() + "-" + persona.getPk().getIdgrupo() + "-" + persona.getPk().getIdsocio() + ",correo:" + persona.getEmail());
                        Signer signer4 = new Signer();
                        signer4.setEmail(persona.getEmail());
                        signer4.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer4.setRole("FIRMANTE");
                        signer4.setType("FIRMA");
                        signers.add(signer4);
                    }

                    signersReqVo.setSigners(signers);

                    if (!signersReqVo.getSigners().isEmpty()) {
                        signersReqVo.setSigners(signers);
                        log.info("::::::::::::Vamos a enviar a firmantes:::::::::::::::");
                        System.out.println(":::::::Tu peticion enviar firmantes:::" + signersReqVo);
                        signersReqVo.setSend_invite(true);
                        ResSignersVo signerResVo = apisHttp.firmantes(signersReqVo);
                        resSignersVo.setMessage(signerResVo.getMessage());
                        resSignersVo.setSuccess(true);
                        resSignersVo.setMessage(signerResVo.getMessage());
                        digitalDoc.setMensajeFinal("Documento enviado a firma");
                        digitalDoc.setEnviado_firmantes(true);
                    }
                } else {
                    log.info(":::::::::::::El documento ya esta firmado::::::::::::::");
                    resSignersVo.setMessage("El documento ya esta firmado");
                    resSignersVo.setSuccess(false);
                    resSignersVo.setMessage("El documento ya esta firmado");
                    digitalDoc.setMensajeFinal("Documento ya firmado");
                }
            } else {
                log.info("::::::::::::Es el documento no existe o ya se envio a firma anteriormente::::::::::::::");
                resSignersVo.setMessage("El documento no existe o ya se envio a firma anteriormente");
                digitalDoc.setMensajeFinal("Documento no existe o ya se envio a firma anteriormente");
            }
            digitalDocService.insertarDigitalDoc(digitalDoc);
        } catch (Exception e) {
            log.error(":::::::::::::Sucedio un error al enviar a firmar:::::::::" + e.getMessage());
            resSignersVo.setMessage("Error al enviar a firmantes:" + e.getMessage());

        }
        return resSignersVo;
    }

    public ConfirmaIdentidadVo confirmaFirmacontrato(String cadena) {
        ConfirmaIdentidadVo confirmaIdentidadVo = new ConfirmaIdentidadVo();

        try {
            JSONObject json = new JSONObject(cadena);
            JSONObject jsonObject = json.getJSONObject("document");
            String idDoctoFirmado = jsonObject.getString("id");
            boolean completed = jsonObject.getBoolean("completed");

            if (!idDoctoFirmado.isEmpty() && completed) {
                DigitalDoc digitalDoc = digitalDocService.buscaPorIdDocto(idDoctoFirmado);
                if (digitalDoc != null) {
                    confirmaIdentidadVo.setSucces(true);
                    confirmaIdentidadVo.setMessage("Recibido");
                    digitalDoc.setStatus("Firmado correctamente");
                    digitalDoc.setFirmado(true);
                    digitalDoc.setMensajeFinal("Proceso terminado con exito");
                    log.info("::::::::::Recibido correctamente:::::::::");
                } else {
                    confirmaIdentidadVo.setSucces(false);
                    confirmaIdentidadVo.setMessage("No se encuentra el iddocumento firmado");
                    digitalDoc.setMensajeFinal("Documento no existe");
                    log.info("::::::::::::::::::No se encuentra el documento firmado::::::::::::::");
                }
                digitalDocService.insertarDigitalDoc(digitalDoc);
            }
        } catch (Exception e) {
            log.info(":::::::::::::Error al recibidor notificacion documento firmado:::::::::::");
        }
        return confirmaIdentidadVo;
    }

    private static Map<String, Object> createItem(int key, String name, String value) {
        Map<String, Object> item = new HashMap<>();
        item.put("key", key);
        item.put("name", name);
        item.put("value", value);
        return item;
    }

    /*private String token() {
        String token = "";
        try {
            tablaPK = new TablaPK(idTabla, "token");
            tabla = tablaService.buscarPorId(tablaPK);
            if (tabla != null) {
                if (!"".equals(tabla.getDato2())) {
                    String fechaString = tabla.getDato3();
                    LocalDate fechaActual = LocalDate.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    LocalDate fechVencimiento = LocalDate.parse(fechaString, formatter);
                    if (fechaActual.isBefore(fechVencimiento)) {
                        token = tabla.getDato2();
                    } else {
                        log.info(":::::::token vencido hay que generar otro");

                        LoginVoResponse loginVoResponse = apisHttp.login();
                        token = apisHttp.token(loginVoResponse.getClientId(), loginVoResponse.getClientSecret());
                    }
                } else {
                    log.warn("::::::::::::no existe parametro token:::::::::::::");
                }
            } else {
                log.warn(":::::::::::::::::Sin parametrizacion token::::::::::::::::");
            }
        } catch (Exception e) {
            log.error(":::::::::::::::Error al obtener token:" + e.getMessage() + "::::::::::::::::::");
        }

        return token;
    }*///Comentado el 06/11/2025 por cambio en scope y tiempo de token
}
