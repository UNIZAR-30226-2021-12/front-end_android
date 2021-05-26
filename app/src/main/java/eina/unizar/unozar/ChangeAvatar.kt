package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_avatar.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.UpdateRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class ChangeAvatar : AppCompatActivity() {

    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )
    private  var owned = 0
    private lateinit var session: String
    override fun onCreate(savedInstanceState: Bundle?) {
        session = intent.getStringExtra("session").toString()
        owned = intent.getIntExtra("owned", 0)
        super.onCreate(savedInstanceState)
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        if (response.body()!!.unlockedAvatars.size > 1) {
                            avatar_two.visibility = VISIBLE
                            avatar_two.setImageResource(avatars[(response.body()!!.unlockedAvatars[1])])
                            avatar_two.setOnClickListener { changeAvatar(response.body()!!.unlockedAvatars[1]) }
                        }
                        if (response.body()!!.unlockedAvatars.size > 2) {
                            avatar_three.visibility = VISIBLE
                            avatar_three.setImageResource(avatars[(response.body()!!.unlockedAvatars[2])])
                            avatar_three.setOnClickListener { changeAvatar(response.body()!!.unlockedAvatars[2]) }
                        }
                        if (response.body()!!.unlockedAvatars.size > 3) {
                            avatar_four.visibility = VISIBLE
                            avatar_four.setImageResource(avatars[(response.body()!!.unlockedAvatars[3])])
                            avatar_four.setOnClickListener { changeAvatar(response.body()!!.unlockedAvatars[3]) }
                        }
                    }
                }
            })
        setContentView(R.layout.activity_change_avatar)

        go_back.setOnClickListener {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        avatar_one.setImageResource(avatars[0])
        avatar_one.setOnClickListener { changeAvatar(0) }

    }

    private fun changeAvatar(i: Int) {
        if (i == owned) Toast.makeText(applicationContext, getString(R.string.avatar_in_use), Toast.LENGTH_LONG).show()
        else {
            val check = AlertDialog.Builder(this)
            check.setTitle(getString(R.string.avatar_change_alert_message))
            check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.updatePlayer(UpdateRequest(i, null, null, null, session, 10, 10))
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
                                Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            check.setNegativeButton(getString(R.string.alert_negative_button)) { _: DialogInterface, _: Int -> }
            check.show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
