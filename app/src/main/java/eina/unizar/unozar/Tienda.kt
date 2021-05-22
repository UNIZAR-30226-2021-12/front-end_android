package eina.unizar.unozar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_tienda.*

class Tienda : AppCompatActivity() {
    var dinero = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda)
        //updateInfo()
        comprarFlippy.setOnClickListener {
            comprar(0,500)
        }
        comprarSlendid.setOnClickListener {
            comprar(1,500)
        }
        exit.setOnClickListener() {
            finish();
        }
    }

    private fun comprar(id: Int, coste: Int) {
        if(dinero > coste){
            //Mandar peticion para comprar
        }
        else{
            Toast.makeText(applicationContext, "No tiene suficiente dinero", Toast.LENGTH_LONG).show()
        }
    }

    /*private fun updateInfo() {
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
    }*/
}