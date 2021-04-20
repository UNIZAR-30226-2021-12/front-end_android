package eina.unizar.unozar

import eina.unizar.unozar.TestCalls
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

    fun register(view: View) {
        val alias = register_alias.text.toString().trim()
        val email = register_email.text.toString().trim()
        val password = register_password.text.toString().trim()
        val passwordRepeat = register_password_repeat.text.toString().trim()

        if (validateInput(alias, email, password, passwordRepeat)) {
            if (!true) {
                RetrofitClient.instance.userRegister(email, alias, password)
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "1: " + t.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            Toast.makeText(
                                applicationContext,
                                "2: " + response.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                            val intent = Intent(this@Register, Principal::class.java)
                            intent.putExtra("session", response.body()?.message)
                            startActivity(intent)
                        }
                    })
            } else {
                val test = TestCalls("test")
                val intent = Intent(this, Principal::class.java)
                intent.putExtra("session", test.userRegisterTest())
                startActivity(intent)
            }
        }
    }

    fun cancel(view: View) {
        finish()
    }

    private fun validateInput (alias: String, email:String, password:String,
                               password_repeat:String) : Boolean {
        if(alias.isEmpty()){
            register_alias.error = "Escriba un alias válido"
        } else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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
        } else {
            return true
        }
        return false
    }
}
