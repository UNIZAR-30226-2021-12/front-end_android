package server.request

import com.google.gson.annotations.SerializedName

data class JoinPrivateRequest (@SerializedName("code") val code:String,
                               @SerializedName("token") val token:String)
