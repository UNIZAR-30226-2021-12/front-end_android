package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun register(view: View) {
        val alias = findViewById<EditText>(R.id.register_alias).text.toString()
        val email = findViewById<EditText>(R.id.register_email).text.toString()
        val password = findViewById<EditText>(R.id.register_password).text.toString()
        val password_repeat = findViewById<EditText>(R.id.register_password_repeat).text.toString()
        // Enviar al backend
        if (password != password_repeat) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alerta!")
            builder.setMessage("Las contraseñas no coinciden")
            builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int -> }
            builder.show()
        } else {
            RetrofitClient.instance.userRegister(email, alias, password)
                .enqueue(object: Callback<RegisterResponse> {
                    override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "1: " + t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                        Toast.makeText(
                            applicationContext,
                            "2: " + response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()


                    }

                })
        }
    }

    fun cancel(view: View) {
        finish()
    }
}