package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class PasswordChange : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password)
    }

    fun changePassword(view: View) {
        val old_password = findViewById<EditText>(R.id.old_password).text.toString()
        val new_password = findViewById<EditText>(R.id.new_password).text.toString()
        val repeat_password = findViewById<EditText>(R.id.repeat_password).text.toString()

        // enviar datos al backend y esperar respuesta

        if (new_password=="hola") {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Éxito")
            builder.setMessage("La contraseña ha sido cambiada correctamente")
            builder.setPositiveButton("Ok") { dialog, which ->
                finish()
            }
            builder.show()
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Error")
            builder.setMessage("No se ha efectuado el cambio")
            builder.setPositiveButton("Ok") { dialogInterface: DialogInterface, i: Int -> }
            builder.show()
        }
    }

    fun cancel(view: View) {
        finish()
    }
}