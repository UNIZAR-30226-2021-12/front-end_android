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
        check.setTitle("Alerta!")
        check.setMessage("Va a cambiar el correo electrónico asociado a su cuenta, ¿desea continuar?")
        check.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
            if (validateInput(newEmail)) {
                RetrofitClient.instance.userUpdatePlayer(session.substring(0,32), UpdateRequest(newEmail, null, null, session))
                    .enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.code() == 200) {
                                Toast.makeText(applicationContext, "Ha actualizado su dirección de correo electrónico con éxito", Toast.LENGTH_LONG).show()
                                finish()
                            } else {
                                Toast.makeText(applicationContext, "Error! No se pudo realizar el cambio" + response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        }
        check.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        check.show()
    }

    fun cancel(@Suppress("UNUSED_PARAMETER")view: View) {
        finish()
    }

    private fun validateInput (newEmail:String) : Boolean {
        if(newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
            new_email.error = "Escriba un email válido"
        } else return true
        return false
    }
}
