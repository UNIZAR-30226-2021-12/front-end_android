package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class CreateMatchRequest (@SerializedName("isPrivate") val isPrivate:Boolean,
                               @SerializedName("maxPlayers") val maxPlayers:Int,
                               @SerializedName("numBots") val numBots:Int,
                               @SerializedName("token") val token:String)
