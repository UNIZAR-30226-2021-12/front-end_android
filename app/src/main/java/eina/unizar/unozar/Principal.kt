package eina.unizar.unozar

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Principal : AppCompatActivity() {

    private var n = 2
    private lateinit var session: String
    private lateinit var players: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        session = intent.getStringExtra("session").toString()
        val numPlayers = arrayOf("2", "3", "4")
        players = AlertDialog.Builder(this)
        players.setTitle("Número de jugadores")
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
                startActivity(intent)
            }
            R.id.action_logout -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
            R.id.action_refresh -> {
                RetrofitClient.instance.userRefreshToken(RefreshRequest(session))
                    .enqueue(object : Callback<BasicResponse> {
                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                            Toast.makeText(
                                applicationContext,
                                "Error: " + t.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {
                            if (response.code() == 200) {
                                Toast.makeText(
                                    applicationContext,
                                    "ok: " + response.code(),
                                    Toast.LENGTH_LONG
                                ).show()
                                /*val intent = Intent(this@Principal, Principal::class.java)
                                intent.putExtra("session", response.body()?.token)
                                startActivity(intent)*/
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "Error: " + response.code(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    })
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun publicMatch(@Suppress("UNUSED_PARAMETER")view: View) {
        val choose = AlertDialog.Builder(this)
        choose.setTitle("Elija")
        choose.setMessage("¿Quiere crear una nueva partida pública o unirse a una ya creada?")
        choose.setPositiveButton("crear") {_: DialogInterface, _: Int ->
            players.show()
            /*val intent = Intent(this, CreatePublicMatch::class.java)
            intent.putExtra("numPlayers", n)
            intent.putExtra("session", session)
            startActivity(intent)*/
        }
        choose.setNegativeButton("Unirse") { _: DialogInterface, _: Int ->
            players.show()
            /*val intent = Intent(this, JoinMatch::class.java)
            intent.putExtra("numPlayers", n)
            intent.putExtra("session", session)
            startActivity(intent)*/
        }
        choose.show()
    }

    fun privateMatch(@Suppress("UNUSED_PARAMETER")view: View) {
        var b = 0;
        val choose = AlertDialog.Builder(this)
        choose.setTitle("Elija")
        choose.setMessage("¿Quiere crear una nueva partida privada o unirse a una ya creada?")
        choose.setPositiveButton("crear") {_: DialogInterface, _: Int ->
            val numP = AlertDialog.Builder(this)
            val numPlayers = arrayOf("2", "3", "4")
            numP.setTitle("Número de jugadores")
            numP.setItems(numPlayers) { _: DialogInterface, i: Int ->
                n = numPlayers[i].toInt()
                val bots = AlertDialog.Builder(this)
                var numBots:Array<String>
                if (n > 2) {
                    numBots = arrayOf("0", "1", "2")
                    if (n > 3)
                        numBots = arrayOf("0", "1", "2", "3")
                } else {
                    numBots = arrayOf("0", "1")
                }
                bots.setTitle("Número de Inteligencias Artificiales")
                bots.setItems(numBots) { _: DialogInterface, i: Int ->
                    b = numBots[i].toInt()
                }
                bots.show()
            }
            numP.show()

            /*val intent = Intent(this, CreatePrivateMatch::class.java)
            intent.putExtra("numPlayers", n)
            intent.putExtra("numBots", b)
            intent.putExtra("session", session)
            startActivity(intent)*/
        }
        choose.setNegativeButton("Unirse") { _: DialogInterface, _: Int ->
            val code = AlertDialog.Builder(this)
            val customLayout: View = getLayoutInflater().inflate(R.layout.custom_alertdialog, null)
            code.setView(customLayout)
            code.setTitle("Escriba aquí el código de la partida")
            code.setPositiveButton("Unirse") { _: DialogInterface, _: Int ->
                /*val intent = Intent(this, JoinMatch::class.java)
                intent.putExtra("numPlayers", n)
                intent.putExtra("session", session)
                startActivity(intent)*/
            }
            code.show()
        }
        choose.show()
    }
}
