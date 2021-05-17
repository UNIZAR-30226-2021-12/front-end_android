package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
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
import server.response.PlayerInfo
import server.response.RoomInfoResponse
import server.response.TokenResponse

// Gestionar feedback join
class PrivateRoom : AppCompatActivity() {
    private var normalCode = 73
    private var inviteCode = 52
    private var players = 2
    private var bots = 0
    /** Flags **/
    private var started = false
    private var gone = false
    private var start = false
    private var invite = false
    private var done = false
    private var quit = false
    private var owner = false


    private lateinit var img: ArrayList<ImageView>
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.castor,
        R.drawable.jesica,
        R.drawable.larry,
        R.drawable.oso
    )
    private lateinit var session: String
    private lateinit var code: String
    private val inviteFriend = Menu.FIRST
    private lateinit var ids: ArrayList<String>
    private lateinit var avatarIds: Map<Int,Int>
    private lateinit var names: Map<Int,String>
    private  var myPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        players = intent.getIntExtra("numPlayers", 0)
        bots = intent.getIntExtra("numBots", 0)
        session = intent.getStringExtra("session").toString()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        setSupportActionBar(match_toolbar)
        ids = ArrayList()
        avatarIds = mapOf()
        names = mapOf()
        img = ArrayList()
        img.add(player_one)
        img.add(player_two)
        img.add(player_three)
        img.add(player_four)

        RetrofitClient.instance.readRoom(TokenRequest(session))
            .enqueue(object : Callback<RoomInfoResponse> {
                override fun onFailure(call: Call<RoomInfoResponse>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.no_response),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onResponse(
                    call: Call<RoomInfoResponse>,
                    response: Response<RoomInfoResponse>
                ) {
                    if (response.code() == 200) {
                        session = response.body()?.token.toString()
                        if ((response.body()!!.playersIds[0]).equals(session.substring(0,32))) {
                            create.isEnabled = false
                            owner = true
                        }
                        for (i in response.body()!!.playersIds.indices) {
                            ids.add(response.body()!!.playersIds[i])
                            if ((response.body()!!.playersIds[i]).equals("BOT")) {
                                avatarIds = avatarIds + Pair(i, 2)
                                names = names + Pair(i, "IA")
                                img[i].setImageResource(R.drawable.robotia)
                            } else {
                                actualizarJugador(response.body()!!.playersIds[i], i)
                            }
                        }
                        done = true
                    } else {
                        Toast.makeText(
                            applicationContext,
                            response.code(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            })
        create.setOnClickListener { start = true }
        exit.setOnClickListener { quit = true }
        actualizar()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, inviteFriend, Menu.NONE, "Invitar amigo")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { inviteFriend -> {
            invite = true
            return true }}
        return super.onContextItemSelected(item)
    }

    private fun actualizarJugador(id: String, pos: Int) {
        if (id.equals("BOT")) {
            img[pos].setImageResource(R.drawable.robotia)
            avatarIds = avatarIds + Pair(pos, 2)
            names = names + Pair(pos, "BOT")
        }
        else {
            RetrofitClient.instance.readPlayer(IdRequest(id))
                .enqueue(object : Callback<PlayerInfo> {
                    override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                    } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                        if (response.code() == 200) {
                            avatarIds = avatarIds + Pair(pos, response.body()!!.avatarId)
                            names = names + Pair(pos, response.body()!!.alias)
                            img[pos].setImageResource(avatars[response.body()!!.avatarId])
                        } else {
                            //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_SHORT).show()
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    private fun actualizar(){
        CoroutineScope(Dispatchers.IO).launch {
            while (!started && !gone) {
                if (start && done) {     /** Inicio de partida **/
                    done = false
                    start = false
                    RetrofitClient.instance.startMatch(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    started = true
                                    val intent =
                                        Intent(this@PrivateRoom, TableroActivity::class.java)
                                    intent.putExtra("session", response.body()!!.token)
                                    intent.putExtra("ids", ids.toTypedArray())
                                    intent.putExtra("myPosition", myPos)
                                    intent.putExtra("avatars", avatarIds.values.toTypedArray())
                                    intent.putExtra("names", names.values.toTypedArray())
                                    startActivityForResult(intent, normalCode)
                                } else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })

                } else if (quit && done) {     /** Salir de la sala **/
                    done = false
                    quit = false
                    RetrofitClient.instance.quitMatch(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    gone = true
                                    Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_SHORT).show()
                                    val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, getString(R.string.bad_creation_response), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                } else if (invite && done) {     /** Invitar amigo **/
                    invite = false
                    val intent = Intent(this@PrivateRoom, ChooseFriend::class.java)
                    intent.putExtra("session", session)
                    intent.putExtra("code", code)
                    startActivityForResult(intent, inviteCode)
                } else if(done) {     /** Actualización de la sala **/
                    done = false
                    RetrofitClient.instance.readRoom(TokenRequest(session))
                        .enqueue(object : Callback<RoomInfoResponse> {
                            override fun onFailure(call: Call<RoomInfoResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<RoomInfoResponse>, response: Response<RoomInfoResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    if (response.body()!!.gameStarted) {
                                        started = true
                                        val intent =
                                            Intent(this@PrivateRoom, TableroActivity::class.java)
                                        intent.putExtra("session", session)
                                        intent.putExtra("ids", ids.toTypedArray())
                                        intent.putExtra("posicion", myPos)
                                        startActivityForResult(intent, normalCode)
                                    } else {
                                        if (response.body()!!.playersIds[0] == session.substring(0,32))
                                            owner = true
                                        ids = ArrayList()
                                        for (i in response.body()!!.playersIds.indices) {
                                            if (i >= ids.size || !(response.body()!!.playersIds[i].equals(ids[i]))) {
                                                actualizarJugador(response.body()!!.playersIds[i], i)
                                            }
                                            if (response.body()!!.playersIds[i].equals(session.substring(0, 32))) myPos = i
                                            //Toast.makeText(applicationContext, "Mi posicion: $myPos", Toast.LENGTH_SHORT).show()
                                            //else idsOtros.add(response.body()!!.playersIds[i])
                                            ids.add(response.body()!!.playersIds[i])
                                        }
                                        done = true
                                    }
                                } else  {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                    delay(1000)
                }
            }
        }
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == normalCode) {
            setResult(Activity.RESULT_OK, data)
            finish()
        } else if (resultCode == Activity.RESULT_OK && requestCode == inviteCode) {
            session = data!!.getStringExtra("session").toString()
            done = true
        }
        else { super.onActivityResult(requestCode, resultCode, data) }
    }

    override fun onDestroy() {
        super.onDestroy()
        RetrofitClient.instance.quitMatch(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        session = response.body()?.token.toString()
                        Toast.makeText(applicationContext, getString(R.string.quit_game_success), Toast.LENGTH_SHORT).show()
                        val intent = Intent().apply { putExtra("session", session) }
                        setResult(Activity.RESULT_OK, intent)
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_SHORT).show()
                    }
                }
            })
    }
}
