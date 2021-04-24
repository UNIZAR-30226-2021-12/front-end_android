package eina.unizar.unozar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun register(@Suppress("UNUSED_PARAMETER")view: View) {
        val alias = register_alias.text.toString().trim()
        val email = register_email.text.toString().trim()
        val password = register_password.text.toString().trim()
        val passwordRepeat = register_password_repeat.text.toString().trim()

        if (validateInput(alias, email, password, passwordRepeat)) {
            RetrofitClient.instance.userRegister(RegisterUser(email, alias, password))
                .enqueue(object : Callback<RegisterResponse> {
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Register, Principal::class.java)
                            intent.putExtra("session", response.body()?.id)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        finish()
    }

    private fun validateInput (alias: String, email:String, password:String, password_repeat:String) : Boolean {
        if(alias.isEmpty()) register_alias.error = "Escriba un alias válido"
        else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            register_email.error = "Escriba un email válido"
            register_email.requestFocus()
        } else if(password.isEmpty()){
            register_password.error = "Escriba una contraseña válida"
            register_password.requestFocus()
        } else if (password != password_repeat) {
            register_password.error = "Las contraseñas no coinciden"
            register_password_repeat.error = "Las contraseñas no coinciden"
            register_password.requestFocus()
            register_password_repeat.requestFocus()
        } else return true
        return false
    }
}
