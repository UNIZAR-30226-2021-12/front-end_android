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

    @PATCH("/player/updatePlayer/{id}")
    fun userUpdatePlayer(
        @Path("id") id: String,
        @Body updateRequest: UpdateRequest
    ): Call<Void>

    @HTTP(method = "DELETE", path = "/player/deletePlayer/{id}", hasBody = true)
    fun userDeleteAccount(
        @Path("id") id: String,
        @Body delete: DeleteRequest
    ): Call<Void>

    @POST("/player/refreshToken")
    fun userRefreshToken(
        @Body refreshRequest: RefreshRequest
    ): Call<BasicResponse>

    @POST("/game/createGame")
    fun userCreateMatch(
        @Field("session") session:String,
        @Body createPrivateRequest: CreatePrivateRequest
    ): Call<BasicResponse>

    @POST("/game/join")
    fun userJoinPrivateMatch(
        @Field("session") session:String,
        @Body joinPrivateRequest: JoinPrivateRequest
    ): Call<BasicResponse>

    @POST("/player/addFriend")
    fun userAddFriend(
        @Field("session") session:String,
        @Body addFriendRequest: AddFriendRequest
    ): Call<BasicResponse>

    @POST("/player/addFriend")
    fun userGetFriendsList(
        @Field("session") session:String,
        @Body friendsListRequest: FriendsListRequest
    ): Call<FriendsListResponse>

    @POST("/game/createGame")
    fun userStartMatch(
        @Field("session") session:String
    ): Call<BasicResponse>
}
