package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import retrofit2.Callback
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response
import storage.SharedPrefManager

class Login : AppCompatActivity() {
    private val ACTIVITY_LOGIN = 1
    private val ACTIVITY_REGISTER = 2
    private val ACTIVITY_PERFIL = 3

    /*override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<View>(R.id.login) as Button
        val toPerfilButton = findViewById<View>(R.id.registrase) as Button
        toPerfilButton.setOnClickListener {
            goToRegister()
        }
        loginButton.setOnClickListener {
            val name = editTextTextPersonName2.text.toString().trim()
            val password = editTextTextPassword.text.toString().trim()

            if(name.isEmpty()){
                editTextTextPersonName2.error = "Email required"
                editTextTextPersonName2.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                editTextTextPassword.error = "Password required"
                editTextTextPassword.requestFocus()
                return@setOnClickListener
            }
            RetrofitClient.instance.userLogin(name,password)
                .enqueue(object: Callback<LoginResponse>{
                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if(!response.body()?.error!!){
                            SharedPrefManager.
                            getInstance(applicationContext).
                            saveUser(response.body()?.user!!)

                        }else {
                            Toast.makeText(
                                applicationContext,
                                response.body()?.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                })
        }
    }

    override fun onStart(){
        super.onStart()
        if(SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(applicationContext,Principal::class.java/*Actividad*/)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun login(view: View) {
        val email = editTextTextPersonName2.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()

        if(validateInput(email, password)) {
            if (!true) {
                RetrofitClient.instance.userAuthentication(email, password)
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        }

                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
                                val intent = Intent(this@Login, Principal::class.java)
                                intent.putExtra("session", response.body()?.message)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "error: " + response.body()?.message,
                                    Toast.LENGTH_LONG
                                ).show()
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

    fun goToRegister(view: View) {
        val i = Intent(this, Register::class.java)
        startActivityForResult(i, ACTIVITY_REGISTER)
    }

    private fun validateInput (email:String, password:String) : Boolean {
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            register_email.error = "Escriba un email válido"
            register_email.requestFocus()
        } else if(password.isEmpty()){
            register_password.error = "Escriba una contraseña válida"
            register_password.requestFocus()
        } else {
            return true
        }
        return false
    }
}
