package server.request

import com.google.gson.annotations.SerializedName

data class JoinPrivateRequest (@SerializedName("gameId") val gameId:String,
                               @SerializedName("token") val token:String)
