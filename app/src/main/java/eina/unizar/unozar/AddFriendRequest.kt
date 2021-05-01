package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class AddFriendRequest (@SerializedName("email") val email:String,
                             @SerializedName("token") val token:String)
