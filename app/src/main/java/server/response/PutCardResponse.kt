package server.response

import com.google.gson.annotations.SerializedName

data class PutCardResponse(@SerializedName("token")val token: String)
