package server.response

import com.google.gson.annotations.SerializedName

data class GameInfoResponse(@SerializedName("gameId")val gameId:String,
                            @SerializedName("maxPlayers")val maxPlayers:Int,
                            @SerializedName("topDiscard")val topDiscard:String,
                            @SerializedName("playerCards")val playerCards:Array<String>,
                            @SerializedName("turn")val turn:Int,
                            @SerializedName("playersIds")val playersIds:Array<String>,
                            @SerializedName("playersNumCards")val playersNumCards:Array<Int>,
                            @SerializedName("gameStarted")val gameStarted:Boolean,
                            @SerializedName("gamePaused")val gamePaused:Boolean,
                            @SerializedName("token")val token: String)
