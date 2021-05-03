package server.request

import com.google.gson.annotations.SerializedName

data class TokenRequest (@SerializedName("token") val token:String)
