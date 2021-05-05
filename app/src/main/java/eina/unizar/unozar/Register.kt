package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import server.response.PlayerInfo
import server.response.RegisterUser
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.CreateMatchRequest
import server.response.LoginUser
import server.response.TokenResponse

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
            RetrofitClient.instance.register(RegisterUser(email, alias, password))
                .enqueue(object : Callback<PlayerInfo> {
                    override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                        if (response.code() == 200) {
                            RetrofitClient.instance.authentication(LoginUser(email, password))
                                .enqueue(object : Callback<TokenResponse> {
                                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                        if (response.code() == 200) {
                                            val intent = Intent(this@Register, Principal::class.java)
                                            intent.putExtra("session", response.body()?.token)
                                            startActivity(intent)
                                        } else {
                                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                                            //Toast.makeText(applicationContext, getString(R.string.bad_login_response), Toast.LENGTH_LONG).show()
                                        }
                                    }
                                })
                            /*startActivity(intent)
                            val intent = Intent(this@Register, Principal::class.java)
                            intent.putExtra("session", response.body()?.id)
                            val bots = AlertDialog.Builder(this@Register)
                            var numBots:Array<String>
                            numBots = arrayOf(response.body()?.id.toString(),
                                response.body()?.email.toString(),
                                response.body()?.alias.toString(),
                                response.body()?.gameId.toString(),
                                response.body()?.privateWins.toString(),
                                response.body()?.privateTotal.toString(),
                                response.body()?.publicWins.toString(),
                                response.body()?.publicTotal.toString())
                            bots.setTitle(getString(R.string.number_of_bots))
                            bots.setItems(numBots) { _: DialogInterface, j: Int ->
                                startActivity(intent)
                            }
                            bots.show()*/
                        } else {
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            //Toast.makeText(applicationContext, getString(R.string.bad_register_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        finish()
    }

    private fun validateInput (alias: String, email:String, password:String, password_repeat:String) : Boolean {
        if(alias.isEmpty()) register_alias.error = getString(R.string.alias_format_error)
        else if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            register_email.error = getString(R.string.email_format_error)
            register_email.requestFocus()
        } else if(password.isEmpty()){
            register_password.error = getString(R.string.password_format_error)
            register_password.requestFocus()
        } else if (password != password_repeat) {
            register_password.error = getString(R.string.different_passwords)
            register_password_repeat.error = getString(R.string.different_passwords)
            register_password.requestFocus()
            register_password_repeat.requestFocus()
        } else return true
        return false
    }
}
