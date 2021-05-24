package server.response

import com.google.gson.annotations.SerializedName

data class RoomInfoResponse(@SerializedName("gameId")val gameId:String,
                            @SerializedName("maxPlayers")val maxPlayers:Int,
                            @SerializedName("playersIds")val playersIds:Array<String>,
                            @SerializedName("gameStarted")val gameStarted:Boolean,
                            @SerializedName("token")val token: String,
                            @SerializedName("bet")val bet: Int)
