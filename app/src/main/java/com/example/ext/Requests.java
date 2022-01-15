package com.example.ext;



import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class Post {
    private final String username;
    private final String password;
    public Post(String password, String username){
        this.username = username;
        this.password = password;
    }
    static final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(chain -> {
                Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                return chain.proceed(request);
            })
            .build();

    public Response get(String url) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Cookie", "ys-userId=n%3A832; ys-user=s%3A" + this.username + "; ys-password=s%3A" + this.password)
                    .build();
            okhttp3.Response response = client.newCall(request).execute();
            assert response.body() != null;
            return new Response(response.body().string(), response.code());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Response post(String url, Map<String, String> json) throws IOException {
        try {
            OkHttpClient client = new OkHttpClient();
            FormBody.Builder body = new FormBody.Builder();
            for (Map.Entry<String, String> e : json.entrySet()) {
                body.add(e.getKey(), e.getValue());
            }
            Request request = new Request.Builder()
                    .url(url)
                    .post(body.build())
                    .addHeader("Cookie", "ys-userId=n%3A832; ys-user=s%3A" + this.username + "; ys-password=s%3A" + this.password)
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
class Response {
    private final String string;
    private final int statusCode;

    public Response(String string, int statusCode) {
        this.string = string;
        this.statusCode = statusCode;
        Log.e("REQ", string);
    }

    public JSONObject json(){
        return ProcJSON.decode(string);
    }

    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return string;
    }
}