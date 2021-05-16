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
import kotlinx.android.synthetic.main.activity_tablero.*
import kotlinx.android.synthetic.main.custom_alertdialog.*
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.*
import server.response.GameInfoResponse
import server.response.PlayerInfo
import server.response.TokenResponse


class Principal : AppCompatActivity() {
    private var normalCode = 73
    private var n = 2
    private lateinit var session: String
    private lateinit var players: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        session = intent.getStringExtra("session").toString()
        updateInfo()
        val numPlayers = arrayOf("2", "3", "4")
        players = AlertDialog.Builder(this)
        players.setTitle(getString(R.string.number_of_players))
        players.setItems(numPlayers) { _: DialogInterface, i: Int ->
            n = numPlayers[i].toInt()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_profile -> {
                val intent = Intent(this, Profile::class.java)
                intent.putExtra("session", session)
                startActivityForResult(intent, normalCode)
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
            R.id.action_refresh -> {
                RetrofitClient.instance.refreshToken(TokenRequest(session))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                session = response.body()?.token.toString()
                                Toast.makeText(applicationContext, getString(R.string.refresh_success), Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_refresh_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
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
            R.id.action_draw -> {
                RetrofitClient.instance.readGame(TokenRequest(session))
                    .enqueue(object : Callback<GameInfoResponse> {
                        override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.no_response),
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<GameInfoResponse>,
                            response: Response<GameInfoResponse>
                        ) {
                            if (response.code() == 200) {
                                session = response.body()?.token.toString()
                            } else Toast.makeText(
                                applicationContext,
                                response.code(),
                                Toast.LENGTH_LONG
                            ).show()
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
            RetrofitClient.instance.createMatch(CreateMatchRequest(false, numPlayers[i].toInt(), 0, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                            val intent = Intent(this@Principal, CreatePublicMatch::class.java)
                            intent.putExtra("numPlayers", n)
                            intent.putExtra("numBots", 0)
                            intent.putExtra("session", response.body()?.token)
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
                                    val intent = Intent(this@Principal, CreatePrivateMatch::class.java)
                                    intent.putExtra("numPlayers", n)
                                    intent.putExtra("numBots", b)
                                    intent.putExtra("session", response.body()?.token)
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
                                val intent = Intent(this@Principal, CreatePrivateMatch::class.java)
                                intent.putExtra("session", response.body()?.token)
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

    private fun updateInfo() {
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) { money.text = response.body()?.money.toString() }
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
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
