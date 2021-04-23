package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_create_game.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JoinMatch : AppCompatActivity() {
    private val tested = false
    private var players = 2
    private var bots = 1
    private var session = ""

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
        }
        if (n > 3) {
            player_four.setImageResource(R.drawable.ai)
            player_four.visibility = View.VISIBLE
            bots++
            players++
        }
        exit.setOnClickListener{ finish() }
    }
}
