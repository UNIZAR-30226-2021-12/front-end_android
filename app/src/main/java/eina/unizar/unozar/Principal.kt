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
import kotlinx.android.synthetic.main.activity_principal.*
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
                                    } override fun onResponse(call: Call<RoomInfoResponse>, response1: Response<RoomInfoResponse>) {
                                        if (response1.code() == 200) {
                                            session = response1.body()?.token.toString()
                                            if (response1.body()!!.gameStarted) {
                                                for (i in response1.body()!!.playersIds.indices) {
                                                    if (response1.body()!!.playersIds[i].equals(session.substring(0, 32))) myPos = i
                                                    if(!(response1.body()!!.playersIds[i].equals("EMPTY"))) {
                                                        actualizarJugador(response1.body()!!.playersIds[i], i)
                                                    }
                                                }
                                                val intent =
                                                    Intent(this@Principal, TableroActivity::class.java)
                                                intent.putExtra("session", response1.body()!!.token)
                                                intent.putExtra("ids", ids.toTypedArray())
                                                intent.putExtra("myPosition", myPos)
                                                intent.putExtra("avatars", avatarIds.values.map { it.toString() }.toTypedArray())
                                                intent.putExtra("names", names.values.toTypedArray())
                                                intent.putExtra("board", response.body()!!.boardId)
                                                intent.putExtra("card", response.body()!!.cardId)
                                                startActivityForResult(intent, normalCode)
                                            } else {
                                                RetrofitClient.instance.quitMatch(TokenRequest(session))
                                                    .enqueue(object : Callback<TokenResponse> {
                                                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                                                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                                            if (response.code() == 200) {
                                                                session = response.body()!!.token
                                                            } else {
                                                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    })
                                            }
                                        } else {
                                            Toast.makeText(applicationContext, response1.code(), Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                })
                        }
                    }
                }
            })

        val numPlayers = arrayOf("2", "3", "4")
        players = AlertDialog.Builder(this)
        players.setTitle(getString(R.string.number_of_players))
        players.setItems(numPlayers) { _: DialogInterface, i: Int ->
            n = numPlayers[i].toInt()
        }
        create_match.setOnClickListener { createMatch() }
        search_public.setOnClickListener { searchPublic() }
        join_match.setOnClickListener { joinWithCode() }
    }

    private fun actualizarJugador(id: String, pos: Int) {
        if (id.equals("BOT")) {
            avatarIds = avatarIds + Pair(pos, 1)
            names = names + Pair(pos, "BOT")
        }
        else {
            avatarIds = avatarIds + Pair(pos, 0)
            names = names + Pair(pos, getString(R.string.img))
            RetrofitClient.instance.readPlayer(IdRequest(id))
                .enqueue(object : Callback<PlayerInfo> {
                    override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                    } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                        if (response.code() == 200) {
                            avatarIds = avatarIds + Pair(pos, response.body()!!.avatarId)
                            names = names + Pair(pos, response.body()!!.alias)
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
                                intent.putExtra("total_matches", (response.body()!!.publicTotal + response.body()!!.privateTotal).toString())
                                intent.putExtra("total_wins", (response.body()!!.publicWins + response.body()!!.privateWins).toString())
                                intent.putExtra("friend_matches", response.body()?.privateTotal.toString())
                                intent.putExtra("friend_wins", response.body()?.privateWins.toString())
                                startActivityForResult(intent, normalCode)
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
                            }
                        }
                    })
            }
            R.id.action_logout -> { finish() }
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
                                val builder = AlertDialog.Builder(this@Principal)
                                with(builder)
                                {
                                    setTitle(getString(R.string.congratulations))
                                    setMessage(getString(R.string.price_message, response.body()!!.gift.toString()))
                                    val neutralButtonClick = null
                                    setPositiveButton(getString(R.string.go_back),neutralButtonClick)
                                    show()
                                }
                                this@Principal.updateMoney()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.price_already_collected), Toast.LENGTH_LONG).show()
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
            /** BORRAR **/
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
            /** BORRAR **/
            R.id.add_money -> {
                RetrofitClient.instance.addMoney(TokenRequest(session))
                    .enqueue(object : Callback<Void> {
                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.code() == 200) {
                                updateMoney()
                            }
                        }
                    })
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createMatch() {
        val choose = AlertDialog.Builder(this)
        choose.setTitle(getString(R.string.choose))
        choose.setMessage(getString(R.string.create_match_dialog))
        choose.setPositiveButton(getString(R.string.pub)) { _: DialogInterface, _: Int ->
            val numP = AlertDialog.Builder(this)
            val numPlayers = arrayOf("2", "3", "4")
            numP.setTitle(getString(R.string.number_of_players))
            numP.setItems(numPlayers) { _: DialogInterface, i: Int ->
                n = numPlayers[i].toInt()
                val code = AlertDialog.Builder(this)
                val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
                code.setView(customLayout)
                code.setTitle(getString(R.string.set_initial_bet))
                code.setPositiveButton(getString(R.string.create_button)) { _: DialogInterface, _: Int ->
                    val myBet = customLayout.inputCode.text.toString().trim().toInt()
                    if (myBet <= myMoney) {
                        RetrofitClient.instance.createMatch(CreateMatchRequest(false, n, 0, myBet, session))
                            .enqueue(object : Callback<TokenResponse> {
                                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                    if (response.code() == 200) {
                                        val intent = Intent(this@Principal, MatchRoom::class.java)
                                        intent.putExtra("numPlayers", n)
                                        intent.putExtra("numBots", 0)
                                        intent.putExtra("session", response.body()?.token)
                                        intent.putExtra("public", true)
                                        intent.putExtra("bet", myBet)
                                        startActivityForResult(intent, normalCode)
                                    }
                                }
                            })
                    } else  Toast.makeText(applicationContext, getString(R.string.not_enough_money), Toast.LENGTH_LONG).show()
                }
                code.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
                code.show()
            }
            numP.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
            numP.show()
        }
        choose.setNegativeButton(getString(R.string.priv)) { _: DialogInterface, _: Int ->
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
                    val b = numBots[j].toInt()
                    RetrofitClient.instance.createMatch(CreateMatchRequest(true, n, b, 0, session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    val intent = Intent(this@Principal, MatchRoom::class.java)
                                    intent.putExtra("numPlayers", n)
                                    intent.putExtra("numBots", b)
                                    intent.putExtra("session", response.body()?.token)
                                    intent.putExtra("public", false)
                                    startActivityForResult(intent, normalCode)
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
        choose.show()
    }

    private fun searchPublic() {
        val numP = AlertDialog.Builder(this)
        val numPlayers = arrayOf("2", "3", "4")
        numP.setTitle(getString(R.string.number_of_players))
        numP.setItems(numPlayers) { _: DialogInterface, i: Int ->
            n = numPlayers[i].toInt()
            RetrofitClient.instance.joinPublic(JoinPublicRequest(n, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Principal, MatchRoom::class.java)
                            intent.putExtra("numPlayers", n)
                            intent.putExtra("numBots", 0)
                            intent.putExtra("session", response.body()?.token)
                            intent.putExtra("public", true)
                            startActivityForResult(intent, normalCode)
                        }
                    }
                })
        }
        numP.show()
    }

    private fun joinWithCode() {
        val code = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
        code.setView(customLayout)
        code.setTitle(getString(R.string.code))
        code.setPositiveButton(getString(R.string.join_button)) { _: DialogInterface, _: Int ->
            RetrofitClient.instance.joinPrivate(JoinPrivateRequest(customLayout.inputCode.text.toString().trim(), session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent(this@Principal, MatchRoom::class.java)
                            intent.putExtra("session", response.body()?.token)
                            intent.putExtra("public", false)
                            startActivityForResult(intent, normalCode)
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.cant_join), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        code.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
        code.show()
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
                }
            })
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && requestCode == normalCode) {
            session = data!!.getStringExtra("session").toString()
            if (data.getBooleanExtra("logout", false)) {
                finish()
            }
            updateMoney()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
