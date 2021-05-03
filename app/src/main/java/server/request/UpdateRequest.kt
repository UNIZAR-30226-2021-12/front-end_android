package server.request

import com.google.gson.annotations.SerializedName

data class UpdateRequest (@SerializedName("email") val email:String?,
                          @SerializedName("alias") val alias: String?,
                          @SerializedName("password") val password: String?,
                          @SerializedName("token") val token: String)
