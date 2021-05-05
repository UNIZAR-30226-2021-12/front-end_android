package server.request

import com.google.gson.annotations.SerializedName

data class IdRequest (@SerializedName("playerId") val playerId:String)
