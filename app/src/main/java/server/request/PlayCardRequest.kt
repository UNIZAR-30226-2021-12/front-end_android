package server.request

import com.google.gson.annotations.SerializedName

data class PlayCardRequest (@SerializedName("token") val token:String,
                            @SerializedName("cardToPlay") val cardToPlay:Int,
                            @SerializedName("hasSaidUnozar") val hasSaidUnozar:Boolean,
                            @SerializedName("colorSelected") val colorSelected:String)
