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
    private ApisHttp apisHttp;


    private Tabla tabla;
    private TablaPK tablaPK;


    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public IdentidadVoResponse identidadCrear(String ogs) {
        IdentidadVoResponse identidadVoResponse = new IdentidadVoResponse();
        try {
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
                String correo = persona.getEmail();//"julio" + sb.toString() + "@gmail.com";

                log.info("El correo es " + correo);

                SignerReqVo signer = new SignerReqVo();
                signer.setEmail(correo);//persona.getEmail());
                signer.setPhone(persona.getCelular());
                signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getAppaterno());
                signer.setStatus("pending");
                signer.setStatus_identity("active");
                signer.setFlow_id(tabla.getDato3());
                signer.setOrganization_id(tabla.getDato2());

                SignersVoReq signersVoReq = new SignersVoReq();
                List<SignerReqVo> lista = new ArrayList<>();
                lista.add(signer);
                signersVoReq.setSigners(lista);

                //Buscamos si existe un token en tablas
                tablaPK = new TablaPK(idTabla, "token");
                tabla = tablaService.buscarPorId(tablaPK);

                if (tabla != null) {
                    token();
                    identidadVoResponse = apisHttp.identidadCrear(signersVoReq);
                    if (!identidadVoResponse.isSuccess() && identidadVoResponse.getMessage().contains("AUTHORIZATION_ERROR")) {
                        token();
                        identidadVoResponse = apisHttp.identidadCrear(signersVoReq);
                    }
                } else {
                    log.warn(":::::::::::::::::No existe parametrizacion token::::::::::::");
                }
            } else {
                log.info("::::::::::::::Persona no existe con id:" + ogs);

            }
        } catch (Exception e) {
            log.error("::::::::::::Error al crear validacion de identidad:::::::::::" + e.getMessage());
        }
        return identidadVoResponse;
    }


    public ResCreaDocumentoVo resCreaDocumentoVo(String opa) {
        ResCreaDocumentoVo resp = new ResCreaDocumentoVo();
        DigitalDoc digitalDoc = new DigitalDoc();
        try {
            OpaVo opas = util.opa(opa);
            AuxiliarPK auxiliarPK = new AuxiliarPK(opas.getIdorigenp(), opas.getIdproducto(), opas.getIdauxiliar());

            //Verificamos que el folio exista en auxiliares y que este en estatus capturado
            Auxiliar a = auxiliarService.buscarPorId(auxiliarPK);
            if (a != null) {
                if (a.getEstatus() == 0) {
                    List<List<Map<String, Object>>> sequence1 = new ArrayList<>();
                    //Si esta el auxiliar ahora buscamos que esten las variables listas para el contrato
                    List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);
                    if (formatos.size() > 0) {
                        //Ya nos aseguramos que ya esten las variables en la tabla ahora si creamos la identidad
                        //Antes llenamos la tabla para preparar respuestas de legalario

                        digitalDoc.setAuxiliarPK(a.getPk());
                        digitalDoc.setFecha_captura(new Date());
                        digitalDoc.setStatus("Pendiente");
                        digitalDoc.setOk_identidad(false);
                        digitalDoc.setFirmado(false);

                        digitalDocService.insertarDigitalDoc(digitalDoc);

                        //Una ves guardado el registros enviamos la creacion de identidad
                        Persona p = new Persona();
                        PersonaPK personaPK = new PersonaPK(a.getIdorigen(), a.getIdgrupo(), a.getIdsocio());
                        Persona persona = personaService.buscarPorId(personaPK);

                        String ogs = util.ogs(persona.getPk());
                        //Consumimos metodo de crear Identidad
                        IdentidadVoResponse identidadVoResponse = identidadCrear(ogs);

                        ObjectMapper mapper = new ObjectMapper();
                        JsonNode dataArray = mapper.valueToTree(identidadVoResponse.getData());

                        if (identidadVoResponse.isSuccess()) {
                            digitalDoc = digitalDocService.buscarDigitalDocPorId(a.getPk());
                            JsonNode firstElement = dataArray.get(0);
                            String id = firstElement.get("_id").asText();
                            digitalDoc.setIdidentidad(id);
                            digitalDoc.setMensajeFinal("Identidad creada:"+new Date());
                            digitalDocService.insertarDigitalDoc(digitalDoc);
                            log.info("::::::::::Se envio la identidad::::::::::::");
                            resp.setSuccess(true);
                            resp.setMessage("Identidad creada con exito:" + id);
                        } else {
                            log.error(":::::::::::::::::::::" + identidadVoResponse.getMessage() + ":::::::::::::::::::");
                            digitalDoc.setMensajeFinal(identidadVoResponse.getMessage()+":"+new Date());
                            digitalDocService.insertarDigitalDoc(digitalDoc);
                            resp.setSuccess(false);
                            resp.setMessage(identidadVoResponse.getMessage());
                        }
                    }
                } else {
                    log.info("::::::::::::::Estatus folio debe ser capturado:::::::::::::");
                    digitalDoc.setMensajeFinal("Estatus folio debe ser capturado:"+new Date());
                    digitalDocService.insertarDigitalDoc(digitalDoc);
                }
            } else {
                log.info(":::::::::::::::No existe folio capturado::::::::::::::::");
                digitalDoc.setMensajeFinal("No existe folio capturado:"+new Date());
                digitalDocService.insertarDigitalDoc(digitalDoc);
            }


        } catch (Exception e) {
            log.error("Error al crear Documento para opa:" + opa + "," + e.getMessage() + ":::::::::::::::");
            resp.setMessage("Error al crear Documento:" + e.getMessage());
            digitalDoc.setMensajeFinal(e.getMessage()+":"+new Date());
            digitalDocService.insertarDigitalDoc(digitalDoc);
        }
        return resp;
    }


    public ConfirmaIdentidadVo confirmaIdentidadVo(ConfirmaIdentidadReqVo confirmaIdentidadReqVo) {
        ConfirmaIdentidadVo confirmaIdentidadVo = new ConfirmaIdentidadVo();
        DigitalDoc digitalDoc = new DigitalDoc();
        try {
            CrearDReqVo crearDReqVo = new CrearDReqVo();
            digitalDoc = digitalDocService.buscaPorIdIdentidad(confirmaIdentidadReqVo.getId());

            if (digitalDoc != null) {
                log.info("Se encuentra la identidad");
                //digitalDocService.insertarDigitalDoc(digitalDoc);
                //Una ves guardada la identidad vamos a enviar las variables para el documento
                List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(digitalDoc.getAuxiliarPK());

                List<List<Map<String, Object>>> sequence = new ArrayList<>();
                boolean banderaDatos = false;
                tablaPK = new TablaPK(idTabla, "contrato_aniversario");
                tabla = tablaService.buscarPorId(tablaPK);
                crearDReqVo.setName(tabla.getPk().getIdelemento());
                crearDReqVo.setTemplate_id(tabla.getDato2());

                for (int i = 0; i < formatos.size(); i++) {
                    FormatoDigital formato = formatos.get(i);
                    String etiqueta = formato.getEtiqueta().replaceAll("[<>]", "").replaceAll("\\|$", "").replace("|", "_").replace("__", "_").replace(".sql", "");
                    if (!etiqueta.toUpperCase().contains("AMORTIZACIONES")) {
                        sequence.add(Collections.singletonList(createItem(formato.getIdkey(), etiqueta.replace("_+$", "").trim(), formato.getValor().trim())));
                    } else {
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


                        for (Amortizacion amortizacion : listaAmortizaciones) {
                            columnasMap.get("NUM_P").add(amortizacion.getId());
                            columnasMap.get("FECHA_CORTE_PAGO").add(amortizacion.getVence());
                            columnasMap.get("ABONO_PRINCIPAL").add( amortizacion.getAbono());
                            columnasMap.get("ANUALIDAD").add(amortizacion.getAnualidad());
                            columnasMap.get("SALDO_INSOLUTO").add(0.00);
                            columnasMap.get("INTERES_ORDINARIO").add(amortizacion.getIo());
                            columnasMap.get("IVA_INTERESES").add(0.00);
                            columnasMap.get("TOTAL_PAGAR").add(0.00);
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
                    crearDReqVo.setType("template");
                    crearDReqVo.setSequence(sequence);
                    ResCreaDocumentoVo resp = apisHttp.crearDocumento(crearDReqVo);
                    if (!resp.isSuccess() && resp.getMessage().contains("AUTHORIZATION_ERROR")) {
                        token();
                        resp = apisHttp.crearDocumento(crearDReqVo);
                    }

                    log.info(":::::::::::::::::::::Se creo el documento::::::::::::::::::");
                    //Una ves creado el documento actualizamos la tabla y enviamos a los firmantes
                    if (resp.isSuccess()) {
                        digitalDoc.setIddocto_creado(resp.getData().getId());
                        digitalDoc.setOk_docto_creado(true);
                        confirmaIdentidadVo.setSucces(true);
                        confirmaIdentidadVo.setMessage(resp.getMessage());
                        digitalDoc.setOk_identidad(true);
                        digitalDoc.setMensajeFinal("Documento creado exitosamente"+":"+new Date());

                        log.info("::::::::::::::Documento enviado,enviaremos a firmantes::::::::::::::::::::");

                        ResSignersVo resSignersVo = enviarFirmantes(digitalDoc.getAuxiliarPK());

                        if (resSignersVo.isSuccess()) {
                            confirmaIdentidadVo.setSucces(true);
                            confirmaIdentidadVo.setMessage(resSignersVo.getMessage());
                            digitalDoc.setMensajeFinal(resSignersVo.getMessage()+":"+new Date());
                            log.info(":::::::::::::::::Mensaje envio a firmantes:"+resSignersVo.getMessage()+"::::::::::::::::::::::");
                            digitalDoc.setEnviado_firmantes(true);
                        }else{
                            digitalDoc.setMensajeFinal(resSignersVo.getMessage());
                            digitalDoc.setEnviado_firmantes(false);
                        }
                    } else {
                        confirmaIdentidadVo.setMessage(resp.getMessage());
                        digitalDoc.setMensajeFinal(resp.getMessage()+":"+new Date());
                    }
                }
            }
        } catch (Exception e) {
            log.error("::::::::::::Error al confirmar identidad:::::::::::::" + e.getMessage());
            digitalDoc.setOk_identidad(false);
            digitalDoc.setOk_docto_creado(false);
            digitalDoc.setMensajeFinal("Error al confirmar identidad:::::::::::::" + e.getMessage()+":"+new Date());

        }
        digitalDocService.insertarDigitalDoc(digitalDoc);
        return confirmaIdentidadVo;
    }


    public ResSignersVo enviarFirmantes(AuxiliarPK opa) {
        ResSignersVo resSignersVo = new ResSignersVo();
        try {
            //OpaVo opaVo = util.opa(opa);
            //AuxiliarPK auxiliarPK = new AuxiliarPK(opaVo.getIdorigenp(), opaVo.getIdproducto(), opaVo.getIdauxiliar());
            DigitalDoc digitalDoc = digitalDocService.buscarDigitalDocPorId(opa);
            if (digitalDoc != null) {
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

                    System.out.println("gfdgfdgfdg");
                    for (int i = 0; i < formatos.size(); i++) {
                        FormatoDigital formato = formatos.get(i);
                        //Vamos a enviar a firmantes verificamos que personas debemos enviar
                        if (formato.getEtiqueta().contains("ogs_codeudor") && !formato.getValor().isEmpty()) {
                            ogsCodeudor = formato.getValor().replace("-", "");
                            log.info("Ogs codeudor: " + ogsCodeudor);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval1") && !formato.getValor().isEmpty()) {
                            ogsAval1 = formato.getValor().replace("-", "");
                            log.info("Ogs aval1: " + ogsAval1);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval2") && !formato.getValor().isEmpty()) {
                            ogsAval2 = formato.getValor().replace("-", "");
                            log.info("Ogs aval2: " + ogsAval2);
                        }

                        if (formato.getEtiqueta().contains("ogs_aval3") && !formato.getValor().isEmpty()) {
                            ogsAval3 = formato.getValor().replace("-", "");
                            log.info("Ogs aval3: " + ogsAval3);
                        }
                    }

                    Signer signer = new Signer();
                    OgsVo ogsVo = new OgsVo();

                    if(!persona.getEmail().isEmpty()){
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    }

                    System.out.println("padso asasdasodso");
                    if (!ogsCodeudor.isEmpty()) {
                        ogsVo = util.ogs(ogsCodeudor);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    } else if (!ogsAval1.isEmpty()) {
                        ogsVo = util.ogs(ogsAval1);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    } else if (!ogsAval2.isEmpty()) {
                        ogsVo = util.ogs(ogsAval2);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    }else if (!ogsAval3.isEmpty()) {
                        ogsVo = util.ogs(ogsAval2);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    }


                    log.info("::::::::::Req:"+resSignersVo);

                    signersReqVo.setSigners(signers);

                    if(!signersReqVo.getSigners().isEmpty()){
                        signersReqVo.setSigners(signers);
                        log.info("::::::::::::Vamos a enviar a firmantes:::::::::::::::");
                        System.out.println(":::::::Tu peticion:::"+signersReqVo);
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
                log.info("::::::::::::Es el documento no existe::::::::::::::");
                resSignersVo.setMessage("El documento no existe");
                digitalDoc.setMensajeFinal("Documento no existe");
            }
           digitalDocService.insertarDigitalDoc(digitalDoc);
        } catch (Exception e) {
            log.error(":::::::::::::Sucedio un error al enviar a firmar:::::::::" + e.getMessage());
            resSignersVo.setMessage("Error al enviar a firmantes:"+e.getMessage());

        }
        return resSignersVo;
    }

    public ConfirmaIdentidadVo confirmaFirmacontrato(String cadena) {
        ConfirmaIdentidadVo confirmaIdentidadVo = new ConfirmaIdentidadVo();

        try {
            JSONObject json = new JSONObject(cadena);
            JSONObject jsonObject = json.getJSONObject("document");
            String idDoctoFirmado = jsonObject.getString("id");
            boolean completed  = jsonObject.getBoolean("completed");

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

    private String token() {
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
    }
}
