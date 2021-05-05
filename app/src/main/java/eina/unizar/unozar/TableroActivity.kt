package eina.unizar.unozar

import adapter.CardAdapter
import adapter.GamerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import data.Card
import data.Gamer
import kotlinx.android.synthetic.main.activity_tablero.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.TokenRequest
import server.response.TokenResponse

var recordCambiado = 0
var nombreRecordado = ""
var record = 0

class TableroActivity : AppCompatActivity(){

    private lateinit var session: String
    private val cardsList = listOf(
        Card(1, "inicio", R.drawable.inicio),
        Card(1, "cambio_color_base", R.drawable.cambio_color_base),
        Card(1, "dos_rojo", R.drawable.dos_rojo),
        Card(1, "Carta negado amarillo", R.drawable.dos_verde),
        Card(1, "mas_cuatro_base", R.drawable.mas_cuatro_base),
        Card(1, "ocho_azul", R.drawable.ocho_azul),
        Card(1, "cinco_azul", R.drawable.cinco_azul),
        Card(1, "dos_azul", R.drawable.dos_azul),
        Card(1, "fin", R.drawable.fin)
    )

    private val gamersList = listOf(
        Gamer(1, R.drawable.jesica, "Nombre1", "su turno", "13  Cartas"),
        Gamer(2, R.drawable.castor, "Nombre2", "", "4 Cartas"),
        Gamer(3, R.drawable.larry, "Nombre3", "", "6 Cartas")
    )

    fun traductorCartasToInt(carta: String): Int {
        if(carta[1] == 'R') {
            if(carta[2] == 'X') {
                if(carta[0] == '0'){ return R.drawable.cero_rojo}
                if(carta[0] == '1'){ return R.drawable.uno_rojo}
                if(carta[0] == '2'){ return R.drawable.dos_rojo}
                if(carta[0] == '3'){ return R.drawable.tres_rojo}
                if(carta[0] == '4'){ return R.drawable.cuatro_rojo}
                if(carta[0] == '5'){ return R.drawable.cinco_rojo}
                if(carta[0] == '6'){ return R.drawable.seis_rojo}
                if(carta[0] == '7'){ return R.drawable.siete_rojo}
                if(carta[0] == '8'){ return R.drawable.ocho_rojo}
                if(carta[0] == '9'){ return R.drawable.nueve_rojo}
            }
            else if(carta[2] == 'S'){return R.drawable.saltar_turno_rojo}
            else if(carta[2] == '2') {return R.drawable.mas_dos_rojo}
            else if(carta[2] == 'R') {return R.drawable.cambio_color_rojo}
            else if(carta[2] == '4') {return R.drawable.mas_cuatro_rojo}
            else{return 0}
        }
        else if(carta[1] == 'Y') {
            if(carta[2] == 'X') {
                if(carta[0] == '0'){ return R.drawable.cero_amarillo}
                if(carta[0] == '1'){ return R.drawable.uno_amarillo}
                if(carta[0] == '2'){ return R.drawable.dos_amarillo}
                if(carta[0] == '3'){ return R.drawable.tres_amarillo}
                if(carta[0] == '4'){ return R.drawable.cuatro_amarillo}
                if(carta[0] == '5'){ return R.drawable.cinco_amarillo}
                if(carta[0] == '6'){ return R.drawable.seis_amarillo}
                if(carta[0] == '7'){ return R.drawable.siete_amarillo}
                if(carta[0] == '8'){ return R.drawable.ocho_amarillo}
                if(carta[0] == '9'){ return R.drawable.nueve_amarillo}
            }
            else if(carta[2] == 'S'){return R.drawable.saltar_turno_amarillo}
            else if(carta[2] == '2') {return R.drawable.mas_dos_amarillo}
            else if(carta[2] == 'R') {return R.drawable.cambio_color_amarillo}
            else if(carta[2] == '4') {return R.drawable.mas_cuatro_amarillo}
            else{return 0}
        }
        else if(carta[1] == 'B') {
                if(carta[2] == 'X') {
                    if(carta[0] == '0'){ return R.drawable.cero_azul}
                    if(carta[0] == '1'){ return R.drawable.uno_azul}
                    if(carta[0] == '2'){ return R.drawable.dos_azul}
                    if(carta[0] == '3'){ return R.drawable.tres_azul}
                    if(carta[0] == '4'){ return R.drawable.cuatro_azul}
                    if(carta[0] == '5'){ return R.drawable.cinco_azul}
                    if(carta[0] == '6'){ return R.drawable.seis_azul}
                    if(carta[0] == '7'){ return R.drawable.siete_azul}
                    if(carta[0] == '8'){ return R.drawable.ocho_azul}
                    if(carta[0] == '9'){ return R.drawable.nueve_azul}
                }
                else if(carta[2] == 'S'){return R.drawable.saltar_turno_azul}
                else if(carta[2] == '2') {return R.drawable.mas_dos_azul}
                else if(carta[2] == 'R') {return R.drawable.cambio_color_azul}
                else if(carta[2] == '4') {return R.drawable.mas_cuatro_azul}
                else{return 0}
            }
        else if(carta[1] == 'G') {
                if(carta[2] == 'X') {
                    if(carta[0] == '0'){ return R.drawable.cero_verde}
                    if(carta[0] == '1'){ return R.drawable.uno_verde}
                    if(carta[0] == '2'){ return R.drawable.dos_verde}
                    if(carta[0] == '3'){ return R.drawable.tres_verde}
                    if(carta[0] == '4'){ return R.drawable.cuatro_verde}
                    if(carta[0] == '5'){ return R.drawable.cinco_verde}
                    if(carta[0] == '6'){ return R.drawable.seis_verde}
                    if(carta[0] == '7'){ return R.drawable.siete_verde}
                    if(carta[0] == '8'){ return R.drawable.ocho_verde}
                    if(carta[0] == '9'){ return R.drawable.nueve_verde}
                }
                else if(carta[2] == 'S'){return R.drawable.saltar_turno_verde}
                else if(carta[2] == '2') {return R.drawable.mas_dos_verde}
                else if(carta[2] == 'R') {return R.drawable.cambio_color_verde}
                else if(carta[2] == '4') {return R.drawable.mas_cuatro_verde}
                else{return 0}
        }
        else if((carta[0] == 'X') && (carta[1] == 'X')) {
            return when {
                carta[2] == 'C' -> { R.drawable.cambio_color_base }
                carta[2] == '4' -> { R.drawable.mas_cuatro_base }
                else -> { 0 }
            }
        }
        return 0
    }

