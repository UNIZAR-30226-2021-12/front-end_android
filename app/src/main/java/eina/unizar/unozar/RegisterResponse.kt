package eina.unizar.unozar

import com.google.gson.annotations.SerializedName

data class RegisterResponse(//@SerializedName("error")val error: Boolean,
                            @SerializedName("error_message")val message:String)
                            //@SerializedName("user")val user: loginUser)
