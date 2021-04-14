package eina.unizar.unozar

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateGame : AppCompatActivity() {

    var players = 2
    private var bots = 1
    private var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        val n = intent.getIntExtra("num_players", 0)
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        if (n > 2) {
            val imageView: ImageView = findViewById(R.id.player_three)
            imageView.visibility = View.VISIBLE
            bots++
            players++
        }
        if (n > 3) {
            val imageView: ImageView = findViewById(R.id.player_four)
            imageView.visibility = View.VISIBLE
            bots++
            players++
        }
    }

    fun createGame(view: View) {
        RetrofitClient.instance.userCreateGame(session, players, bots)
            .enqueue(object: Callback<BasicResponse> {
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
                            response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                        //val intent = Intent(this, Tablero::class.java)
                        //intent.putExtra("password", response.body()?.message)
                        //startActivity(intent)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "error: " + response.body()?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            })
    }

    fun cancel(view: View) {
        finish()
    }
}
