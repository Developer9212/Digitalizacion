package com.fenoreste.util;

import okhttp3.*;

import java.io.IOException;

public class TestMain {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        RequestBody body = RequestBody.create(mediaType, "email=ctello@csn.coop&password=HcJV6W85L8d9");
        Request request = new Request.Builder()
                .url("https://sandbox-api.legalario.com/auth/login")
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();
        Response response = client.newCall(request).execute();
        System.out.println("Response:"+response.body().string());
    }
}
