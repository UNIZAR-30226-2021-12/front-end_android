package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_principal.*
import kotlinx.android.synthetic.main.activity_tienda.*
import kotlinx.android.synthetic.main.activity_tienda.money
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.IdRequest
import server.request.UnlockRequest
import server.response.PlayerInfo
import server.response.TokenResponse

class Tienda : AppCompatActivity() {
    var dinero = 20
    val costeAvatares = 250
    val costeTableros = 250
    val costeAvatar = 250
    private lateinit var session: String
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.castor,
        R.drawable.flippy,
        R.drawable.jesica,
        R.drawable.larry,
        R.drawable.oso,
        R.drawable.slendid
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)
        session = intent.getStringExtra("session").toString()
        dinero = intent.getIntExtra("money", 0)
        money.text = dinero.toString()
        comprar1.setOnClickListener {
            comprar(0,100)
        }
        comprar2.setOnClickListener {
            comprar(7,costeAvatares)
        }
        exit.setOnClickListener() {
            finish();
        }
    }

    private fun comprar(id: Int, coste: Int) {
        if(dinero >= coste){
            RetrofitClient.instance.unlockAvatar(UnlockRequest(id, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                            money.text = (dinero - coste).toString()
                            setResult(Activity.RESULT_OK, intent)
                            Toast.makeText(applicationContext, getString(R.string.avatar_change_success), Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_update_response) + response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        else{
            Toast.makeText(applicationContext, "No tiene suficiente dinero", Toast.LENGTH_LONG).show()
        }
    }
}
