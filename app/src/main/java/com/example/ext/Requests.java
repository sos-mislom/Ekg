package com.example.ext;



import java.io.IOException;
import java.util.Dictionary;
import java.util.Map;
import java.util.Objects;

import okhttp3.*;

class Get {
    static final OkHttpClient client = new OkHttpClient();

    public static String get(String url, Dictionary<String, String> timestemp, String username, String password) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .addHeader("Cookie", "ys-userId=n%3A832; ys-user=s%3A" + username + "; ys-password=s%3A" + password)
                .addHeader("DNT", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 YaBrowser/20.4.2.197 Yowser/2.5 Safari/537.36")
                .addHeader("Host", "176.215.5.226:8082")
                .addHeader("Referer", "http://176.215.5.226:8082/")
                .build();
        try (okhttp3.Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(((Response) response).body()).string();
        }
    }
}


class Post {
    static final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(chain -> {
                Request request = chain.request().newBuilder().addHeader("Connection", "close").build();
                return chain.proceed(request);
                })
            .build();
    static String post(String url, Map<String, String> json, String username, String password) throws IOException {

        FormBody.Builder body = new FormBody.Builder();
        for (Map.Entry<String, String> e : json.entrySet()) {
            body.add(e.getKey(), e.getValue());
        }
        Request request = new Request.Builder()
                .url(url)
                .post(body.build())
                .addHeader("Accept", "*/*")
                .addHeader("Connection", "keep-alive")
                .addHeader("DNT", "1")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 YaBrowser/20.4.2.197 Yowser/2.5 Safari/537.36")
                .addHeader("Host", "176.215.5.226:8082")
                .addHeader("Referer", "http://176.215.5.226:8082/")
                .addHeader("Cookie", "ys-userId=n%3A832; ys-user=s%3A" + username + "; ys-password=s%3A" + password)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            return Objects.requireNonNull(((Response) response).body()).string();
        }
    }

}