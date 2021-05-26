package eina.unizar.unozar

import android.app.Activity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_friend_profile.*

class FriendProfile : AppCompatActivity() {

    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friend_profile)
        friend_alias.setText(intent.getStringExtra("alias"), TextView.BufferType.EDITABLE)
        friend_email.text = intent.getStringExtra("email")
        friend_total_played.text = intent.getStringExtra("total_matches")
        friend_total_wins.text = intent.getStringExtra("total_wins")
        friend_played.text = intent.getStringExtra("friend_matches")
        friend_wins.text = intent.getStringExtra("friend_wins")
        friend_avatar.setImageResource(avatars[intent.getIntExtra("avatar", 0)])
        return_button.setOnClickListener {
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        setResult(Activity.RESULT_OK, intent)
    }
}
