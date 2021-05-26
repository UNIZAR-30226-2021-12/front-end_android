package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_board.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.UpdateRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class ChangeBoard : AppCompatActivity() {

    private var boards = arrayListOf(
        R.drawable.empty,
        R.drawable.tablero2,
        R.drawable.tablero3
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
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        if (response.body()!!.unlockedBoards.size > 1) {
                            board_two.visibility = VISIBLE
                            board_two.setImageResource(boards[(response.body()!!.unlockedBoards[1])])
                            board_two.setOnClickListener { changeBoard(response.body()!!.unlockedBoards[1]) }
                        }
                        if (response.body()!!.unlockedBoards.size > 2) {
                            board_three.visibility = VISIBLE
                            board_three.setImageResource(boards[(response.body()!!.unlockedBoards[2])])
                            board_three.setOnClickListener { changeBoard(response.body()!!.unlockedBoards[2]) }
                        }
                    }
                }
            })
        setContentView(R.layout.activity_change_board)

        go_back.setOnClickListener {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        board_one.setOnClickListener { changeBoard(0) }

    }

    private fun changeBoard(i: Int) {
        if (i == owned) Toast.makeText(applicationContext, getString(R.string.avatar_in_use), Toast.LENGTH_SHORT).show()
        else {
            val check = AlertDialog.Builder(this)
            check.setTitle(getString(R.string.board_change_alert_message))
            check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.updatePlayer(UpdateRequest(10, null, null, null, session, i, 10))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                setResult(Activity.RESULT_OK, intent)
                                Toast.makeText(applicationContext, getString(R.string.board_change_success), Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                //Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_SHORT).show()
                                Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
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
