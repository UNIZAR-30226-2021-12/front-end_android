package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class RegisterResponse(@SerializedName("id")val id:String,
                         @SerializedName("email")val email:String,
                         @SerializedName("alias")val alias:String)