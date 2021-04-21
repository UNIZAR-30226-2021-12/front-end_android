package eina.unizar.unozar

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_perfil.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {

    private val tested = false
    private var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        session = intent.getStringExtra("password").toString()
        // getStatistics
    }

    @SuppressLint("SetTextI18n")
    fun goToChangeEmail(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
        showEmail2.text = "tomate@gmail.com"
    }

    fun goToChangePassword(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent(this, PasswordChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    fun deleteAccount(@Suppress("UNUSED_PARAMETER")view: View) {
        val check = AlertDialog.Builder(this)
        check.setTitle("Alerta!")
        check.setMessage("Va a eliminar su cuenta de forma permanente, ¿desea continuar?")
        check.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
            if (tested) {
                RetrofitClient.instance.userDeleteAccount(session)
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
                            val intent = Intent(this@Profile, Register::class.java)
                            startActivity(intent)
                        }
                    })
            } else {
                val intent = Intent(this, Register::class.java)
                startActivity(intent)
            }
        }
        check.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        check.show()
    }
}
