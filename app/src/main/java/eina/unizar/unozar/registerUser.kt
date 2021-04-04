package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class RegisterUser (@SerializedName("user_alias") val userAlias:String,
                         @SerializedName("user_name") val userEmail:String,
                         @SerializedName("user_email") val userPassword:String,
                         @SerializedName("user_age") val userRepeatPassword: String)