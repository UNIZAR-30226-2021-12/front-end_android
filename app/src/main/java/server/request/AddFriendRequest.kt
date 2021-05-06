package server.request

import com.google.gson.annotations.SerializedName

data class AddFriendRequest (@SerializedName("token") val token:String,
                             @SerializedName("friendId") val friendId:String)
