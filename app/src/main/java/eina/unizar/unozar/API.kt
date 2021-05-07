package eina.unizar.unozar

import android.os.TokenWatcher
import server.response.LoginUser
import server.response.PlayerInfo
import server.response.RegisterUser
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

    @POST("/player/read")
    fun readPlayer(
        @Body idRequest: IdRequest
    ): Call<PlayerInfo>

    @PATCH("/player/update")
    fun updatePlayer(
        @Body updateRequest: UpdateRequest
    ): Call<TokenResponse>

    @HTTP(method = "DELETE", path = "/player/delete", hasBody = true)
    fun deleteAccount(
        @Body delete: TokenRequest
    ): Call<Void>

    @POST("/player/refreshToken")
    fun refreshToken(
        @Body refreshRequest: TokenRequest
    ): Call<TokenResponse>

    @POST("/game/create")
    fun createMatch(
        @Body createMatchRequest: CreateMatchRequest
    ): Call<TokenResponse>

    @POST("/game/join")
    fun joinPrivate(
        @Body joinPrivateRequest: JoinPrivateRequest
    ): Call<TokenResponse>

    @POST("/game/quit")
    fun quitMatch(
        @Body delete: TokenRequest
    ): Call<TokenResponse>

    @POST("/game/read")
    fun readGame(
        @Body delete: TokenRequest
    ): Call<GameInfoResponse>

    @POST("/player/addFriend")
    fun addFriend(
        @Body addFriendRequest: AddFriendRequest
    ): Call<TokenResponse>

    @POST("/player/deleteFriend")
    fun deleteFriend(
        @Body addFriendRequest: AddFriendRequest
    ): Call<TokenResponse>

    @POST("/player/readFriends")
    fun getFriends(
        @Body tokenRequest: TokenRequest
    ): Call<FriendsListResponse>

    @POST("/game/createGame")
    fun startMatch(
        @Body tokenRequest: TokenRequest
    ): Call<TokenResponse>

    @POST("/game/playCard")
    fun userPlayCard(
        @Body playCardRequest: PlayCardRequest
    ): Call<TokenResponse>

    @POST("/game/drawCards")
    fun userDrawCards(
        @Field("session") session:String
    ): Call<PutCardResponse>
}
