package eina.unizar.unozar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar

class Principal : AppCompatActivity() {

    private val TWO_PLAYERS = 2
    private val THREE_PLAYERS = 3
    private val FOUR_PLAYERS = 4

    var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        session = intent.getStringExtra("session").toString()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here.
        when (item.getItemId()) {
            R.id.action_profile -> {
                val intent = Intent(this, Profile::class.java)
                intent.putExtra("session", session)
                startActivity(intent)
            }
            R.id.action_logout -> {
                val intent = Intent(this, Login::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)

    }

    fun createTwoPlayersGame(view: View) {
        val intent = Intent(this, CreateGame::class.java)
        intent.putExtra("numPlayers", TWO_PLAYERS)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    fun createThreePlayersGame(view: View) {
        val intent = Intent(this, CreateGame::class.java)
        intent.putExtra("numPlayers", THREE_PLAYERS)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    fun createFourPlayersGame(view: View) {
        val intent = Intent(this, CreateGame::class.java)
        intent.putExtra("numPlayers", FOUR_PLAYERS)
        intent.putExtra("session", session)
        startActivity(intent)
    }

}
