package com.limetray.limetrayassignment.Api;

import com.limetray.limetrayassignment.Interfaces.RestCallback;


import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by PG on 21/08/16.
 */
public class JsonBlobApiClient {

    private static final String BASE_URL = "https://jsonblob.com/api/jsonBlob/";
    public static final OkHttpClient client = new OkHttpClient();


    public static void get(String url, RestCallback callback) {
        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .build();
        enqueue(request,callback);
    }

    public static void put(String url,String jsonBody,RestCallback callback){

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, jsonBody);

        Request request = new Request.Builder()
                .url(getAbsoluteUrl(url))
                .put(body) //PUT
                .build();
        enqueue(request,callback);

    }

    public static String getAbsoluteUrl(String url){
      return BASE_URL+url;
    }

    public static void enqueue(Request request, final RestCallback callback) {

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e);
            }

            @Override
            public void onResponse(Call call, final Response response) {
                if (!response.isSuccessful()) {
                    callback.onError(new IOException("Unexpected code " + response));
                } else {
                    String res = null;
                    try {
                        final ResponseBody body = response.body();
                        res = body.string();
                        body.close();
                        callback.onSuccess(res);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


}


