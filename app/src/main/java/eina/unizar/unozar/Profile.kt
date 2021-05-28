package eina.unizar.unozar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.TokenRequest
import server.request.UpdateRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class Profile : AppCompatActivity() {

    private var normalCode = 73
    private lateinit var session: String
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        session = intent.getStringExtra("session").toString()
        my_alias.text = intent.getStringExtra("alias")
        my_id.text = getString(R.string.my_id, intent.getStringExtra("id"))
        my_email.text = intent.getStringExtra("email")
        total_played.text = intent.getStringExtra("total_matches")
        total_wins.text = intent.getStringExtra("total_wins")
        private_played.text = intent.getStringExtra("friend_matches")
        private_wins.text = intent.getStringExtra("friend_wins")
        my_avatar.setImageResource(avatars[intent.getIntExtra("avatar", 0)])
        edit_alias.setOnClickListener {
            val code = AlertDialog.Builder(this)
            val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
            code.setView(customLayout)
            code.setTitle(getString(R.string.new_alias))
            code.setPositiveButton(getString(R.string.change)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.updatePlayer(UpdateRequest(10, null, customLayout.inputCode.text.toString().trim(), null, session, 10, 10))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                session = response.body()!!.token
                                Toast.makeText(applicationContext, getString(R.string.alias_change_success), Toast.LENGTH_LONG).show()
                                my_alias.text = customLayout.inputCode.text.toString().trim()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            code.setNegativeButton(getString(R.string.cancel)) {_, _ ->}
            code.show()
        }
    }

    private fun updateInfo() {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        my_alias.text = response.body()?.alias
                        my_id.text = getString(R.string.my_id, response.body()?.id)
                        my_email.text = response.body()?.email
                        total_played.text = response.body()!!.publicTotal.toString()
                        total_wins.text = response.body()!!.publicWins.toString()
                        private_played.text = response.body()?.privateTotal.toString()
                        private_wins.text = response.body()?.privateWins.toString()
                        my_avatar.setImageResource(avatars[response.body()!!.avatarId])
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun goToChangeEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivityForResult(intent, normalCode)
    }

    fun goToChangePassword(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, PasswordChange::class.java)
        intent.putExtra("session", session)
        startActivityForResult(intent, normalCode)
    }

    fun goToChangeAvatar(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ChangeAvatar::class.java)
        intent.putExtra("session", session)
        intent.putExtra("owned", intent.getIntExtra("avatar", 0))
        startActivityForResult(intent, normalCode)
    }

    fun deleteAccount(@Suppress("UNUSED_PARAMETER") view: View) {
        val check = AlertDialog.Builder(this)
        check.setTitle(getString(R.string.alert))
        check.setMessage(getString(R.string.delete_account_alert_message))
        check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
            RetrofitClient.instance.deleteAccount(TokenRequest(session))
                .enqueue(object : Callback<Void> {
                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.code() == 200) {
                            val intent = Intent().apply {
                                putExtra("session", session)
                                putExtra("delete", true)
                            }
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_delete_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        check.setNegativeButton(getString(R.string.alert_negative_button)) { _: DialogInterface, _: Int -> }
        check.show()
    }

    override fun onBackPressed() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == normalCode) {
            session = data!!.getStringExtra("session").toString()
            updateInfo()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
    }
}
