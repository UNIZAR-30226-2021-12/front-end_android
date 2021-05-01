package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class PutCardRequest(@SerializedName("code") val code:String,
                          @SerializedName("nameCard") val nameCard:String,
                          @SerializedName("token") val token:String)