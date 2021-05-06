package server.response

import com.google.gson.annotations.SerializedName

data class FriendsListResponse(@SerializedName("token")val token:String,
                               @SerializedName("friendIds")val friendIds:Array<String>?,
                               @SerializedName("alias")val alias:Array<String>?,
                               @SerializedName("emails")val emails:Array<String>?,
                               @SerializedName("avatarIds")val avatarIds:Array<Int>?)
