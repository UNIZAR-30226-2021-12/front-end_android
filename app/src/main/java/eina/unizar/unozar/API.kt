package eina.unizar.unozar

import data.LoginUser
import data.PlayerInfo
import data.RegisterUser
import retrofit2.Call
import retrofit2.http.*
import server.request.*
import server.response.FriendsListResponse
import server.response.GameInfoResponse
import server.response.PutCardResponse
import server.response.TokenResponse

interface API {
    @POST("/player/authentication")
    fun authentication(
        @Body login: LoginUser
    ): Call<TokenResponse>

    @POST("/player/create")
    fun register(
        @Body register: RegisterUser
    ): Call<PlayerInfo>

    @GET("/player/read/{id}")
    fun readPlayer(
        @Path("id") id: String
    ): Call<PlayerInfo>

    @PATCH("/player/update/{id}")
    fun updatePlayer(
        @Path("id") id: String,
        @Body updateRequest: UpdateRequest
    ): Call<Void>

    @HTTP(method = "DELETE", path = "/player/delete/{id}", hasBody = true)
    fun deleteAccount(
        @Path("id") id: String,
        @Body delete: TokenRequest
    ): Call<Void>

    @POST("/player/refreshToken")
    fun refreshToken(
        @Body refreshRequest: TokenRequest
    ): Call<TokenResponse>

    @POST("/game/create")
    fun createMatch(
        @Body createMatchRequest: CreateMatchRequest
    ): Call<Void>

    @POST("/game/join")
    fun joinPrivate(
        @Body joinPrivateRequest: JoinPrivateRequest
    ): Call<TokenResponse>

    @POST("/game/quit")
    fun quitMatch(
        @Body delete: TokenRequest
    ): Call<Void>

    @POST("/game/read")
    fun readGame(
        @Body delete: TokenRequest
    ): Call<GameInfoResponse>

    @POST("/player/addFriend")
    fun addFriend(
        @Body addFriendRequest: AddFriendRequest
    ): Call<TokenResponse>

    @POST("/player/addFriend")
    fun getFriends(
        @Body friendsListRequest: FriendsListRequest
    ): Call<FriendsListResponse>

    @POST("/game/createGame")
    fun startMatch(
        @Field("session") session:String
    ): Call<TokenResponse>

    @POST("/game/playCard")
    fun userPlayCard(
        @Field("session") session:String
    ): Call<PutCardResponse>

    @POST("/game/drawCards")
    fun userDrawCards(
        @Field("session") session:String
    ): Call<PutCardResponse>
}
