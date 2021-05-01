package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class CreatePrivateRequest (@SerializedName("token") val token:String,
                                 @SerializedName("numPlayers") val numPlayers:Int,
                                 @SerializedName("numBots") val numBots:Int)
