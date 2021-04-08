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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
    }

    fun changePassword(view: View) {
        val oldPassword = findViewById<EditText>(R.id.old_password).text.toString()
        val newPassword = findViewById<EditText>(R.id.new_password).text.toString()
        val repeatPassword = findViewById<EditText>(R.id.repeat_password).text.toString()

        if (newPassword != repeatPassword) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Alerta!")
            builder.setMessage("Las contraseñas no coinciden")
            builder.setPositiveButton("Ok") { _: DialogInterface, _: Int -> }
            builder.show()
        } else {
            RetrofitClient.instance.userPasswordChange("auth", oldPassword, newPassword)
                .enqueue(object: Callback<PasswordChangeResponse> {
                    override fun onFailure(call: Call<PasswordChangeResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, "1: " + t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<PasswordChangeResponse>, response: Response<PasswordChangeResponse>) {
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
