package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_create_game.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_tablero.*
import kotlinx.android.synthetic.main.custom_alertdialog.*
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.*
import server.response.GiftResponse
import server.response.PlayerInfo
import server.response.RoomInfoResponse
import server.response.TokenResponse


class Principal : AppCompatActivity() {
    private var normalCode = 73
    private var n = 2
    private lateinit var session: String
    private lateinit var players: AlertDialog.Builder
    private var myMoney = 0
    private lateinit var ids: ArrayList<String>
    private lateinit var avatarIds: Map<Int,Int>
    private lateinit var names: Map<Int,String>
    private  var myPos: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        session = intent.getStringExtra("session").toString()
        this.updateMoney()
        ids = ArrayList()
        avatarIds = mapOf()
        names = mapOf()
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        if(!(response.body()!!.gameId.equals("NONE"))) {
                            RetrofitClient.instance.readRoom(TokenRequest(session))
                                .enqueue(object : Callback<RoomInfoResponse> {
                                    override fun onFailure(call: Call<RoomInfoResponse>, t: Throwable) {
                                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                                    } override fun onResponse(call: Call<RoomInfoResponse>, response: Response<RoomInfoResponse>) {
                                        if (response.code() == 200) {
                                            session = response.body()?.token.toString()
                                            for (i in response.body()!!.playersIds.indices) {
                                                if (response.body()!!.playersIds[i].equals(session.substring(0, 32))) myPos = i
                                                if(!(response.body()!!.playersIds[i].equals("EMPTY"))) {
                                                    actualizarJugador(response.body()!!.playersIds[i], i)
                                                }
                                            }
                                            val intent =
                                                Intent(this@Principal, TableroActivity::class.java)
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
                        }
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })

        val numPlayers = arrayOf("2", "3", "4")
        players = AlertDialog.Builder(this)
        players.setTitle(getString(R.string.number_of_players))
        players.setItems(numPlayers) { _: DialogInterface, i: Int ->
            n = numPlayers[i].toInt()
        }
    }

    private fun actualizarJugador(id: String, pos: Int) {
        if (id.equals("BOT")) {
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
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> {
                RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
                    .enqueue(object : Callback<PlayerInfo> {
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if (response.code() == 200) {
                                val intent = Intent(this@Principal, Profile::class.java)
                                intent.putExtra("session", session)
                                intent.putExtra("avatar", response.body()!!.avatarId)
                                intent.putExtra("alias", response.body()!!.alias)
                                intent.putExtra("email", response.body()!!.email)
                                intent.putExtra("private_matches", response.body()?.privateTotal.toString())
                                intent.putExtra("private_wins", response.body()?.privateWins.toString())
                                intent.putExtra("public_matches", response.body()?.publicTotal.toString())
                                intent.putExtra("public_wins", response.body()?.publicWins.toString())
                                startActivityForResult(intent, normalCode)
                            } else {
                                //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            R.id.change_board -> {
                RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
                    .enqueue(object : Callback<PlayerInfo> {
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if (response.code() == 200) {
                                val intent = Intent(this@Principal, ChangeBoard::class.java)
                                intent.putExtra("session", session)
                                intent.putExtra("owned", response.body()!!.boardId)
                                startActivityForResult(intent, normalCode)
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            R.id.change_reverse -> {
                RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
                    .enqueue(object : Callback<PlayerInfo> {
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if (response.code() == 200) {
                                val intent = Intent(this@Principal, ChangeCards::class.java)
                                intent.putExtra("session", session)
                                intent.putExtra("owned", response.body()!!.cardId)
                                startActivityForResult(intent, normalCode)
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            R.id.action_logout -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
            R.id.action_friends -> {
                val intent = Intent(this, Friends::class.java)
                intent.putExtra("session", session)
                startActivityForResult(intent, normalCode)
            }
            R.id.action_prize -> {
                RetrofitClient.instance.getDailyGift(TokenRequest(session))
                    .enqueue(object : Callback<GiftResponse> {
                        override fun onFailure(call: Call<GiftResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<GiftResponse>, response: Response<GiftResponse>) {
                            if (response.code() == 200) {
                                session = response.body()?.token.toString()
                                val premio = response.body()!!.gift.toString()
                                val builder = AlertDialog.Builder(this@Principal)
                                with(builder)
                                {
                                    setTitle("Enhorabuena")
                                    setMessage("Has ganado un premio de $premio monedas")
                                    val neutralButtonClick = null
                                    setPositiveButton("OK",neutralButtonClick)
                                    show()
                                }
                                this@Principal.updateMoney()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_refresh_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            R.id.action_shop -> {
                val intent = Intent(this, Tienda::class.java)
                intent.putExtra("session", session)
                intent.putExtra("money", myMoney)
                startActivityForResult(intent, normalCode)
            }
            R.id.action_delete_game -> {
                RetrofitClient.instance.quitMatch(TokenRequest(session))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                session = response.body()!!.token
                                Toast.makeText(applicationContext, "Ha salido de la partida", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            R.id.add_money -> {
                RetrofitClient.instance.addMoney(TokenRequest(session))
                    .enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.code() == 200) {
                                updateMoney()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun publicMatch(@Suppress("UNUSED_PARAMETER") view: View) {
        val numP = AlertDialog.Builder(this)
        val numPlayers = arrayOf("2", "3", "4")
        numP.setTitle(getString(R.string.number_of_players))
        numP.setItems(numPlayers) { _: DialogInterface, i: Int ->
            n = numPlayers[i].toInt()
            RetrofitClient.instance.joinPublic(JoinPublicRequest(n, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@Principal, MatchRoom::class.java)
                            intent.putExtra("numPlayers", n)
                            intent.putExtra("numBots", 0)
                            intent.putExtra("session", response.body()?.token)
                            intent.putExtra("public", true)
                            startActivityForResult(intent, normalCode)
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                            //Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        numP.show()
    }

    fun privateMatch(@Suppress("UNUSED_PARAMETER") view: View) {
        var b: Int
        val choose = AlertDialog.Builder(this)
        choose.setTitle(getString(R.string.choose))
        choose.setMessage(getString(R.string.private_match_dialog))
        choose.setPositiveButton(getString(R.string.create_button)) { _: DialogInterface, _: Int ->
            val numP = AlertDialog.Builder(this)
            val numPlayers = arrayOf("2", "3", "4")
            numP.setTitle(getString(R.string.number_of_players))
            numP.setItems(numPlayers) { _: DialogInterface, i: Int ->
                n = numPlayers[i].toInt()
                val bots = AlertDialog.Builder(this)
                var numBots:Array<String>
                if (n > 2) {
                    numBots = arrayOf("0", "1", "2")
                    if (n > 3) numBots = arrayOf("0", "1", "2", "3")
                } else
                    numBots = arrayOf("0", "1")
                bots.setTitle(getString(R.string.number_of_bots))
                bots.setItems(numBots) { _: DialogInterface, j: Int ->
                    b = numBots[j].toInt()
                    d("Test", "numPlayers: $n, numBots: $b")
                    RetrofitClient.instance.createMatch(CreateMatchRequest(true, n, b, session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                                    val intent = Intent(this@Principal, MatchRoom::class.java)
                                    intent.putExtra("numPlayers", n)
                                    intent.putExtra("numBots", b)
                                    intent.putExtra("session", response.body()?.token)
                                    intent.putExtra("public", false)
                                    startActivityForResult(intent, normalCode)
                                } else {
                                    //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                }
                bots.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
                bots.show()
            }
            numP.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
            numP.show()
        }
        choose.setNegativeButton(getString(R.string.join_button)) { _: DialogInterface, _: Int ->
            val code = AlertDialog.Builder(this)
            val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
            code.setView(customLayout)
            code.setTitle(getString(R.string.code))
            code.setPositiveButton(getString(R.string.join_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.joinPrivate(JoinPrivateRequest(customLayout.inputCode.text.toString().trim(), session))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                                val intent = Intent(this@Principal, MatchRoom::class.java)
                                intent.putExtra("session", response.body()?.token)
                                intent.putExtra("public", false)
                                startActivityForResult(intent, normalCode)
                            } else {
                                //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            code.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
            code.show()
        }
        choose.show()
    }

    private fun updateMoney() {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        money.text = response.body()?.money.toString()
                        myMoney = response.body()!!.money
                    }
                    else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_read_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == normalCode) {
            session = data!!.getStringExtra("session").toString()
            updateMoney()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
