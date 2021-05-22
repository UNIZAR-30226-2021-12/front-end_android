package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_avatar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.UnlockRequest
import server.response.TokenResponse

class ChangeAvatar : AppCompatActivity() {

    private lateinit var unlocked: Array<String>
    private lateinit var session: String
    override fun onCreate(savedInstanceState: Bundle?) {
        session = intent.getStringExtra("session").toString()
        unlocked = intent.getStringArrayExtra("unlocked") as Array<String>
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)
        registerForContextMenu(avatar_list)
    }
}
