package pl.mg6.safetynet

import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

internal object RetrofitProvider : Provider<Retrofit>({
    Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8080/")
            .client(OkHttpClient.Builder()
                    .apply { addInterceptor(HttpLoggingInterceptor().setLevel(BODY)) }
                    .build())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
})
