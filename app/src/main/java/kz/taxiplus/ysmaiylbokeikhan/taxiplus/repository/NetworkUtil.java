package kz.taxiplus.ysmaiylbokeikhan.taxiplus.repository;

import kz.taxiplus.ysmaiylbokeikhan.taxiplus.utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class NetworkUtil {
    public static RetrofitInterface getRetrofit(){

        RxJavaCallAdapterFactory rxAdapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
//
//        return new Retrofit.Builder()
//                .baseUrl(UtilConstants.BASE_URL)
//                .addCallAdapterFactory(rxAdapter)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build().create(RetrofitInterface.class);
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        clientBuilder.addInterceptor(loggingInterceptor);

        return new Retrofit.Builder()
                .baseUrl(Constants.TEST_URL)
                .client(clientBuilder.build())
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitInterface.class);

    }
}
