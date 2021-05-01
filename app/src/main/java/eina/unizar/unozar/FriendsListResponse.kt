package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class FriendsListResponse(@SerializedName("friends")val friends:Array<String>)