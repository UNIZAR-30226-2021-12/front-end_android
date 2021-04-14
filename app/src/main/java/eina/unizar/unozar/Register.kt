package eina.unizar.unozar

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
    }

    fun register(view: View) {
        val alias = register_alias.text.toString().trim()
        val email = register_email.text.toString().trim()
        val password = register_password.text.toString().trim()
        val password_repeat = register_password_repeat.text.toString().trim()

        if(alias.isEmpty()){
            register_alias.error = "Escriba un alias"
            register_alias.requestFocus()
        }
        if(email.isEmpty()){
            register_email.error = "Escriba un email"
            register_email.requestFocus()
        }
        if(password.isEmpty()){
            register_password.error = "Escriba una contraseña"
            register_password.requestFocus()
        }
        if (password != password_repeat) {
            /*val builder = AlertDialog.Builder(this)
            builder.setTitle("Alerta!")
            builder.setMessage("Las contraseñas no coinciden")
            builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int -> }
            builder.show()*/
            register_password.error = "Las contraseñas no coinciden"
            register_password_repeat.error = "Las contraseñas no coinciden"
            register_password.requestFocus()
            register_password_repeat.requestFocus()
        } else {
            RetrofitClient.instance.userRegister(email, alias, password)
                .enqueue(object: Callback<BasicResponse> {
                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "1: " + t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        Toast.makeText(
                            applicationContext,
                            "2: " + response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        //val intent = Intent(this, Principal::class.java)
                        //intent.putExtra("session", response.body()?.message)
                        //startActivity(intent)
                    }
                })
        }
    }

    fun cancel(view: View) {
        finish()
    }
}