    /*fun traductorCartasToString(carta: Int): String {
        if(carta == R.drawable.cero_rojo){return "0RX"}
        if(carta == R.drawable.uno_rojo){return "1RX"}
        if(carta == R.drawable.dos_rojo){return "2RX"}
        if(carta == R.drawable.tres_rojo){return "3RX"}
        if(carta == R.drawable.cuatro_rojo){return "4RX"}
        if(carta == R.drawable.cinco_rojo){return "5RX"}
        if(carta == R.drawable.seis_rojo){return "6RX"}
        if(carta == R.drawable.siete_rojo){return "7RX"}
        if(carta == R.drawable.ocho_rojo){return "8RX"}
        if(carta == R.drawable.nueve_rojo){return "9RX"}
        if(carta == R.drawable.saltar_turno_rojo){return "XRS"}
        if(carta == R.drawable.mas_dos_rojo){return "XR2"}
        if(carta == R.drawable.cambio_color_rojo){return "XRR"}
        if(carta == R.drawable.mas_cuatro_rojo){return "XR4"}

        if(carta == R.drawable.cero_amarillo){return "0AX"}
        if(carta == R.drawable.uno_amarillo){return "1AX"}
        if(carta == R.drawable.dos_amarillo){return "2AX"}
        if(carta == R.drawable.tres_amarillo){return "3AX"}
        if(carta == R.drawable.cuatro_amarillo){return "4AX"}
        if(carta == R.drawable.cinco_amarillo){return "5AX"}
        if(carta == R.drawable.seis_amarillo){return "6AX"}
        if(carta == R.drawable.siete_amarillo){return "7AX"}
        if(carta == R.drawable.ocho_amarillo){return "8AX"}
        if(carta == R.drawable.nueve_amarillo){return "9AX"}
        if(carta == R.drawable.saltar_turno_amarillo){return "XAS"}
        if(carta == R.drawable.mas_dos_amarillo){return "XA2"}
        if(carta == R.drawable.cambio_color_amarillo){return "XAR"}
        if(carta == R.drawable.mas_cuatro_amarillo){return "XA4"}

        if(carta == R.drawable.cero_azul){return "0BX"}
        if(carta == R.drawable.uno_azul){return "1BX"}
        if(carta == R.drawable.dos_azul){return "2BX"}
        if(carta == R.drawable.tres_azul){return "3BX"}
        if(carta == R.drawable.cuatro_azul){return "4BX"}
        if(carta == R.drawable.cinco_azul){return "5BX"}
        if(carta == R.drawable.seis_azul){return "6BX"}
        if(carta == R.drawable.siete_azul){return "7BX"}
        if(carta == R.drawable.ocho_azul){return "8BX"}
        if(carta == R.drawable.nueve_azul){return "9BX"}
        if(carta == R.drawable.saltar_turno_azul){return "XBS"}
        if(carta == R.drawable.mas_dos_azul){return "XB2"}
        if(carta == R.drawable.cambio_color_azul){return "XBR"}
        if(carta == R.drawable.mas_cuatro_azul){return "XB4"}

        if(carta == R.drawable.cero_verde){return "0GX"}
        if(carta == R.drawable.uno_verde){return "1GX"}
        if(carta == R.drawable.dos_verde){return "2GX"}
        if(carta == R.drawable.tres_verde){return "3GX"}
        if(carta == R.drawable.cuatro_verde){return "4GX"}
        if(carta == R.drawable.cinco_verde){return "5GX"}
        if(carta == R.drawable.seis_verde){return "6GX"}
        if(carta == R.drawable.siete_verde){return "7GX"}
        if(carta == R.drawable.ocho_verde){return "8GX"}
        if(carta == R.drawable.nueve_verde){return "9GX"}
        if(carta == R.drawable.saltar_turno_verde){return "XGS"}
        if(carta == R.drawable.mas_dos_verde){return "XG2"}
        if(carta == R.drawable.cambio_color_verde){return "XGR"}
        if(carta == R.drawable.mas_cuatro_verde){return "XG4"}

        if(carta == R.drawable.cambio_color_base){return "XXR"}
        if(carta == R.drawable.mas_cuatro_base){return "XX4"}
        return ""
    }*/

