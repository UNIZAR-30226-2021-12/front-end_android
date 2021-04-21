package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import retrofit2.Callback
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response
//import storage.SharedPrefManager

class Login : AppCompatActivity() {

    private val tested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(@Suppress("UNUSED_PARAMETER")view: View) {
        val email = editTextTextPersonName2.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()

        if(validateInput(email, password)) {
            if (tested) {
                RetrofitClient.instance.userAuthentication(LoginUser(email, password))
                    .enqueue(object : Callback<LoginResponse> {
                        override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<LoginResponse>,
                            response: Response<LoginResponse>
                        ) {
                            if (response.code() == 200) {
                                Toast.makeText(
                                    applicationContext,
                                    "ok: " + response.body()?.token,
                                    Toast.LENGTH_LONG
                                ).show()
                                finish()
                                val intent = Intent(this@Login, Principal::class.java)
                                intent.putExtra("session", response.body()?.token)
                                startActivity(intent)
                            } else {
                                val check = AlertDialog.Builder(this@Login)
                                check.setTitle("Error " + response.code())
                                //check.setMessage("No se ha podido iniciar sesión")
                                check.setMessage(response.message())
                                check.setPositiveButton("Volver") { _: DialogInterface, _: Int -> }
                                check.show()
                            }
                        }

                    })
            } else {
                val test = TestCalls("test")
                val intent = Intent(this, Principal::class.java)
                intent.putExtra("session", test.userAuthenticationTest())
                startActivity(intent)
            }
        }
    }

    fun goToRegister(@Suppress("UNUSED_PARAMETER")view: View) {
        val i = Intent(this, Register::class.java)
        startActivity(i)
    }

    private fun validateInput (email:String, password:String) : Boolean {
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextTextPersonName2.error = "Escriba un email válido"
        } else if(password.isEmpty()){
            editTextTextPassword.error = "Escriba una contraseña válida"
        } else {
            return true
        }
        return false
    }
}
