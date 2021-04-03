package eina.unizar.eina.unozargame

import com.google.gson.annotations.SerializedName

data class LoginResponse(@SerializedName("error")val error: Boolean,
                         @SerializedName("error_message")val message:String,
                         @SerializedName("user")val user: loginUser)