    private val cards = mutableListOf<Card>()
    val listaJugadores = mutableListOf<Gamer>()

    private var cimaActual = R.drawable.cambio_sentido_azul
    private var cimaCambiada = 1
    /*var cimaNueva = 0

    val manoActual = mutableListOf<String>()
    var manoCambiada = 0
    val manoNueva = mutableListOf<String>()

    val jugadoresActuales = mutableListOf<String>()
    var jugadoresCambiados = 0
    val jugadoresNuevos = mutableListOf<String>()

    val numCartasJugadoresActuales  = mutableListOf<Int>()
    var numCartasJugadoresCambiados = 0
    val numCartasJugadoresNuevos  = mutableListOf<Int>()

    var miTurno = 0

    //var record = 0
    var selectedCard = 0
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tablero)
        session = intent.getStringExtra("session").toString()
        //Picasso.get().load(R.drawable.cero_verde).into(R.id.image_record)

        /*val imageView1 = findViewById<ImageView>(R.id.image_cima)
        imageView1.setImageResource(cima)*/
        /*val imageView2 = findViewById<ImageView>(R.id.image_record)
        imageView2.setImageResource(record)*/
        actualizarJuego()
        actualizar()
        //cambiarCima()
        //val recicleView = findViewById<View>(R.id.rvCard) as Button
        //val recordButton = findViewById<View>(R.id.recordarButton) as Button
        recordarButton.setOnClickListener {
            //val imageView = findViewById<ImageView>(R.id.image_record)
            image_record.setImageResource(record)
        }
        /*val putButton = findViewById<View>(R.id.buttonPoner) as Button
        val pedirUnoButton = findViewById<View>(R.id.buttonPedirUno) as Button
        val robarButton = findViewById<View>(R.id.buttonRobarCarta) as Button
        val pasarButton = findViewById<View>(R.id.buttonPasar) as Button
        putButton.setOnClickListener {
            ponerCarta()
        }
        pedirUnoButton.setOnClickListener{
            pedirUno()
        }
        robarButton.setOnClickListener{
            robarCarta()
        }
        pasarButton.setOnClickListener{
            pasarTurno()
        }*/
    }

    fun pedirUno(@Suppress("UNUSED_PARAMETER")view: View){
        //Pedir uno al servidor
        /*RetrofitClient.instance.userPlayCard(PutCardRequest(/*Que tengo que enviar*/))
            .enqueue(object : Callback<PutCardResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200) {
                        //Se ha pedido uno con éxito
                    } else {
                        Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                    }
                }
            })*/
    }

    fun robarCarta(@Suppress("UNUSED_PARAMETER")view: View){
        //Pedir robar carta al servidor
        /*RetrofitClient.instance.userPlayCard(PutCardRequest(/*Que tengo que enviar*/))
            .enqueue(object : Callback<PutCardResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200) {
                        //La carta se ha puesto con éxito
                    } else {
                        Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                    }
                }
            })*/
    }

    fun pasarTurno(@Suppress("UNUSED_PARAMETER")view: View){
        //Pedir pasarTurno al servidor
        /*RetrofitClient.instance.userPlayCard(PutCardRequest(/*Que tengo que enviar*/))
            .enqueue(object : Callback<PutCardResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200) {
                        //Se ha pasado el tuno con éxito
                    } else {
                        Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                    }
                }
            })*/
    }

    fun ponerCarta(@Suppress("UNUSED_PARAMETER")view: View){
        //Si es una +4 o un cambia color
        if(nombreRecordado == "mas_cuatro_base" || nombreRecordado == "cambio_color_base") {
            val builder = AlertDialog.Builder(this)
            val items = arrayOf("Red", "Green", "Yellow", "Blue")
            with(builder)
            {
                setTitle("Elija un color")
                setItems(items) { _, which ->
                    //Poner carta
                    Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                }
                show()
            }
        }
        //Mandar carta al servidor
        /*RetrofitClient.instance.userPlayCard(PutCardRequest(/*Que tengo que enviar*/))
            .enqueue(object : Callback<PutCardResponse> {
                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.code() == 200) {
                        //La carta se ha puesto con éxito
                    } else {
                        Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                    }
                }
            })*/
    }


    fun initRecycler(){
        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CardAdapter(cards)
        rvCard.adapter = adapter
    }

    private fun actualizarJuego(){
        anyadirCartas()
        anyadirGamers()
    }

    var listaCartas = ArrayList<Card>()

    private fun anyadirCartas(){
        //Cards.removeAll(Cards)
        var nCartas = 1
        //Realizar consulta de las cartas al servidor
        var i = 0
        cards.add(Card(1, "inicio", R.drawable.inicio))
        for(i in 1..nCartas) {
            cards.add(Card(1, "Carta +4", R.drawable.cuatro_verde))
        }
        cards.add(Card(1, "fin", R.drawable.fin))

        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CardAdapter(cardsList)
        rvCard.adapter = adapter
    }

    private fun anyadirGamers(){
        //GamersList.removeAll(GamersList)
        var nGamers = 1
        //Realizar consulta de los otros juegadores al servidor
        var i = 0
        /*for(i in 1..nGamers) {
            Gamers.add(Gamer(1, "Carta +4", R.drawable.cuatro_verde))
        }*/
        rvGamer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = GamerAdapter(gamersList)
        rvGamer.adapter = adapter
    }

    private fun cambiarCima(){
        val imageView1 = findViewById<ImageView>(R.id.image_cima)
        imageView1.setImageResource(cimaActual)
    }

    private fun cambiarElegido(){
        /*val imageView2 = findViewById<ImageView>(R.id.image_record)
        Picasso.get().load(record).into(imageView2)*/
        //imageView2.setImageResource(record)
        recordCambiado = 0
    }
    //var recordCambiado = 0;
    private fun actualizar(){
        CoroutineScope(Dispatchers.IO).launch {
            while(true){
                /*RetrofitClient.instance.userPlayCard(PutCardRequest(/*Que tengo que enviar*/))
                    .enqueue(object : Callback<PutCardResponse> {
                        override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                            if (response.code() == 200) {
                                //La carta se ha puesto con éxito
                            } else {
                                Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                            }
                        }
                    })*/
                if(cimaCambiada == 1){
                    cambiarCima()
                    cimaCambiada = 0
                }
                if(recordCambiado == 1){
                    cambiarElegido()
                }

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
                RetrofitClient.instance.quitMatch(TokenRequest(session))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                Toast.makeText(applicationContext, "Ha salido de la partida", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
