package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import retrofit2.Callback
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Response
import storage.SharedPrefManager

class Login : AppCompatActivity() {
    private val ACTIVITY_LOGIN = 1
    private val ACTIVITY_REGISTER = 2
    private val ACTIVITY_PERFIL = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<View>(R.id.login) as Button
        val toPerfilButton = findViewById<View>(R.id.registrase) as Button
        toPerfilButton.setOnClickListener {
            goToPerfil();
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
            val intent = Intent(applicationContext,Perfil::class.java/*Actividad*/)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    protected fun goToPerfil() {
        val i = Intent(this, eina.unizar.eina.unozargame.Perfil::class.java)
        startActivityForResult(i, ACTIVITY_PERFIL)
    }
}