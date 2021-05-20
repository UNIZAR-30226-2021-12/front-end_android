package eina.unizar.unozar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_change_email.*
import kotlinx.android.synthetic.main.activity_profile.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.TokenRequest
import server.request.UpdateRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class Profile : AppCompatActivity() {

    private var CODE = 73
    private lateinit var session: String
    private val avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.oso,
        R.drawable.larry,
        R.drawable.jesica
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        session = intent.getStringExtra("session").toString()
        showAlias.setText(intent.getStringExtra("alias"), TextView.BufferType.EDITABLE)
        showEmail2.text = intent.getStringExtra("email")
        showJugadasTotales.text = intent.getStringExtra("private_matches")
        showGanadasTotales.text = intent.getStringExtra("private_wins")
        showJugadas.text = intent.getStringExtra("public_matches")
        showGanadas.text = intent.getStringExtra("public_wins")
        avatar.setImageResource(avatars[intent.getIntExtra("avatar", 0)])
    }

    private fun updateInfo() {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        showAlias.setText(response.body()?.alias, TextView.BufferType.EDITABLE)
                        showEmail2.text = response.body()?.email
                        showJugadasTotales.text = response.body()?.publicTotal.toString()
                        showGanadasTotales.text = response.body()?.publicWins.toString()
                        showJugadas.text = response.body()?.privateTotal.toString()
                        showGanadas.text = response.body()?.privateWins.toString()
                        avatar.setImageResource(avatars[response.body()!!.avatarId])
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    @SuppressLint("SetTextI18n")
    fun goToChangeEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivityForResult(intent, CODE)
    }

    fun goToChangePassword(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, PasswordChange::class.java)
        intent.putExtra("session", session)
        startActivityForResult(intent, CODE)
    }

    fun goToChangeAvatar(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, ChangeAvatar::class.java)
        intent.putExtra("session", session)
        startActivityForResult(intent, CODE)
    }

    fun changeAlias(@Suppress("UNUSED_PARAMETER") view: View) {
        edit_alias.setImageResource(R.drawable.save_icon)
        showAlias.isFocusable = true
        showAlias.setSelection(0);
        edit_alias.setOnClickListener {
            RetrofitClient.instance.updatePlayer(UpdateRequest(null, showAlias.text.toString().trim(), null, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()!!.token
                            Toast.makeText(applicationContext, getString(R.string.alias_change_success), Toast.LENGTH_LONG).show()
                            edit_alias.setImageResource(R.drawable.edit_alias)
                            showAlias.isFocusable = false
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
            edit_alias.setOnClickListener { changeAlias(this.view) }
        }

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
                            val intent = Intent(this@Profile, Register::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_delete_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        check.setNegativeButton(getString(R.string.alert_negative_button)) { _: DialogInterface, _: Int -> }
        check.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CODE) {
            session = data!!.getStringExtra("session").toString()
            updateInfo()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
