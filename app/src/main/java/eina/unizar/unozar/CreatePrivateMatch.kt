package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_game.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePrivateMatch : AppCompatActivity() {
    private val tested = false
    private var players = 2
    private var bots = 1
    private lateinit var session: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val n:Int = intent.getIntExtra("numPlayers", 0)
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        setSupportActionBar(match_toolbar)

        player_one.setImageResource(R.drawable.test_user)
        player_two.setImageResource(R.drawable.ai)
        if (n > 2) {
            player_three.setImageResource(R.drawable.ai)
            player_three.visibility = View.VISIBLE
            bots++
            players++
            if (n > 3) {
                player_four.setImageResource(R.drawable.ai)
                bots++
                players++
            } else {
                player_four.visibility = View.INVISIBLE
            }
        } else {
            player_three.visibility = View.INVISIBLE
            player_four.visibility = View.INVISIBLE
        }
        cancel.setOnClickListener{ finish() }
        create.setOnClickListener{ createGame() }
    }

    private fun createGame() {
        if (tested) {
            RetrofitClient.instance.userCreateGame(session, players, bots)
                .enqueue(object : Callback<BasicResponse> {
                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {
                        if (response.code() == 200) {
                            val intent = Intent(this@CreatePrivateMatch, TableroActivity::class.java)
                            intent.putExtra("session", response.code())
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error " + response.code(),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }

                })
        } else {
            val test = TestCalls("test")
            val intent = Intent(this@CreatePrivateMatch, TableroActivity::class.java)
            intent.putExtra("session", test.userCreateGameTest())
            startActivity(intent)
        }
    }
}
