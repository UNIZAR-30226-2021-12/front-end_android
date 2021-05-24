package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_change_cards.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.UpdateRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class ChangeCards : AppCompatActivity() {

    private var cards = arrayListOf(
        R.drawable.reverso1,
        R.drawable.reverso2,
        R.drawable.reverso3,
        R.drawable.reverso4,
        R.drawable.reverso5
    )
    private  var owned = 0
    private lateinit var session: String
    override fun onCreate(savedInstanceState: Bundle?) {
        session = intent.getStringExtra("session").toString()
        owned = intent.getIntExtra("owned", 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_cards)
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        if (response.body()!!.unlockedCards.size > 1) {
                            card_two.visibility = VISIBLE
                            card_two.setImageResource(cards[(response.body()!!.unlockedCards[1])])
                            card_two.setOnClickListener { changeCards(response.body()!!.unlockedCards[1]) }
                        }
                        if (response.body()!!.unlockedCards.size > 2) {
                            card_three.visibility = VISIBLE
                            card_three.setImageResource(cards[(response.body()!!.unlockedCards[2])])
                            card_three.setOnClickListener { changeCards(response.body()!!.unlockedCards[2]) }
                        }
                        if (response.body()!!.unlockedCards.size > 3) {
                            card_four.visibility = VISIBLE
                            card_four.setImageResource(cards[(response.body()!!.unlockedCards[3])])
                            card_four.setOnClickListener { changeCards(response.body()!!.unlockedCards[3]) }
                        }
                        if (response.body()!!.unlockedCards.size > 4) {
                            card_five.visibility = VISIBLE
                            card_five.setImageResource(cards[(response.body()!!.unlockedCards[4])])
                            card_five.setOnClickListener { changeCards(response.body()!!.unlockedCards[4]) }
                        }
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })

        go_back.setOnClickListener {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
        }
        card_one.setImageResource(cards[0])
        card_one.setOnClickListener { changeCards(0) }

    }

    private fun changeCards(i: Int) {
        if (i == owned) Toast.makeText(applicationContext, getString(R.string.avatar_in_use), Toast.LENGTH_LONG).show()
        else {
            val check = AlertDialog.Builder(this)
            check.setTitle(getString(R.string.cards_change_alert_message))
            check.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.updatePlayer(UpdateRequest(10, null, null, null, session, 10, i))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                setResult(Activity.RESULT_OK, intent)
                                Toast.makeText(applicationContext, getString(R.string.cards_change_success), Toast.LENGTH_LONG).show()
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
