package eina.unizar.unozar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton

class changeAvatar : AppCompatActivity() {
    var nameAvatar = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)
        var img = findViewById<View>(R.id.imageButton) as ImageButton
        img.setOnClickListener{
            nameAvatar = "oso"
            cambiarAvatar(nameAvatar)
        }
        var img1 = findViewById<View>(R.id.imageButton2) as ImageButton
        img1.setOnClickListener{
            nameAvatar = "castor"
            cambiarAvatar(nameAvatar)
        }
        var img2 = findViewById<View>(R.id.imageButton3) as ImageButton
        img2.setOnClickListener{
            nameAvatar = "larry"
            cambiarAvatar(nameAvatar)
        }
        var img3 = findViewById<View>(R.id.imageButton4) as ImageButton
        img3.setOnClickListener{
            nameAvatar = "jesica"
            cambiarAvatar(nameAvatar)
        }
    }


    fun cambiarAvatar(avatar: String){
        //Solicitud cambiar avatar
        finish()
    }
}