package eina.unizar.unozar

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_tienda.*
import kotlinx.android.synthetic.main.activity_tienda.money
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.UnlockRequest
import server.response.TokenResponse

class Tienda : AppCompatActivity() {
    var myMoney = 20
    val avatarPrice = 250
    val boardPrice = 500
    val cardPrice = 750
    private lateinit var session: String
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )
    private var boards = arrayListOf(
        R.drawable.empty,
        R.drawable.tablero2,
        R.drawable.tablero3
    )
    private var cards = arrayListOf(
        R.drawable.reverso1,
        R.drawable.reverso2,
        R.drawable.reverso3,
        R.drawable.reverso4,
        R.drawable.reverso5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)
        session = intent.getStringExtra("session").toString()
        myMoney = intent.getIntExtra("money", 0)
        money.text = myMoney.toString()
        comprar1.setOnClickListener {
            purchaseAvatar(2)
        }
        comprar2.setOnClickListener {
            purchaseAvatar(3)
        }
        comprar3.setOnClickListener {
            purchaseAvatar(4)
        }
        comprar4.setOnClickListener {
            purchaseBoard(1)
        }
        comprar5.setOnClickListener {
            purchaseBoard(2)
        }
        comprar6.setOnClickListener {
            purchaseCard(1)
        }
        comprar7.setOnClickListener {
            purchaseCard(2)
        }
        comprar8.setOnClickListener {
            purchaseCard(3)
        }
        comprar9.setOnClickListener {
            purchaseCard(4)
        }
        exit.setOnClickListener {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

    private fun purchaseAvatar(id: Int) {
        if(myMoney >= avatarPrice){
            RetrofitClient.instance.unlockAvatar(UnlockRequest(id, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                            money.text = (myMoney - avatarPrice).toString()
                            setResult(Activity.RESULT_OK, intent)
                            Toast.makeText(applicationContext, getString(R.string.avatar_change_success), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        else{
            Toast.makeText(applicationContext, getString(R.string.not_enough_money), Toast.LENGTH_LONG).show()
        }
    }

    private fun purchaseBoard(id: Int) {
        if(myMoney >= boardPrice){
            RetrofitClient.instance.unlockBoard(UnlockRequest(id, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                            money.text = (myMoney - boardPrice).toString()
                            setResult(Activity.RESULT_OK, intent)
                            Toast.makeText(applicationContext, getString(R.string.avatar_change_success), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        else{
            Toast.makeText(applicationContext, getString(R.string.not_enough_money), Toast.LENGTH_LONG).show()
        }
    }

    private fun purchaseCard(id: Int) {
        if(myMoney >= cardPrice){
            RetrofitClient.instance.unlockCard(UnlockRequest(id, session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                            money.text = (myMoney - cardPrice).toString()
                            setResult(Activity.RESULT_OK, intent)
                            Toast.makeText(applicationContext, getString(R.string.avatar_change_success), Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_update_response), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        else{
            Toast.makeText(applicationContext, getString(R.string.not_enough_money), Toast.LENGTH_LONG).show()
        }
    }

    override fun onBackPressed() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
