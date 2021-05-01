package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class JoinPrivateRequest (@SerializedName("token") val token:String,
                               @SerializedName("code") val code:String)
