package com.fenoreste.service;

import com.fenoreste.api_legalario.ApisHttp;
import com.fenoreste.entity.*;
import com.fenoreste.model.*;
import com.fenoreste.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

                SignerVo signer = new SignerVo();
                signer.setEmail(persona.getEmail());
                signer.setPhone(persona.getCelular());
                signer.setFullname(persona.getNombre() + " " + persona.getAppaterno() + " " + persona.getAppaterno());
                signer.setStatus("pending");
                signer.setStatus_identity("active");
                signer.setFlow_id(tabla.getDato3());
                signer.setOrganization_id(tabla.getDato2());

                SignersVoReq signersVoReq = new SignersVoReq();
                List<SignerVo> lista = new ArrayList<>();
                lista.add(signer);
                signersVoReq.setSigners(lista);

                //Buscamos si existe un token en tablas
                tablaPK = new TablaPK(idTabla, "token");
                tabla = tablaService.buscarPorId(tablaPK);

                if (tabla != null) {
                    token();
                    identidadVoResponse = apisHttp.identidadCrear(signersVoReq);
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
        CrearDReqVo crearDReqVo = new CrearDReqVo();
        try {

            List<List<Map<String, Object>>> sequence = new ArrayList<>();

            boolean banderaDatos = false;

            banderaDatos = true;
            crearDReqVo.setName("Anexo A");

            tablaPK = new TablaPK(idTabla, "anexo_a");
            tabla = tablaService.buscarPorId(tablaPK);
            crearDReqVo.setTemplate_id(tabla.getDato2());

            OpaVo opas = util.opa(opa);
            AuxiliarPK auxiliarPK = new AuxiliarPK(opas.getIdorigenp(),opas.getIdproducto(),opas.getIdauxiliar());
            List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);

            List<List<Map<String, Object>>> sequence1 = new ArrayList<>();
            for(int i = 1;i< formatos.size();i++){
                FormatoDigital formato = formatos.get(i);
                String etiqueta = formato.getEtiqueta().replaceAll("[<>]", "").replaceAll("\\|$", "").replace("|","_").replace("__","_").replace(".sql","");
                if(!etiqueta.toUpperCase().contains("AMORTIZACIONES")) {
                    sequence1.add(Collections.singletonList(createItem(i,etiqueta.replace("_+$",""),formato.getValor())));
                }

            }


            List<Amortizacion> listaAmortizaciones = amortizacionService.buscarTodasPorId(auxiliarPK);
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
            amortizacionData.put("key", 6);
            amortizacionData.put("name", "amortizaciones");
            amortizacionData.put("value", amortizacionesArray);
            sequence1.add(Collections.singletonList(amortizacionData));


            System.out.println(sequence1);

            if (banderaDatos) {
                crearDReqVo.setType("template");
                crearDReqVo.setSequence(sequence1);
                resp = apisHttp.crearDocumento(crearDReqVo);

                if(!resp.isSuccess() && resp.getMessage().contains("AUTHORIZATION_ERROR")){
                    token();
                    resp = apisHttp.crearDocumento(crearDReqVo);
                }
                System.out.println("Respuesta:"+resp);

            } else {
                resp.setSuccess(false);
                resp.setMessage("Error al crear Documento");

            }

        } catch (Exception e) {
            log.error("Error al crear Documento con id:" + crearDReqVo.getTemplate_id() + "," + e.getMessage());
            resp.setMessage("Error al crear Documento");
        }
        return resp;
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
