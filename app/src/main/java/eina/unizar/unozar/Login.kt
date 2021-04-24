package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import retrofit2.Callback
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(@Suppress("UNUSED_PARAMETER")view: View) {
        val email = editTextTextPersonName2.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()

        if(validateInput(email, password)) {
            RetrofitClient.instance.userAuthentication(LoginUser(email, password))
                .enqueue(object : Callback<BasicResponse> {
                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Login, Principal::class.java)
                            intent.putExtra("session", response.body()?.token)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Error! Revise los datos introducidos", Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    fun goToRegister(@Suppress("UNUSED_PARAMETER")view: View) {
        val i = Intent(this, Register::class.java)
        startActivity(i)
    }

    private fun validateInput (email:String, password:String) : Boolean {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextTextPersonName2.error = "Escriba un email válido"
        } else if (password.isEmpty()) {
            editTextTextPassword.error = "Escriba una contraseña válida"
        } else return true
        return false
    }
}
