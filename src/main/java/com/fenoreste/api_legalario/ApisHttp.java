package com.fenoreste.api_legalario;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fenoreste.entity.Tabla;
import com.fenoreste.entity.TablaPK;
import com.fenoreste.model.*;
import com.fenoreste.service.ITablaService;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.MediaType;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class ApisHttp {

    private String basePath = "https://api.legalario.com/";
    private String pathLogin = "auth/login";
    private String pathToken = "auth/token";

    //Sandbox Testing
    private String basePathS = "https://sandbox-api.legalario.com/";
    private String pathLoginS = "auth/login";
    private String pathTokenS = "auth/token";
    private String pathIdentidadS = "v2/identities";
    private String pathCrearDocumentoS = "v2/documents";
    private String pathEnviarFirmantes = "v2/signers";

    OkHttpClient client = null;
    MediaType mediaType = null;
    RequestBody body = null;
    Request request = null;
    Response response = null;

    @Autowired
    private static RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private SSLUtil sslUtil;

    @Autowired
    private ITablaService tablaService;

    private TablaPK tablaPK;
    private Tabla tabla;

    private ObjectMapper mapper = new ObjectMapper();


    public LoginVoResponse login() {
        String resultado = "";
        LoginVoResponse loginVoResponse = new LoginVoResponse();
        try {
            tablaPK = new TablaPK("digitalizacion", "login_credentials");
            tabla = tablaService.buscarPorId(tablaPK);
            sslUtil.disableSSLCertificateChecking();

            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();

            mediaType = MediaType.parse("application/x-www-form-urlencoded");
            body = RequestBody.create(mediaType, "email=ctello@csn.coop&password=HcJV6W85L8d9");

            String url = basePathS + pathLoginS;
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();

            JsonObject jsonObject = JsonParser.parseString(resultado).getAsJsonObject();
            JsonObject data = jsonObject.getAsJsonObject("data");

            loginVoResponse.setClientId(data.get("client_id").getAsString());
            loginVoResponse.setClientSecret(data.get("client_secret").getAsString());
        } catch (Exception e) {
            log.error("::::Error al realizar login:" + e.getMessage() + "::::::::::::::");
        }
        return loginVoResponse;
    }


    public String token(String clientId, String clientSecret) {
        String resultado = "";

        try {

            sslUtil.disableSSLCertificateChecking();
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();
            mediaType = MediaType.parse("application/x-www-form-urlencoded");
            body = RequestBody.create(mediaType, "client_id=" + clientId +
                    "&client_secret=" + clientSecret + "&grant_type=client_credentials&scope=customers");

            String url = basePathS + pathTokenS;
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body).addHeader("Content-Type", "application/x-www-form-urlencoded").build();
            response = client.newCall(request).execute();
            resultado = response.body().string();
            log.info("Resultado token: " + resultado);
            tabla = new Tabla();
            tablaPK = new TablaPK("digitalizacion", "token");
            JsonObject jsonObject = JsonParser.parseString(resultado).getAsJsonObject();
            JsonObject data = jsonObject.getAsJsonObject("data");

            tabla.setDato2(data.get("access_token").getAsString());
            long expiresIn = data.get("expires_in").getAsLong();

            Instant ahora = Instant.now(); // Fecha actual
            Instant instant = ahora.plusSeconds(expiresIn); // Sumar segundos

            LocalDate fechaFinal = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String fechaFormateada = fechaFinal.format(formatter);

            tabla.setDato3(fechaFormateada);
            tabla.setPk(tablaPK);
            tablaService.insertar(tabla);
        } catch (Exception e) {
            log.error("::::Error al obtener token http:" + e.getMessage() + "::::::::::::::");
        }
        return resultado;
    }

    public IdentidadVoResponse identidadCrear(SignersVoReq signersVoReq) {
        String resultado = "";
        IdentidadVoResponse identidadVoResponse = new IdentidadVoResponse();
        try {
            tablaPK = new TablaPK("digitalizacion", "token");
            tabla = tablaService.buscarPorId(tablaPK);
            sslUtil.disableSSLCertificateChecking();
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();
            String json = mapper.writeValueAsString(signersVoReq);
            mediaType = MediaType.parse("application/application/json");
            body = RequestBody.create(mediaType, json);
            String url = basePathS + pathIdentidadS;
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + tabla.getDato2())
                    .build();

            response = client.newCall(request).execute();
            resultado = response.body().string();
            identidadVoResponse = mapper.readValue(resultado, IdentidadVoResponse.class);

            if(!identidadVoResponse.isSuccess()) {
                identidadVoResponse.setMessage(identidadVoResponse.getData().get(0).asText());
            }

            log.info(":::::::::::Resultado identidad crear:::::::::::::::::::" + resultado + "," + identidadVoResponse);
        } catch (Exception e) {
            log.error("::::Error al crear identidad:" + e.getMessage() + "::::::::::::::");
        }
        return identidadVoResponse;
    }

    public ResCreaDocumentoVo crearDocumento(CrearDReqVo crearDocumento) {
        String resultado = "";
        ResCreaDocumentoVo creaDocumentoVo = new ResCreaDocumentoVo();
        try {
            tablaPK = new TablaPK("digitalizacion", "token");
            tabla = tablaService.buscarPorId(tablaPK);
            sslUtil.disableSSLCertificateChecking();
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();
            String json = mapper.writeValueAsString(crearDocumento);

            log.info("Json a enviar es:"+json);
            mediaType = MediaType.parse("application/application/json");
            body = RequestBody.create(mediaType, json);
            String url = basePathS + pathCrearDocumentoS;
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + tabla.getDato2())
                    .build();

            response = client.newCall(request).execute();
            resultado = response.body().string();
            Gson gson = new Gson();
            JsonObject res = gson.fromJson(resultado, JsonObject.class);

            log.info(":::::::::::Resultado documento crear:::::::::::::::::::" + resultado);

            if(res.get("success").getAsBoolean()) {
                creaDocumentoVo = mapper.readValue(resultado, ResCreaDocumentoVo.class);
            }else{
                System.out.println("Si aqui");
                creaDocumentoVo.setSuccess(false);
                creaDocumentoVo.setMessage(res.get("message").getAsString());
            }

        } catch (Exception e) {
            log.error("::::Error al crear Documento:" + e.getMessage() + "::::::::::::::");
        }
        return creaDocumentoVo;
    }

    public ResSignersVo firmantes(SignersVoReq signersVoReq) {
        ResSignersVo resSignersVo = new ResSignersVo();

        try {
            sslUtil.disableSSLCertificateChecking();
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, java.util.concurrent.TimeUnit.SECONDS) // Tiempo de espera para establecer la conexión
                    .readTimeout(20, java.util.concurrent.TimeUnit.SECONDS)    // Tiempo de espera para la lectura de datos
                    .build();
            String json = mapper.writeValueAsString(resSignersVo);

            log.info("Json signers a enviar es:"+json);
            mediaType = MediaType.parse("application/application/json");
            body = RequestBody.create(mediaType, json);
            String url = basePathS + pathEnviarFirmantes;
            request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + tabla.getDato2())
                    .build();

            response = client.newCall(request).execute();
            String resultado = response.body().string();
            Gson gson = new Gson();
            JsonObject res = gson.fromJson(resultado, JsonObject.class);
            log.info(":::::::::::Resultado signers:::::::::::::::::::" + resultado);

            if(res.get("success").getAsBoolean()) {
                resSignersVo = mapper.readValue(resultado, ResSignersVo.class);
            }else{
                resSignersVo.setSuccess(false);
                resSignersVo.setMessage(res.get("message").getAsString());
            }
        }catch (Exception e) {
             log.error(":::::::::::::::::::::::::::Error al enviar a firmantes::::::::::::::::::::::::");
        }

        return resSignersVo;
    }

}
