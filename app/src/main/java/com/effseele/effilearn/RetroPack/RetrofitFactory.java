package com.effseele.effilearn.RetroPack;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitFactory {
    private static Retrofit retrofit=null;
    private RetrofitFactory()
    {}

    public static Retrofit getRetrofit()
    {
        if (retrofit==null)
        {

            OkHttpClient okHttpClient= new OkHttpClient().newBuilder().
                    connectTimeout(60, TimeUnit.SECONDS).readTimeout(60,TimeUnit.SECONDS).
                    writeTimeout(60,TimeUnit.SECONDS).build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(APIUrl.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
}
