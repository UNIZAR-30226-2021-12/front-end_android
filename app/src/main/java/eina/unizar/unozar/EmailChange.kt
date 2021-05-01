 package eina.unizar.unozar

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_email.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EmailChange : AppCompatActivity() {

    private lateinit var session: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        session = intent.getStringExtra("session").toString()
    }

    fun changeEmail(@Suppress("UNUSED_PARAMETER")view: View) {
        val newEmail = new_email.text.toString().trim()
        val check = AlertDialog.Builder(this)
        check.setTitle(getString(R.string.alert))
        check.setMessage(getString(R.string.email_update_alert_message))
        check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
            if (validateInput(newEmail)) {
                RetrofitClient.instance.userUpdatePlayer(session.substring(0,32), UpdateRequest(newEmail, null, null, session))
                    .enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.code() == 200) {
                                Toast.makeText(applicationContext, getString(R.string.email_change_success), Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        }
        check.setNegativeButton(getString(R.string.alert_negative_button)) { _: DialogInterface, _: Int -> }
        check.show()
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        finish()
    }

    private fun validateInput (newEmail:String) : Boolean {
        if(newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            new_email.error = getString(R.string.email_format_error)
        } else return true
        return false
    }
}
