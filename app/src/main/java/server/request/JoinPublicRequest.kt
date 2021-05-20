package server.request

import com.google.gson.annotations.SerializedName

data class JoinPublicRequest (@SerializedName("numPlayers") val numPlayers:Int,
                              @SerializedName("token") val token:String)
