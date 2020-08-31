package com.karungkung.klinik;

import android.content.Context;
import android.util.Log;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hanif on 23/09/18.
 */

public class ApiClient {
    private static Retrofit retrofit;
//    public static final String BASE_URL = "http://10.0.2.2/Karungkung/klinik_ws/";

    public static Retrofit getClient(Context context, boolean authBearer) {
        SessionManager sm = new SessionManager(context);

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder client = new OkHttpClient.Builder();
        if(authBearer){
            Interceptor headerAuthorizationInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Headers headers = request.headers().newBuilder().add("Authorization", "Bearer "+ sm.getPref("token")).build();
                    request = request.newBuilder().headers(headers).build();
                    return chain.proceed(request);
                }
            };
            client.addInterceptor(headerAuthorizationInterceptor);
        }else{
            client.addInterceptor(new BasicAuthInterceptor("klinik", "klinik2020"));
        }
        client.addInterceptor(interceptor);

        retrofit = new Retrofit.Builder()
                .baseUrl(sm.getPref("base_url"))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client.build())
                .build();
        return retrofit;
    }
}
