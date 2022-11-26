package com.example.ext.helper;


import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class Post {
    static final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(chain -> {
                Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                return chain.proceed(request);
            })
            .build();

    public Response post(String url, Map<String, String> json) {
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder body = new FormBody.Builder();
            for (Map.Entry<String, String> e : json.entrySet()) {
                body.add(e.getKey(), e.getValue());
            }

            Request request = new Request.Builder()
                    .addHeader("Connection", "keep-alive")
                    .addHeader("Content-Type", "application/json")
                    .url(url)
                    .post(body.build())
                    .build();

            okhttp3.Response response = client.newCall(request).execute();
            assert response.body() != null;
            return new Response(response.body().string(), response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
