package eina.unizar.unozar

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {
    @FormUrlEncoded
    @POST("userLogin")
    fun userLogin(
        @Field("nombre") nombre:String,
        @Field("password") password:String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("player/createPlayer")
    fun userRegister(
        @Field("email") email:String,
        @Field("alias") alias:String,
        @Field("password") password:String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("player/changePassword")  // AÑADIR NOMBRE CORRECTO
    fun userPasswordChange(
        @Field("authorization") auth:String,        // Comprobar parámetros a enviar
        @Field("old_password") old_password:String,
        @Field("new_password") new_password:String
    ): Call<PasswordChangeResponse>
}
