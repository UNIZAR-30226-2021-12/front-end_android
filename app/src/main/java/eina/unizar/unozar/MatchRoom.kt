package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_create_game.*
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

class MatchRoom : AppCompatActivity() {
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
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )
    private lateinit var session: String
    private lateinit var code: String
    private var isPublic = false
    private lateinit var ids: ArrayList<String>
    private lateinit var avatarIds: Map<Int,Int>
    private lateinit var names: Map<Int,String>
    private  var myPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        setSupportActionBar(match_toolbar)
        players = intent.getIntExtra("numPlayers", 0)
        bots = intent.getIntExtra("numBots", 0)
        session = intent.getStringExtra("session").toString()
        isPublic = intent.getBooleanExtra("public", true)

        if (isPublic) {
            room_type.text = getString(R.string.public_game)
        } else room_type.text = getString(R.string.private_game)

        code = ""
        ids = ArrayList()
        avatarIds = mapOf()
        names = mapOf()
        img = ArrayList()
        img.add(avatar_one)
        img.add(avatar_two)
        img.add(avatar_three)
        img.add(avatar_four)

        RetrofitClient.instance.readRoom(TokenRequest(session))
            .enqueue(object : Callback<RoomInfoResponse> {
                override fun onFailure(call: Call<RoomInfoResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                } override fun onResponse(call: Call<RoomInfoResponse>, response: Response<RoomInfoResponse>) {
                    if (response.code() == 200) {
                        players = response.body()!!.maxPlayers
                        players_ready.text = getString(R.string.players_ready, (bots+1).toString(), players.toString())
                        if (players > 2) avatar_three.visibility = VISIBLE
                        if (players > 3) avatar_four.visibility = VISIBLE
                        if(isPublic) apuesta.text = getString(R.string.bet, response.body()!!.bet.toString())
                        session = response.body()?.token.toString()
                        if ((response.body()!!.playersIds[0]).equals(session.substring(0,32))) {
                            owner = true
                        }
                        for (i in response.body()!!.playersIds.indices) {
                            if(!(response.body()!!.playersIds[i].equals("EMPTY"))) {
                                actualizarJugador(response.body()!!.playersIds[i], i)
                            }
                        }
                        done = true
                    } else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                    }
                }
            })
        create.setOnClickListener {
            if (owner && ids.size == players) {
                start = true
            }
            else Toast.makeText(applicationContext, getString(R.string.not_owner), Toast.LENGTH_SHORT).show()
        }
        exit.setOnClickListener { quit = true }
        actualizar()
    }

    private fun actualizarJugador(id: String, pos: Int) {
        if (id.equals("BOT")) {
            img[pos].setImageResource(avatars[1])
            avatarIds = avatarIds + Pair(pos, 1)
            names = names + Pair(pos, "BOT")
        }
        else {
            avatarIds = avatarIds + Pair(pos, 0)
            names = names + Pair(pos, "Yo")
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
                            Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_SHORT).show()
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
                                        Intent(this@MatchRoom, TableroActivity::class.java)
                                    intent.putExtra("session", response.body()!!.token)
                                    intent.putExtra("ids", ids.toTypedArray())
                                    intent.putExtra("myPosition", myPos)
                                    intent.putExtra("avatars", avatarIds.values.map { it.toString() }.toTypedArray())
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
                    done = false
                    invite = false
                    val intent = Intent(this@MatchRoom, ChooseFriend::class.java)
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
                                    code = response.body()!!.gameId
                                    session = response.body()?.token.toString()
                                    if (response.body()!!.gameStarted) {
                                        started = true
                                        val intent =
                                            Intent(this@MatchRoom, TableroActivity::class.java)
                                        intent.putExtra("session", response.body()!!.token)
                                        intent.putExtra("ids", ids.toTypedArray())
                                        intent.putExtra("myPosition", myPos)
                                        intent.putExtra("avatars", avatarIds.values.map { it.toString() }.toTypedArray())
                                        intent.putExtra("names", names.values.toTypedArray())
                                        startActivityForResult(intent, normalCode)
                                    } else {
                                        if (response.body()!!.playersIds[0] == session.substring(0,32))
                                            owner = true
                                        ids = ArrayList()
                                        for (i in response.body()!!.playersIds.indices) {
                                            if(!(response.body()!!.playersIds[i].equals("EMPTY"))) {
                                                if (i >= ids.size || !(response.body()!!.playersIds[i].equals(ids[i]))) {
                                                    actualizarJugador(response.body()!!.playersIds[i], i)
                                                }
                                                if (response.body()!!.playersIds[i].equals(session.substring(0, 32))) myPos = i
                                                ids.add(response.body()!!.playersIds[i])
                                            }
                                        }
                                        players_ready.text = getString(R.string.players_ready, ids.size.toString(), players.toString())
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
        if (!gone) {
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.room_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_invite -> {
                if (ids.size < players)  invite = true
                else Toast.makeText(applicationContext, getString(R.string.game_full), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
