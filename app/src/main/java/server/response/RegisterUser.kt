package server.response

import com.google.gson.annotations.SerializedName

data class RegisterUser (@SerializedName("email") val email:String,
                         @SerializedName("alias") val alias: String,
                         @SerializedName("password") val password: String)
