package eina.unizar.unozar

import retrofit2.Call
import retrofit2.http.*

interface API {
    @POST("/player/authentication")
    fun userAuthentication(
        @Body login: LoginUser
    ): Call<BasicResponse>

    @POST("/player/createPlayer")
    fun userRegister(
        @Body register: RegisterUser
    ): Call<RegisterResponse>

    @POST("/player/readPlayer/{id}")
    fun userRead(
        @Path("id") id: String
    ): Call<RegisterResponse>

    @POST("/player/updatePlayer/{id}")
    fun userUpdatePlayer(
        @Path("id") id: String,
        @Body updateRequest: UpdateRequest
    ): Call<BasicResponse>

    @HTTP(method = "DELETE", path = "/player/deletePlayer/{id}", hasBody = true)
    fun userDeleteAccount(
        @Path("id") id: String,
        @Body delete: DeleteRequest
    ): Call<BasicResponse>

    @GET("/player/refreshToken")
    fun userRefreshToken(
        @Body refreshRequest: RefreshRequest
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
