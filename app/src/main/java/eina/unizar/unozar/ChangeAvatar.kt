package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_change_avatar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.UnlockRequest
import server.response.TokenResponse

class ChangeAvatar : AppCompatActivity() {

    private lateinit var session: String
    private var avatarId = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)
        /*imageButton.setOnClickListener{
            nameAvatar = "oso"
            cambiarAvatar(nameAvatar)
        }
        imageButton2.setOnClickListener{
            nameAvatar = "castor"
            cambiarAvatar(nameAvatar)
        }
        imageButton3.setOnClickListener{
            nameAvatar = "larry"
            cambiarAvatar(nameAvatar)
        }
        imageButton4.setOnClickListener{
            nameAvatar = "jesica"
            cambiarAvatar(nameAvatar)
        }*/
        RetrofitClient.instance.unlockAvatar(UnlockRequest(avatarId, session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                        setResult(Activity.RESULT_OK, intent)
                        Toast.makeText(applicationContext, getString(R.string.avatar_change_success), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}