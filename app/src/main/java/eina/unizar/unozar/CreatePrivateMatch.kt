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
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.TokenRequest
import server.response.GameInfoResponse
import server.response.TokenResponse

class CreatePrivateMatch : AppCompatActivity() {
    private var CODE = 73
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
        actualizar()
        /*RetrofitClient.instance.readGame(TokenRequest(session))
            .enqueue(object : Callback<GameInfoResponse> {
                override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                    val code = AlertDialog.Builder(this@CreatePrivateMatch)
                    code.setTitle("Error")
                    code.setMessage(t.message)
                    code.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int -> }
                    code.show()
                } override fun onResponse(call: Call<GameInfoResponse>, response: Response<GameInfoResponse>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Sala creada", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })*/
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
        RetrofitClient.instance.quitMatch(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
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
    }

    fun start(@Suppress("UNUSED_PARAMETER") view: View) {
        RetrofitClient.instance.startMatch(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                        val intent = Intent(this@CreatePrivateMatch, TableroActivity::class.java)
                        intent.putExtra("session", response.body()!!.token)
                        startActivityForResult(intent, CODE)
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun actualizar(){
        CoroutineScope(Dispatchers.IO).launch {
            while(true){
                RetrofitClient.instance.readGame(TokenRequest(session))
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
                    })
                delay(200)
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
