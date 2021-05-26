package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_password.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.UpdateRequest
import server.response.TokenResponse

class PasswordChange : AppCompatActivity() {
    private lateinit var session: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        session = intent.getStringExtra("session").toString()
    }

    fun changePassword(@Suppress("UNUSED_PARAMETER")view: View) {
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString().trim()
        val repeatPassword = findViewById<EditText>(R.id.repeat_password).text.toString().trim()
        val check = AlertDialog.Builder(this)
        check.setTitle(getString(R.string.alert))
        check.setMessage(getString(R.string.password_change_alert_message))
        check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
            if (validateInput(newPassword, repeatPassword)) {
                RetrofitClient.instance.updatePlayer(UpdateRequest(10, null, null, newPassword, session, 10, 10))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                setResult(Activity.RESULT_OK, intent)
                                Toast.makeText(applicationContext, getString(R.string.password_change_success), Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        }
        check.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        check.show()
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun validateInput (newPassword:String, repeatPassword:String) : Boolean {
        when {
            newPassword.isEmpty() -> {
                new_password.error = getString(R.string.password_format_error)
            }
            newPassword != repeatPassword -> {
                new_password.error = getString(R.string.different_passwords)
                repeat_password.error = getString(R.string.different_passwords)
            }
            else -> return true
        }
        return false
    }

    override fun onBackPressed() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
