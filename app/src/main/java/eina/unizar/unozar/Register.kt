package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

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
        if (password == password_repeat) {
            val intent = Intent(this, Principal::class.java)
            startActivity(intent)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("Las contraseñas no coinciden") // Mensaje recibido del backend si hay error
            builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int -> }
            builder.show()
        }
    }

    fun cancel(view: View) {
        finish()
    }
}