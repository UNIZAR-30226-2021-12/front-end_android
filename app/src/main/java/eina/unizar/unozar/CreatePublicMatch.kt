package eina.unizar.unozar

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_create_game.*

class CreatePublicMatch : AppCompatActivity() {
    private var players = 2
    private lateinit var session: String

    override fun onCreate(savedInstanceState: Bundle?) {
        val n:Int = intent.getIntExtra("numPlayers", 0)
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        player_one.setImageResource(R.drawable.test_user)
        player_two.setImageResource(R.drawable.ai)
        if (n > 2) {
            player_three.setImageResource(R.drawable.ai)
            player_three.visibility = View.VISIBLE
            players++
            if (n > 3) {
                player_four.setImageResource(R.drawable.ai)
                players++
            } else {
                player_four.visibility = View.INVISIBLE
            }
        } else {
            player_three.visibility = View.INVISIBLE
            player_four.visibility = View.INVISIBLE
        }
        exit.setOnClickListener{ finish() }
        create.setOnClickListener{ createGame() }
    }

    private fun createGame() {
        /*RetrofitClient.instance.userCreateMatch(CreateMatchRequest(false, players, 0, session))
            .enqueue(object : Callback<BasicResponse> {
                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            response.code(),
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@CreatePublicMatch, TableroActivity::class.java)
                        intent.putExtra("session", response.code())
                        startActivity(intent)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "error: " + response.code(),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            })*/
    }
}
