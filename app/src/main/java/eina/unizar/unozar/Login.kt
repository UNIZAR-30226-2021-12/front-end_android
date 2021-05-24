package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import retrofit2.Callback
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import server.response.LoginUser
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Response
import server.response.TokenResponse

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        RetrofitClient.instance.authentication(LoginUser("alberto.lardies@gmail.com", "alberto"))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        val intent = Intent(this@Login, Principal::class.java)
                        intent.putExtra("session", response.body()?.token)
                        startActivity(intent)
                    } else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        //Toast.makeText(applicationContext, getString(R.string.bad_login_response), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    fun login(@Suppress("UNUSED_PARAMETER")view: View) {
        val email = editTextTextPersonName2.text.toString().trim()
        val password = editTextTextPassword.text.toString().trim()

        if(validateInput(email, password)) {
            RetrofitClient.instance.authentication(LoginUser(email, password))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Login, Principal::class.java)
                            intent.putExtra("session", response.body()?.token)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            //Toast.makeText(applicationContext, getString(R.string.bad_login_response), Toast.LENGTH_LONG).show()
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
            editTextTextPersonName2.error = getString(R.string.email_format_error)
        } else if (password.isEmpty()) {
            editTextTextPassword.error = getString(R.string.password_format_error)
        } else return true
        return false
    }
}
