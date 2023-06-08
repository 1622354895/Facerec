package com.example.camera2_1;


import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OneNetAPIOrder {
    public String orderBackMessage = null;

    public String sendOrder(String order) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, order);
        Request request = new Request.Builder()
                .url("http://api.heclouds.com/cmds?device_id=1085623683")
                .method("POST", body)
                .addHeader("api-key", "ZagDdB6GO5EL7fDAIyg8EAMHe5o=")
                .addHeader("Content-Type", "text/plain")
                .build();
        Response response = client.newCall(request).execute();
        orderBackMessage = response.body().string();
        try {
            return orderBackMessage;
        }catch (Exception e){
            System.out.println("order"+e);
        }
        return null;
    }
}
