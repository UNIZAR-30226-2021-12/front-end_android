package server.response

import com.google.gson.annotations.SerializedName

data class GameInfoResponse(@SerializedName("maxPlayers")val maxPlayers:Int,
                            @SerializedName("topDiscard")val topDiscard:String,
                            @SerializedName("playerCards")val playerCards:String,
                            @SerializedName("turn")val turn:Int,
                            @SerializedName("playersIds")val playersIds:Array<String>,
                            @SerializedName("playersNumCards")val playersNumCards:Array<Int>,
                            @SerializedName("gameStarted")val gameStarted:Boolean,
                            @SerializedName("gamePaused")val gamePaused:Boolean,
                            @SerializedName("gameFinished")val gameFinished:Boolean)
