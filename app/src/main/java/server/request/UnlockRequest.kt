package server.request

import com.google.gson.annotations.SerializedName

data class UnlockRequest (@SerializedName("unlockableId") val unlockableId:Int,
                          @SerializedName("token") val token:String)
