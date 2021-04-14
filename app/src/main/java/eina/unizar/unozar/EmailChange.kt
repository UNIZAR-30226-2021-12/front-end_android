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

class EmailChange : AppCompatActivity() {

    var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
        session = intent.getStringExtra("session").toString()
    }

    fun changeMail(view: View) {
        val new_email = findViewById<EditText>(R.id.old_mail).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val repeat_password = findViewById<EditText>(R.id.repeat_password).text.toString()
        val check = AlertDialog.Builder(this)
        check.setTitle("Alerta!")
        check.setMessage("Va a cambiar el correo electrónico asociado a su cuenta, ¿desea continuar?")
        check.setPositiveButton("Sí") { dialogInterface: DialogInterface, i: Int ->
            /*if (password != repeat_password) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Alerta!")
                builder.setMessage("Las contraseñas no coinciden")
                builder.setPositiveButton("Ok") { _: DialogInterface, _: Int -> }
                builder.show()
            } else if (password == saved_password) {

            } else{*/
                RetrofitClient.instance.userEmailChange(session, new_email)
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
        check.setNegativeButton("No") { dialogInterface: DialogInterface, i: Int -> }
        check.show()
    }

    fun cancel(view: View) {
        finish()
    }
}
