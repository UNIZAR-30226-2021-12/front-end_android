package eina.unizar.unozar

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
//import android.util.Base64

object RetrofitClient {
    //private val AUTH = "Basic "+ Base64.encodeToString("belalkhan:123456".toByteArray(), Base64.NO_WRAP)

    private const val BASE_URL = "https://unozar.herokuapp.com/" //URL donde hacemos las peticiones

    private val okHttpClient = OkHttpClient.Builder().addInterceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()
            .addHeader("Authorization","")
            .method(original.method(),original.body())
        val request = requestBuilder.build()
        chain.proceed(request)
    }.build()

    val instance: API by lazy{
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
        retrofit.create(API::class.java)
    }
}
