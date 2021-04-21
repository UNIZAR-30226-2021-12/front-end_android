package eina.unizar.unozar

import android.content.DialogInterface
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

class PasswordChange : AppCompatActivity() {

    private val tested = false
    private var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        session = intent.getStringExtra("session").toString()
    }

    fun changePassword(@Suppress("UNUSED_PARAMETER")view: View) {
        //val oldPassword = findViewById<EditText>(R.id.old_password).text.toString().trim()
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString().trim()
        val repeatPassword = findViewById<EditText>(R.id.repeat_password).text.toString().trim()
        val check = AlertDialog.Builder(this)
        check.setTitle("Alerta!")
        check.setMessage("Va a cambiar la contraseña de su cuenta, ¿desea continuar?")
        check.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
            if (validateInput(newPassword, repeatPassword)) {
                if (tested) {
                    RetrofitClient.instance.userPasswordChange(session, newPassword)
                        .enqueue(object : Callback<BasicResponse> {
                            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, "1: " + t.message, Toast.LENGTH_LONG)
                                    .show()
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
                                finish()
                            }
                        })
                } else {
                    finish()
                }
            }
        }
        check.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        check.show()
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        finish()
    }

    private fun validateInput (newPassword:String, repeatPassword:String) : Boolean {
        when {
            newPassword.isEmpty() -> {
                new_password.error = "Escriba una contraseña válida"
            }
            newPassword != repeatPassword -> {
                new_password.error = "Las contraseñas no coinciden"
                repeat_password.error = "Las contraseñas no coinciden"
            }
            else -> {
                return true
            }
        }
        return false
    }
}
