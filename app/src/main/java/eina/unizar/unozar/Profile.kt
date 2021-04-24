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

    private lateinit var session: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        session = intent.getStringExtra("session").toString()
        // getStatistics
    }

    @SuppressLint("SetTextI18n")
    fun goToChangeEmail(@Suppress("UNUSED_PARAMETER")view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
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
            RetrofitClient.instance.userDeleteAccount(session.substring(0,32), DeleteRequest(session))
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Profile, Register::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, "Error! No se pudo eliminar su cuenta" + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        check.setNegativeButton("No") { _: DialogInterface, _: Int -> }
        check.show()
    }
}
