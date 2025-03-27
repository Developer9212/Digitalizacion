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
            String fechaApertura = "";
            String reca_recortado = "";
            String nombre = "";
            List<List<Map<String, Object>>> sequence = new ArrayList<>();

            boolean banderaDatos = false;

            banderaDatos = true;
            crearDReqVo.setName("Anexo A");

            tablaPK = new TablaPK(idTabla, "anexo_a");
            tabla = tablaService.buscarPorId(tablaPK);
            crearDReqVo.setTemplate_id(tabla.getDato2());

            OpaVo opas = util.opa(opa);

            //Aqui va el array de amortizaciones
            AuxiliarPK auxPK = new AuxiliarPK(30298, 32644, 5084);

            // Crear y agregar cada lista con su respectivo mapa
            sequence.add(Collections.singletonList(createItem(1, "abonos", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(2, "abonos_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(3, "amort_fecha_ultimo_aa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(4, "amort_fecha_ultimo_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(5, "amort_fecha_ultimo_dd_ml_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(6, "amort_fecha_ultimo_dd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(7, "amort_fecha_ultimo_ml", reca_recortado)));


            sequence.add(Collections.singletonList(createItem(9, "cat", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(10, "cat_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(11, "color_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(12, "color_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(13, "color_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(14, "conversion_meses", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(15, "cuenta_de_tdd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(16, "curp_rfc", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(17, "dia_de_hoy_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(18, "dia_de_hoy_dd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(19, "dia_de_hoy_ml", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(20, "dia_de_pago", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(21, "dia_de_pago1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(22, "dia_exp_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(23, "dia_exp_dd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(24, "dia_exp_ml", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(25, "direccion_aval1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(26, "direccion_aval1_colonia", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(27, "direccion_aval1_direccion", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(28, "direccion_aval1_municipio", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(29, "direccion_aval2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(30, "direccion_aval2_colonia", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(31, "direccion_aval2_direccion", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(32, "direccion_aval2_municipio", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(33, "direccion_gp:", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(34, "direccion_gp_direccion", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(35, "direccion_origenp", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(36, "direccion", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(37, "direccion_codigopostal", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(38, "direccion_completa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(39, "direccion_direccion", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(40, "direccion_munest", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(41, "direccion_municipio", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(42, "direccion_numero_casa_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(43, "email", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(44, "fecha_activacion:", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(45, "fecha_hoy_dia", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(46, "fecha_hoy_dia_minusculas", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(47, "fecha_hoy_mes", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(48, "fecha_hoy_mes_minusculas", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(49, "fecha_hoy", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(50, "fecha_primer_pago", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(51, "fecha_primer_pago_aa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(52, "fecha_primer_pago_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(53, "fecha_primer_pago_dd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(54, "fecha_primer_pago_ml", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(55, "fechaapertura", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(56, "fechaapertura_aaaa", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(57, "fechaapertura_dd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(58, "fechaapertura_ml", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(59, "frecuencia_de_pagos", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(60, "gerente_sucursal", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(61, "idauxiliar", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(62, "idoirgenp", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(63, "idproducto", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(64, "idsocio", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(65, "linea_de_credito_1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(66, "linea_de_credito_1_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(67, "lugarexp", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(68, "m_enganche_prestamo", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(69, "m_enganche_prestamo_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(70, "marca_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(71, "marca_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(72, "marca_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(73, "modelo_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(74, "modelo_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(75, "modelo_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(76, "monto_comision", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(77, "monto_comision_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(78, "montoprestado", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(79, "montoprestado_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(80, "montosolicitado", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(81, "montosolicitado_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(82, "nombre_aval1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(83, "nombre_aval2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(84, "nombre_aval3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(85, "nombre_codeudor", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(86, "nombre_gp", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(87, "nombre_gp2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(88, "nombre_socio", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(89, "nombre_trabajo", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(90, "nombreprod", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(95, "num_celular", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(92, "num_cuenta_tdd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(93, "num_factura_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(94, "num_plastico_tdd", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(95, "num_motor_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(96, "num_motor_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(97, "num_motor_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(98, "numserie_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(99, "numserie_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(100, "numserie_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(101, "ogs_aval1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(102, "ogs_aval2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(103, "ogs_codeudor", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(104, "ogs", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(105, "otros_conceptos", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(106, "placas_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(107, "placas_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(108, "plazo_en_dias", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(109, "porcentaje_comision", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(110, "porcentaje_comision_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(111, "porcentaje_comision2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(112, "propietario_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(113, "propietario_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(114, "propietario_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(115, "propietario", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(116, "puertas_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(117, "reca_recortado_p_64_D", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(118, "reca_recortado_p_74_D", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(119, "reca_recortado_p_79_D", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(120, "reca_recortado_p_84_D", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(121, "rellenar", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(122, "rfc_codeudor", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(123, "rfc", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(124, "simbolo_porcentaje", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(125, "tasaim_anual", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(126, "tasaim_anual_2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(127, "tasaim_anual_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(128, "tasaio_anual", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(129, "tasaio_anual_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(130, "tasaio_anual2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(131, "tasaio_anual2_letra", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(132, "telefono_aval1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(133, "telefono_aval2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(134, "telefono_codeudor", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(135, "telefono", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(136, "tipo_g1", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(137, "tipo_g2", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(138, "tipo_g3", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(139, "direccion_colonia", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(140, "finalidad", reca_recortado)));
            sequence.add(Collections.singletonList(createItem(141, "placas_g1", reca_recortado)));


            AuxiliarPK auxiliarPK = new AuxiliarPK(opas.getIdorigenp(),opas.getIdproducto(),opas.getIdauxiliar());
            List<FormatoDigital> formatos = formatoDigitalService.buscarListaPorId(auxiliarPK);

            List<List<Map<String, Object>>> sequence1 = new ArrayList<>();

            for(int i = 0;i< formatos.size();i++){
                FormatoDigital formato = formatos.get(i);
                sequence1.add(Collections.singletonList(createItem(i,formato.getVariable(),formato.getValor())));
            }


            List<Amortizacion> listaAmortizaciones = amortizacionService.buscarTodasPorId(auxPK);
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
            amortizacionData.put("key", 8);
            amortizacionData.put("name", "amortizaciones");
            amortizacionData.put("value", amortizacionesArray);
            sequence.add(Collections.singletonList(amortizacionData));

            System.out.println(sequence1);




            if (banderaDatos) {
                crearDReqVo.setType("template");
                crearDReqVo.setSequence(sequence);
                resp = apisHttp.crearDocumento(crearDReqVo);
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
