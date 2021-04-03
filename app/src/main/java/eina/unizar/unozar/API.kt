package eina.unizar.unozar

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.POST

interface API {
    @POST("userLogin")
    fun userLogin(
        @Field("nombre") nombre:String,
        @Field("password") password:String
    ): Call<LoginResponse>
}