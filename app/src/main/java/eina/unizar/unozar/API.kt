package eina.unizar.unozar

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {
    @FormUrlEncoded
    @POST("/player/authentication")
    fun userAuthentication(
        @Field("email") email:String,
        @Field("password") password:String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("player/createPlayer")
    fun userRegister(
        @Field("email") email:String,
        @Field("alias") alias:String,
        @Field("password") password:String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("player/updatePlayerPassword")
    fun userPasswordChange(
        @Field("authorization") auth:String,
        @Field("new_password") new_password:String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("player/updatePlayerEmail")
    fun userEmailChange(
        @Field("authorization") auth:String,        // Comprobar parámetros a enviar
        @Field("new_email") new_email:String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/player/deletePlayer")
    fun userDeleteAccount(
        @Field("session") session:String
    ): Call<BasicResponse>

    @FormUrlEncoded
    @POST("/game/createGame")
    fun userCreateGame(
        @Field("session") session:String,
        @Field("num_players") num_players:Int,
        @Field("num_bots") num_bots:Int
    ): Call<BasicResponse>
}
