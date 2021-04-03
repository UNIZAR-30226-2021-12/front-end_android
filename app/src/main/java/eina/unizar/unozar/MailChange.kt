package eina.unizar.unozar

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AlertDialog

class MailChange : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_email)
    }

    fun changeMail(view: View) {
        val old_mail = findViewById<EditText>(R.id.old_mail).text.toString()
        val password = findViewById<EditText>(R.id.password).text.toString()
        val repeat_password = findViewById<EditText>(R.id.repeat_password).text.toString()

        // enviar datos al backend y esperar respuesta

        if (true) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Éxito")
            builder.setMessage("La dirección de correo electrónico ha sido cambiada correctamente")
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