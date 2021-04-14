package eina.unizar.unozar

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PasswordChange : AppCompatActivity() {

    var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        session = intent.getStringExtra("session").toString()
    }

    fun changePassword(view: View) {
        val oldPassword = findViewById<EditText>(R.id.old_password).text.toString()
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString()
        val repeatPassword = findViewById<EditText>(R.id.repeat_password).text.toString()
        val check = AlertDialog.Builder(this)
        check.setTitle("Alerta!")
        check.setMessage("Va a cambiar la contraseña de su cuenta, ¿desea continuar?")
        check.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            /*if (newPassword != repeatPassword) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Alerta!")
                builder.setMessage("Las contraseñas no coinciden")
                builder.setPositiveButton("Ok") { _: DialogInterface, _: Int -> }
                builder.show()
            } else {*/
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
            //}
        }
        check.setNegativeButton("gh") { dialogInterface: DialogInterface, i: Int -> }
        check.show()
    }

    fun cancel(view: View) {
        finish()
    }
}
