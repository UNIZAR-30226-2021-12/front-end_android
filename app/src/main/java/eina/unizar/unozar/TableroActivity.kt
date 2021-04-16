package eina.unizar.unozar

import adapter.CardAdapter
import adapter.GamerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import data.Card
import data.Gamer
import eina.unizar.unozar.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_tablero.*
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

var recordCambiado = 0
var record = 0;

class TableroActivity : AppCompatActivity(){

    val CardsList = listOf<Card>(
            Card(1, "inicio", R.drawable.inicio),
            Card(1, "Carta amarillo 0", R.drawable.cambio_color_azul),
            Card(1, "Carta rojo 1", R.drawable.dos_rojo),
            Card(1, "Carta negado amarillo", R.drawable.dos_verde),
            Card(1, "Carta +2 verde", R.drawable.mas_cuatro_azul),
            Card(1, "Carta +4", R.drawable.ocho_azul),
            Card(1, "Carta verde +2", R.drawable.cinco_azul),
            Card(1, "Carta +4", R.drawable.dos_azul),
            Card(1, "fin", R.drawable.fin)
    )

    val GamersList = listOf<Gamer>(
            Gamer(1, R.drawable.jesica, "Nombre1", "su turno", "13  Cartas"),
            Gamer(2, R.drawable.castor, "Nombre2", "", "4 Cartas"),
            Gamer(3, R.drawable.larry, "Nombre3", "", "6 Cartas")
    )

    val Cards = mutableListOf<Card>()
    val Gamers = mutableListOf<Gamer>()
    var cima = R.drawable.cambio_sentido_azul
    //var record = 0
    public var selectedCard = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tablero)
        //Picasso.get().load(R.drawable.cero_verde).into(R.id.image_record)

        /*val imageView1 = findViewById<ImageView>(R.id.image_cima)
        imageView1.setImageResource(cima)*/
        /*val imageView2 = findViewById<ImageView>(R.id.image_record)
        imageView2.setImageResource(record)*/
        actualizarJuego()
        actualizar()
        //cambiarCima()
        /*val recicleView = findViewById<View>(R.id.rvCard) as Button*/
        val loginButton = findViewById<View>(R.id.recordarButton) as Button
        loginButton.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.image_record)
            imageView.setImageResource(record)
        }
        /*recicleView.setOnClickListener {
            val imageView = findViewById<ImageView>(R.id.image_record)
            imageView.setImageResource(record)
        }*/
        //initRecycler()
    }

    fun initRecycler(){
        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CardAdapter(Cards)
        rvCard.adapter = adapter
    }

    fun actualizarJuego(){
        anyadirCartas()
        anyadirGamers()
    }

    var listaCartas = ArrayList<Card>()

    fun anyadirCartas(){
        //Cards.removeAll(Cards)
        var nCartas = 1
        //Realizar consulta de las cartas al servidor
        var i = 0
        Cards.add(Card(1, "inicio", R.drawable.inicio))
        for(i in 1..nCartas) {
            Cards.add(Card(1, "Carta +4", R.drawable.cuatro_verde))
        }
        Cards.add(Card(1, "fin", R.drawable.fin))

        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CardAdapter(CardsList)
        rvCard.adapter = adapter
    }

    fun anyadirGamers(){
        //GamersList.removeAll(GamersList)
        var nGamers = 1
        //Realizar consulta de los otros juegadores al servidor
        var i = 0
        /*for(i in 1..nGamers) {
            Gamers.add(Gamer(1, "Carta +4", R.drawable.cuatro_verde))
        }*/
        rvGamer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = GamerAdapter(GamersList)
        rvGamer.adapter = adapter
    }

    suspend fun cambiarCima(){
        val imageView1 = findViewById<ImageView>(R.id.image_cima)
        imageView1.setImageResource(cima)
        /*val imageView2 = findViewById<ImageView>(R.id.image_record)
        imageView2.setImageResource(record)*/
    }

    suspend fun cambiarElegido(){
        /*val imageView2 = findViewById<ImageView>(R.id.image_record)
        Picasso.get().load(record).into(imageView2)*/
        //imageView2.setImageResource(record)
        recordCambiado = 0
    }
    var cimaCambiada = 1
    //var recordCambiado = 0;
    private fun actualizar(){
        CoroutineScope(Dispatchers.IO).launch {
            while(true){
                if(cimaCambiada == 1){
                    cambiarCima()
                    cimaCambiada = 0;
                }
                if(recordCambiado == 1){
                    cambiarElegido()
                }
                //cambiarElegido()
                //selectedCard += 1
                /*runOnUiThread {
                 imageView1.setImageResource(cima)
                }*/
                delay(200)
            }

        }
    }

    private val PAUSE_ID = Menu.FIRST
    private val EXIT_ID = Menu.FIRST + 1

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val result = super.onCreateOptionsMenu(menu)
        menu.add(Menu.NONE, PAUSE_ID, Menu.NONE, "Pausar Partida")
        menu.add(Menu.NONE, EXIT_ID, Menu.NONE, "Salir de Partida")
        return result
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            PAUSE_ID -> {
                return true
            }
            EXIT_ID -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
