package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_game.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.TokenRequest
import server.response.TokenResponse

/*import android.content.Intent
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response*/

class JoinMatch : AppCompatActivity() {
    private var CODE = 73
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
        exit.setOnClickListener{ quit(View(this)) }
    }

    fun quit(@Suppress("UNUSED_PARAMETER") view: View) {
        RetrofitClient.instance.quitMatch(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        session = response.body()!!.token
                        Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                        val intent = Intent().apply { putExtra("session", session) }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CODE) {
            setResult(Activity.RESULT_OK, data)
            finish()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
