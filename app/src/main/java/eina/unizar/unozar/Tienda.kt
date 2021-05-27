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
                        var j = 1
                        var k = 1
                        var l = 1
                        for (i in btn.indices) {
                            when {
                                i < 3 -> {
                                    if (items[i] == response.body()?.unlockedAvatars?.get(j) ?: 10) {
                                        btn[i].visibility = GONE
                                        txt[i].text = getString(R.string.already_purchased)
                                        j++
                                    }
                                }
                                i < 5 -> {
                                    if (items[i] == response.body()?.unlockedBoards?.get(k) ?: 10) {
                                        btn[i].visibility = GONE
                                        txt[i].text = getString(R.string.already_purchased)
                                        k++
                                    }
                                }
                                else -> {
                                    if (items[i] == response.body()?.unlockedCards?.get(l) ?: 10) {
                                        btn[i].visibility = GONE
                                        txt[i].text = getString(R.string.already_purchased)
                                        l++
                                    }
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
                        purchaseAvatar(items[i])
                        btn[i].visibility = GONE
                        txt[i].text = getString(R.string.already_purchased)
                    }
                }
                i < 5 -> {
                    btn[i].setOnClickListener {
                        purchaseBoard(items[i])
                        btn[i].visibility = GONE
                        txt[i].text = getString(R.string.already_purchased)
                    }
                }
                else -> {
                    btn[i].setOnClickListener {
                        purchaseCard(items[i])
                        btn[i].visibility = GONE
                        txt[i].text = getString(R.string.already_purchased)
                    }
                }
            }
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
                            Toast.makeText(applicationContext, getString(R.string.avatar_purchase_response), Toast.LENGTH_LONG).show()
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
                            Toast.makeText(applicationContext, getString(R.string.board_purchase_response), Toast.LENGTH_LONG).show()
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
                            Toast.makeText(applicationContext, getString(R.string.card_purchase_response), Toast.LENGTH_LONG).show()
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
