package eina.unizar.unozar

import android.R.id
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class CreateGame : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val N = intent.getIntExtra("num_players", 0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        val toolbar = findViewById(R.id.toolbar) as Toolbar?
        setSupportActionBar(toolbar)

        if (N > 2) {
            val imageView: ImageView = findViewById(R.id.player_three)
            imageView.visibility = View.VISIBLE
        }
        if (N > 3) {
            val imageView: ImageView = findViewById(R.id.player_four)
            imageView.visibility = View.VISIBLE
        }
    }



    fun cancel(view: View) {
        finish()
    }
}
