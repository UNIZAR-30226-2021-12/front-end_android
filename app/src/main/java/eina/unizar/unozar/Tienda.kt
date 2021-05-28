package eina.unizar.unozar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
    var myMoney = 20
    val avatarPrice = 250
    val boardPrice = 500
    val cardPrice = 750
    private lateinit var session: String
    private lateinit var btn: ArrayList<Button>
    private lateinit var txt: ArrayList<TextView>
    private var purchasing = false
    private val items = arrayListOf(2, 3, 4, 1, 2, 1, 2, 3, 4)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)
        session = intent.getStringExtra("session").toString()
        myMoney = intent.getIntExtra("money", 0)
        money.text = myMoney.toString()
        exit.setOnClickListener {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
        btn = ArrayList()
        btn.add(comprar1)
        btn.add(comprar2)
        btn.add(comprar3)
        btn.add(comprar4)
        btn.add(comprar5)
        btn.add(comprar6)
        btn.add(comprar7)
        btn.add(comprar8)
        btn.add(comprar9)
        txt = ArrayList()
        txt.add(compra_text1)
        txt.add(compra_text2)
        txt.add(compra_text3)
        txt.add(compra_text4)
        txt.add(compra_text5)
        txt.add(compra_text6)
        txt.add(compra_text7)
        txt.add(compra_text8)
        txt.add(compra_text9)
        RetrofitClient.instance.readPlayer(IdRequest(session.substring(0, 32)))
            .enqueue(object : Callback<PlayerInfo> {
                override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                    if (response.code() == 200) {
                        for (i in response.body()!!.unlockedAvatars.indices) {
                            when (response.body()!!.unlockedAvatars[i]) {
                                2 -> {
                                    comprar1.visibility = GONE
                                    compra_text1.text = getString(R.string.already_purchased)
                                }
                                3 -> {
                                    comprar2.visibility = GONE
                                    compra_text2.text = getString(R.string.already_purchased)
                                }
                                4 -> {
                                    comprar3.visibility = GONE
                                    compra_text3.text = getString(R.string.already_purchased)
                                }
                            }
                        }
                        for (i in response.body()!!.unlockedBoards.indices) {
                            if (response.body()!!.unlockedBoards[i] == 1) {
                                comprar4.visibility = GONE
                                compra_text4.text = getString(R.string.already_purchased)
                            } else if (response.body()!!.unlockedBoards[i] == 2) {
                                comprar5.visibility = GONE
                                compra_text5.text = getString(R.string.already_purchased)
                            }
                        }
                        for (i in response.body()!!.unlockedCards.indices) {
                            when (response.body()!!.unlockedCards[i]) {
                                1 -> {
                                    comprar6.visibility = GONE
                                    compra_text6.text = getString(R.string.already_purchased)
                                }
                                2 -> {
                                    comprar7.visibility = GONE
                                    compra_text7.text = getString(R.string.already_purchased)
                                }
                                3 -> {
                                    comprar8.visibility = GONE
                                    compra_text8.text = getString(R.string.already_purchased)
                                }
                                4 -> {
                                    comprar9.visibility = GONE
                                    compra_text9.text = getString(R.string.already_purchased)
                                }
                            }
                        }
                    }
                }
            })
        for(i in items.indices) {
            when {
                i < 3 -> {
                    btn[i].setOnClickListener {
                        if(!purchasing) {
                            purchasing = true
                            purchaseAvatar(i)
                        } else
                            Toast.makeText(applicationContext, getString(R.string.purchase_in_progress), Toast.LENGTH_LONG).show()
                    }
                }
                i < 5 -> {
                    btn[i].setOnClickListener {
                        if(!purchasing) {
                            purchasing = true
                            purchaseBoard(i)
                        } else
                            Toast.makeText(applicationContext, getString(R.string.purchase_in_progress), Toast.LENGTH_LONG).show()
                    }
                }
                else -> {
                    btn[i].setOnClickListener {
                        if(!purchasing) {
                            purchasing = true
                            purchaseCard(i)
                        } else
                            Toast.makeText(applicationContext, getString(R.string.purchase_in_progress), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun purchaseAvatar(i: Int) {
        if(myMoney >= avatarPrice){
            RetrofitClient.instance.unlockAvatar(UnlockRequest(items[i], session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()!!.token
                            purchasing = false
                            btn[i].visibility = GONE
                            txt[i].text = getString(R.string.already_purchased)
                            money.text = (myMoney - avatarPrice).toString()
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

    private fun purchaseBoard(i: Int) {
        if(myMoney >= boardPrice){
            RetrofitClient.instance.unlockBoard(UnlockRequest(items[i], session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()!!.token
                            purchasing = false
                            btn[i].visibility = GONE
                            txt[i].text = getString(R.string.already_purchased)
                            money.text = (myMoney - boardPrice).toString()
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

    private fun purchaseCard(i: Int) {
        if(myMoney >= cardPrice){
            RetrofitClient.instance.unlockCard(UnlockRequest(items[i], session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()!!.token
                            purchasing = false
                            btn[i].visibility = GONE
                            txt[i].text = getString(R.string.already_purchased)
                            money.text = (myMoney - cardPrice).toString()
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
