package server.request

import com.google.gson.annotations.SerializedName

data class FriendsListRequest (@SerializedName("token") val token:String)
