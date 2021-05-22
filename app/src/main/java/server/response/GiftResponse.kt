package server.response

import com.google.gson.annotations.SerializedName

data class GiftResponse(@SerializedName("gift")val gift:Int,
                        @SerializedName("token")val token:String)
