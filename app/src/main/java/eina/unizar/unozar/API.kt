package eina.unizar.unozar

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface API {
    @POST("/player/authentication")
    fun userAuthentication(
        @Body login: LoginUser
    ): Call<LoginResponse>

    @POST("/player/createPlayer")
    fun userRegister(
        @Body register: RegisterUser
    ): Call<BasicResponse>

    @POST("/player/updatePlayerPassword")
    fun userPasswordChange(
        @Field("authorization") auth:String,
        @Field("new_password") new_password:String
    ): Call<BasicResponse>

    @POST("/player/updatePlayerEmail")
    fun userEmailChange(
        @Field("authorization") auth:String,
        @Field("new_email") new_email:String
    ): Call<BasicResponse>

    @POST("/player/deletePlayer")
    fun userDeleteAccount(
        @Field("session") session:String
    ): Call<BasicResponse>

    @POST("/game/createGame")
    fun userCreateGame(
        @Field("session") session:String,
        @Field("num_players") num_players:Int,
        @Field("num_bots") num_bots:Int
    ): Call<BasicResponse>

    @POST("/game/createGame")
    fun joinPrivateMatch(
        @Field("session") session:String,
        @Field("code") code:Int
    ): Call<BasicResponse>
}
