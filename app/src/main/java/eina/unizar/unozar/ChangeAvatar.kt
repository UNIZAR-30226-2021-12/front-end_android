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
import server.request.UpdateRequest
import server.response.TokenResponse

class ChangeAvatar : AppCompatActivity() {

    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.castor,
        R.drawable.flippy,
        R.drawable.jesica,
        R.drawable.larry,
        R.drawable.oso,
        R.drawable.slendid
    )
    private lateinit var unlocked: Array<String>
    private  var owned = 0
    private lateinit var session: String
    override fun onCreate(savedInstanceState: Bundle?) {
        session = intent.getStringExtra("session").toString()
        unlocked = intent.getStringArrayExtra("unlocked") as Array<String>
        owned = intent.getIntExtra("owned", 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_avatar)
        avatar_one.setOnClickListener { changeAvatar(0) }
        if (unlocked.size > 1) {
            avatar_two.visibility = VISIBLE
            avatar_two.setImageResource(avatars[(unlocked[1]).toInt()])
            avatar_two.setOnClickListener { changeAvatar(1) }
        }
        if (unlocked.size > 2) {
            avatar_three.visibility = VISIBLE
            avatar_three.setImageResource(avatars[(unlocked[2]).toInt()])
            avatar_two.setOnClickListener { changeAvatar(2) }
        }
        if (unlocked.size > 3) {
            avatar_four.visibility = VISIBLE
            avatar_four.setImageResource(avatars[(unlocked[3]).toInt()])
            avatar_two.setOnClickListener { changeAvatar(3) }
        }
    }

    private fun changeAvatar(i: Int) {
        if (i == owned) Toast.makeText(applicationContext, getString(R.string.avatar_in_use), Toast.LENGTH_LONG).show()
        else {
            val check = AlertDialog.Builder(this)
            check.setTitle(getString(R.string.avatar_change_alert_message))
            check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.updatePlayer(UpdateRequest(null, null, null, session))
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
                                //Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            check.setNegativeButton(getString(R.string.alert_negative_button)) { _: DialogInterface, _: Int -> }
            check.show()
        }
    }

}
