package com.fenoreste.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fenoreste.api_legalario.ApisHttp;
import com.fenoreste.entity.*;
import com.fenoreste.model.*;
import com.fenoreste.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public IdentidadVoResponse identidadCrear(String ogs) {
        IdentidadVoResponse identidadVoResponse = new IdentidadVoResponse();
        try {
            OgsVo ogsVo = util.ogs(ogs);
            PersonaPK personaPK = new PersonaPK(ogsVo.getIdorigen(), ogsVo.getIdgrupo(), ogsVo.getIdsocio());
            Persona persona = personaService.buscarPorId(personaPK);

            if (persona != null) {
                tablaPK = new TablaPK(idTabla, "signer_data");
                tabla = tablaService.buscarPorId(tablaPK);

                SignerReqVo signer = new SignerReqVo();
                signer.setEmail(persona.getEmail());
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
                        DigitalDoc digitalDoc = new DigitalDoc();
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
                            digitalDocService.insertarDigitalDoc(digitalDoc);
                            log.info("::::::::::Se envio la identidad::::::::::::");
                            resp.setSuccess(true);
                            resp.setMessage("Identidad creada con exito");
                        } else {
                            log.error(":::::::::::::::::::::" + identidadVoResponse.getMessage() + ":::::::::::::::::::");
                        }
                    }
                } else {
                    log.info("::::::::::::::Estatus folio debe ser capturado:::::::::::::");
                }
            } else {
                log.info(":::::::::::::::No existe folio capturado::::::::::::::::");
            }


        } catch (Exception e) {
            log.error("Error al crear Documento para opa:" + opa + "," + e.getMessage() + ":::::::::::::::");
            resp.setMessage("Error al crear Documento:" + e.getMessage());
        }
        return resp;
    }


    public ConfirmaIdentidadVo confirmaIdentidadVo(ConfirmaIdentidadReqVo confirmaIdentidadReqVo) {
        ConfirmaIdentidadVo confirmaIdentidadVo = new ConfirmaIdentidadVo();
        try {
            CrearDReqVo crearDReqVo = new CrearDReqVo();
            DigitalDoc digitalDoc = digitalDocService.buscaPorIdIdentidad(confirmaIdentidadReqVo.getIdidentidad());

            if (digitalDoc != null) {
                log.info("Se encuentra la identidad");
                digitalDoc.setOk_identidad(true);
                digitalDocService.insertarDigitalDoc(digitalDoc);
                //Una ves guardada la identidad vamos a enviar las variables para el documento
                List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(digitalDoc.getAuxiliarPK());

                List<List<Map<String, Object>>> sequence = new ArrayList<>();
                boolean banderaDatos = false;
                //crearDReqVo.setName("Anexo A");
                tablaPK = new TablaPK(idTabla, "anexo_a");
                tabla = tablaService.buscarPorId(tablaPK);
                crearDReqVo.setTemplate_id(tabla.getDato2());

                for (int i = 0; i < formatos.size(); i++) {
                    FormatoDigital formato = formatos.get(i);
                    String etiqueta = formato.getEtiqueta().replaceAll("[<>]", "").replaceAll("\\|$", "").replace("|", "_").replace("__", "_").replace(".sql", "");
                    if (!etiqueta.toUpperCase().contains("AMORTIZACIONES")) {
                        sequence.add(Collections.singletonList(createItem(formato.getIdkey(), etiqueta.replace("_+$", ""), formato.getValor())));
                    } else {
                        List<Amortizacion> listaAmortizaciones = amortizacionService.buscarTodasPorId(digitalDoc.getAuxiliarPK());
                        List<Map<String, Object>> amortizacionesArray = new ArrayList<>();
                        for (Amortizacion amortizacion : listaAmortizaciones) {
                            Map<String, Object> amortizacionMap = new HashMap<>();
                            amortizacionMap.put("idamortizacion", amortizacion.getId()); // Reemplaza con los atributos reales
                            amortizacionMap.put("vence", amortizacion.getVence());
                            amortizacionMap.put("abono", amortizacion.getAbono());
                            amortizacionMap.put("io", amortizacion.getIo());
                            amortizacionMap.put("iva", 0.0);
                            amortizacionMap.put("anualidad", 0.0);
                            amortizacionMap.put("total", 0.0);
                            amortizacionMap.put("numero_pago", 0.0);
                            amortizacionMap.put("abono_principal", 0.0);
                            amortizacionMap.put("saldo_insoluto", 0.0);
                            amortizacionMap.put("ios", 0.0);
                            amortizacionMap.put("ivas", 0.0);
                            amortizacionMap.put("monto_total", 0.0);
                            amortizacionesArray.add(amortizacionMap);
                        }

                        Map<String, Object> amortizacionData = new HashMap<>();
                        amortizacionData.put("key", formato.getIdkey());
                        amortizacionData.put("name", "amortizaciones");
                        amortizacionData.put("value", amortizacionesArray);
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
                    digitalDoc.setIddocto_creado(resp.getData().getId());
                    digitalDoc.setOk_docto_creado(true);
                    digitalDocService.insertarDigitalDoc(digitalDoc);


                }
            }
        } catch (Exception e) {
            log.error("::::::::::::Error al confirmar identidad:::::::::::::" + e.getMessage());
        }
        return confirmaIdentidadVo;
    }


    public ResSignersVo enviarFirmantes(String opa) {
        ResSignersVo resSignersVo = new ResSignersVo();
        try {
            OpaVo opaVo = util.opa(opa);
            AuxiliarPK auxiliarPK = new AuxiliarPK(opaVo.getIdorigenp(), opaVo.getIdproducto(), opaVo.getIdauxiliar());
            DigitalDoc digitalDoc = digitalDocService.buscarDigitalDocPorId(auxiliarPK);
            if (digitalDoc != null) {
                if (!digitalDoc.isFirmado()) {
                    SignersReqVo signersReqVo = new SignersReqVo();
                    signersReqVo.setDocument_id(digitalDoc.getIddocto_creado());
                    signersReqVo.setWorkflow(true);
                    signersReqVo.setUse_whatsapp(false);
                    List<Signer> signers = new ArrayList<>();

                    Auxiliar a = auxiliarService.buscarPorId(auxiliarPK);
                    PersonaPK personaPK = new PersonaPK(a.getIdorigen(),a.getIdgrupo(),a.getIdsocio());
                    Persona persona = personaService.buscarPorId(personaPK);

                    List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);

                    String correoSocio = persona.getEmail();
                    String ogsAval1 = "";
                    String ogsAval2 = "";
                    String ogsCodeudor = "";

                    for(int i=0;i<formatos.size();i++){
                        FormatoDigital formato = formatos.get(i);
                        //Vamos a enviar a firmantes verificamos que personas debemos enviar
                        if(formato.getEtiqueta().contains("ogs_codeudor") && !formato.getValor().isEmpty()){
                            ogsCodeudor = formato.getValor().replace("-","");
                            log.info("Ogs codeudor: "+ogsCodeudor);
                        }

                        if(formato.getEtiqueta().contains("ogs_aval1") && !formato.getValor().isEmpty()){
                            ogsAval1 = formato.getValor().replace("-","");
                            log.info("Ogs aval1: "+ogsAval1);
                        }

                        if(formato.getEtiqueta().contains("ogs_aval2") && !formato.getValor().isEmpty()){
                            ogsAval2 = formato.getValor().replace("-","");
                            log.info("Ogs aval2: "+ogsAval2);
                        }
                    }

                    Signer signer = new Signer();

                    OgsVo ogsVo = new OgsVo();
                    if(!ogsCodeudor.isEmpty()){
                        ogsVo = util.ogs(ogsCodeudor);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(),ogsVo.getIdgrupo(),ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() +" " + persona.getAppaterno() +" " +persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    } else if(!ogsAval1.isEmpty()){
                        ogsVo = util.ogs(ogsAval1);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(),ogsVo.getIdgrupo(),ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() +" " + persona.getAppaterno() +" " +persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    } else if (!ogsAval2.isEmpty()) {
                        ogsVo = util.ogs(ogsAval2);
                        personaPK = new PersonaPK(ogsVo.getIdorigen(),ogsVo.getIdgrupo(),ogsVo.getIdsocio());
                        persona = personaService.buscarPorId(personaPK);
                        signer.setEmail(persona.getEmail());
                        signer.setFullname(persona.getNombre() +" " + persona.getAppaterno() +" " +persona.getApmaterno());
                        signer.setRole("FIRMANTE");
                        signer.setType("FIRMA");
                        signers.add(signer);
                    }

                    ResSignersVo signerResVo = apisHttp.firmantes(signersReqVo);


                } else {
                    log.info(":::::::::::::El documento ya esta firmado::::::::::::::");
                }
            } else {
                log.info("::::::::::::Es el documento no existe::::::::::::::");
            }

        } catch (Exception e) {
            log.error(":::::::::::::Sucedio un error al enviar a firmar:::::::::"+e.getMessage());
        }
        return resSignersVo;
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
