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

    @POST("userRegister")
    fun userRegister(
        @Field("alias") alias:String,
        @Field("correo") correo:String,
        @Field("password") password:String,
        @Field("repeat_password") repeat_password:String
    ): Call<RegisterResponse>
}