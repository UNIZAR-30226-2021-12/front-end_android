package eina.unizar.unozar

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_game.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.TokenRequest
import server.response.GameInfoResponse
import server.response.TokenResponse

class CreatePrivateMatch : AppCompatActivity() {
    private var players = 2
    private var bots = 1
    private lateinit var session: String
    private lateinit var code: String
    private val invite = Menu.FIRST

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
        create.setOnClickListener{ createGame(View(this)) }
        RetrofitClient.instance.readGame(TokenRequest(session))
            .enqueue(object : Callback<GameInfoResponse> {
                override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "Hola", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<GameInfoResponse>, response: Response<GameInfoResponse>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, response.body()?.maxPlayers.toString(), Toast.LENGTH_LONG).show()
                        //La carta se ha puesto con éxito
                    } else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun createGame(@Suppress("UNUSED_PARAMETER")view: View) {
        RetrofitClient.instance.startMatch(session)
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(
                    call: Call<TokenResponse>,
                    response: Response<TokenResponse>
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
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, invite, Menu.NONE, "Invitar amigo")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { invite -> {
            val intent = Intent(this@CreatePrivateMatch, Friends::class.java)
            intent.putExtra("session", session)
            intent.putExtra("code", code)
            startActivity(intent)
            return true }}
        return super.onContextItemSelected(item)
    }

    fun quit(@Suppress("UNUSED_PARAMETER") view: View) {
        RetrofitClient.instance.quitMatch(TokenRequest(session))
            .enqueue(object : Callback<Void> {
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }
}
