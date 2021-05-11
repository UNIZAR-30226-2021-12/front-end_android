package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_game.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.TokenRequest
import server.response.GameInfoResponse
import server.response.PlayerInfo
import server.response.RoomInfoResponse
import server.response.TokenResponse
import java.util.concurrent.Semaphore
import java.util.ArrayList

class CreatePrivateMatch : AppCompatActivity() {
    private var CODE = 73
    private var players = 2
    private var bots = 1
    private var started = false
    private var gone = false
    private lateinit var session: String
    private lateinit var code: String
    private val invite = Menu.FIRST
    private lateinit var ids: ArrayList<String>
    private val s = Semaphore(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        val n:Int = intent.getIntExtra("numPlayers", 0)
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        setSupportActionBar(match_toolbar)
        ids = ArrayList<String>(n)

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
        /*RetrofitClient.instance.readGame(TokenRequest(session))
            .enqueue(object : Callback<GameInfoResponse> {
                override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<GameInfoResponse>, response: Response<GameInfoResponse>) {
                    if (response.code() == 200) {
                        //Toast.makeText(applicationContext, "Actualización", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })*/

        actualizar()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, invite, Menu.NONE, "Invitar amigo")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { invite -> {
            val intent = Intent(this@CreatePrivateMatch, ChooseFriend::class.java)
            intent.putExtra("session", session)
            intent.putExtra("code", code)
            startActivity(intent)
            return true }}
        return super.onContextItemSelected(item)
    }

    fun quit(@Suppress("UNUSED_PARAMETER") view: View) {
        s.acquireUninterruptibly()
        gone = true
        RetrofitClient.instance.quitMatch(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    //gone = true
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                        val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
        s.release()
    }

    fun start(@Suppress("UNUSED_PARAMETER") view: View) {
        s.acquireUninterruptibly()
        runOnUiThread {
            started = true
            RetrofitClient.instance.startMatch(TokenRequest(session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<TokenResponse>,
                        response: Response<TokenResponse>
                    ) {
                        if (response.code() == 200) {
                            //started = true
                            Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                            val intent =
                                Intent(this@CreatePrivateMatch, TableroActivity::class.java)
                            intent.putExtra("session", response.body()!!.token)
                            startActivityForResult(intent, CODE)
                        } else {
                            //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG)
                                .show()
                        }
                    }
                })
        }
        s.release()
    }

    private fun actualizarJugador(id: String) {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        session = response.body()?.token.toString()
                        /*showAlias.text = response.body()?.alias
                        showEmail2.text = response.body()?.email
                        showJugadasTotales.text = response.body()?.publicTotal.toString()
                        showGanadasTotales.text = response.body()?.publicWins.toString()
                        showJugadas.text = response.body()?.privateTotal.toString()
                        showGanadas.text = response.body()?.privateWins.toString()*/
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun actualizar(){
        CoroutineScope(Dispatchers.IO).launch {
            while(!started && !gone){
                runOnUiThread {
                    s.acquireUninterruptibly()
                    RetrofitClient.instance.readRoom(TokenRequest(session))
                        .enqueue(object : Callback<RoomInfoResponse> {
                            override fun onFailure(call: Call<RoomInfoResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            }

                            override fun onResponse(
                                call: Call<RoomInfoResponse>,
                                response: Response<RoomInfoResponse>
                            ) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    for (i in 0..response.body()!!.playersIds.size) {
                                        /*if(response.body()!!.playersIds[i] != ids[i] && response.body()!!.playersIds[i] != "BOT") {
                                        actualizarJugador(response.body()!!.playersIds[i])
                                    }*/
                                    }
                                    //Toast.makeText(applicationContext, "Actualización", Toast.LENGTH_LONG).show()
                                } else { Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show() }
                            }
                        })
                    s.release()
                    //delay(1000)
                }
                delay(1000)
            }
        }
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == CODE) {
            setResult(Activity.RESULT_OK, data)
            finish()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
