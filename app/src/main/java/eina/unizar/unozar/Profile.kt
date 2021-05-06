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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        session = intent.getStringExtra("session").toString()
        updateInfo()
    }

    private fun updateInfo() {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        showAlias.text = response.body()?.alias
                        showEmail2.text = response.body()?.email
                        showJugadasTotales.text = response.body()?.publicTotal.toString()
                        showGanadasTotales.text = response.body()?.publicWins.toString()
                        showJugadas.text = response.body()?.privateTotal.toString()
                        showGanadas.text = response.body()?.privateWins.toString()
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

    fun changeAlias() {
        RetrofitClient.instance.updatePlayer(UpdateRequest(null, null, null, session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, getString(R.string.password_change_success), Toast.LENGTH_LONG).show()
                        finish()
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
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
