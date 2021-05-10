package eina.unizar.unozar

import adapter.CardAdapter
import adapter.GamerAdapter
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
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
import server.request.PlayCardRequest
import server.request.TokenRequest
import server.response.GameInfoResponse
import server.response.TokenResponse
import kotlin.properties.Delegates

var posCambiado :Long = 0
var recordCambiado = false
var nombreRecordado = ""
var record = 0

class TableroActivity : AppCompatActivity() {

    internal lateinit var timer : CountDownTimer
    internal val initial: Long = 60000
    internal val interval: Long = 1000
    private lateinit var session: String
    private var turn: Int = 5
    private var finished = false

    fun traductorCartasToInt(carta: String): Int {
        if(carta[1] == 'R') {
            if(carta[2] == 'X') {
                if(carta[0] == '0') { return R.drawable.cero_rojo }
                if(carta[0] == '1') { return R.drawable.uno_rojo }
                if(carta[0] == '2') { return R.drawable.dos_rojo }
                if(carta[0] == '3') { return R.drawable.tres_rojo }
                if(carta[0] == '4') { return R.drawable.cuatro_rojo }
                if(carta[0] == '5') { return R.drawable.cinco_rojo }
                if(carta[0] == '6') { return R.drawable.seis_rojo }
                if(carta[0] == '7') { return R.drawable.siete_rojo }
                if(carta[0] == '8') { return R.drawable.ocho_rojo }
                if(carta[0] == '9') { return R.drawable.nueve_rojo }
            }
            else if(carta[2] == 'S') { return R.drawable.saltar_turno_rojo }
            else if(carta[2] == '2') { return R.drawable.mas_dos_rojo }
            else if(carta[2] == 'C') { return R.drawable.cambio_color_rojo }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_rojo }
            else { return 0 }
        }
        else if(carta[1] == 'Y') {
            if(carta[2] == 'X') {
                if(carta[0] == '0') { return R.drawable.cero_amarillo }
                if(carta[0] == '1') { return R.drawable.uno_amarillo }
                if(carta[0] == '2') { return R.drawable.dos_amarillo }
                if(carta[0] == '3') { return R.drawable.tres_amarillo }
                if(carta[0] == '4') { return R.drawable.cuatro_amarillo }
                if(carta[0] == '5') { return R.drawable.cinco_amarillo }
                if(carta[0] == '6') { return R.drawable.seis_amarillo }
                if(carta[0] == '7') { return R.drawable.siete_amarillo }
                if(carta[0] == '8') { return R.drawable.ocho_amarillo }
                if(carta[0] == '9') { return R.drawable.nueve_amarillo }
            }
            else if(carta[2] == 'S') { return R.drawable.saltar_turno_amarillo }
            else if(carta[2] == '2') { return R.drawable.mas_dos_amarillo }
            else if(carta[2] == 'C') { return R.drawable.cambio_color_amarillo }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_amarillo }
            else{return 0}
        }
        else if(carta[1] == 'B') {
            if(carta[2] == 'X') {
                if(carta[0] == '0') { return R.drawable.cero_azul }
                if(carta[0] == '1') { return R.drawable.uno_azul }
                if(carta[0] == '2') { return R.drawable.dos_azul }
                if(carta[0] == '3') { return R.drawable.tres_azul }
                if(carta[0] == '4') { return R.drawable.cuatro_azul }
                if(carta[0] == '5') { return R.drawable.cinco_azul }
                if(carta[0] == '6') { return R.drawable.seis_azul }
                if(carta[0] == '7') { return R.drawable.siete_azul }
                if(carta[0] == '8') { return R.drawable.ocho_azul }
                if(carta[0] == '9') { return R.drawable.nueve_azul }
            }
            else if(carta[2] == 'S') {return R.drawable.saltar_turno_azul }
            else if(carta[2] == '2') {return R.drawable.mas_dos_azul }
            else if(carta[2] == 'C') {return R.drawable.cambio_color_azul }
            else if(carta[2] == '4') {return R.drawable.mas_cuatro_azul }
            else { return 0 }
        }
        else if(carta[1] == 'G') {
            if(carta[2] == 'X') {
                if(carta[0] == '0') { return R.drawable.cero_verde }
                if(carta[0] == '1') { return R.drawable.uno_verde }
                if(carta[0] == '2') { return R.drawable.dos_verde }
                if(carta[0] == '3') { return R.drawable.tres_verde }
                if(carta[0] == '4') { return R.drawable.cuatro_verde }
                if(carta[0] == '5') { return R.drawable.cinco_verde }
                if(carta[0] == '6') { return R.drawable.seis_verde }
                if(carta[0] == '7') { return R.drawable.siete_verde }
                if(carta[0] == '8') { return R.drawable.ocho_verde }
                if(carta[0] == '9') { return R.drawable.nueve_verde }
            }
            else if(carta[2] == 'S') { return R.drawable.saltar_turno_verde }
            else if(carta[2] == '2') { return R.drawable.mas_dos_verde }
            else if(carta[2] == 'C') { return R.drawable.cambio_color_verde }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_verde }
            else { return 0 }
        }
        else if((carta[0] == 'X') && (carta[1] == 'X')) {
            if(carta[2] == 'C') { return R.drawable.cambio_color_base }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_base }
            else { return 0 }
        }
        return 0
    }

    /*fun traductorCartasToString(carta: Int): String {
        if(carta == R.drawable.cero_rojo) {return "0RX"}
        if(carta == R.drawable.uno_rojo) {return "1RX"}
        if(carta == R.drawable.dos_rojo) {return "2RX"}
        if(carta == R.drawable.tres_rojo) {return "3RX"}
        if(carta == R.drawable.cuatro_rojo) {return "4RX"}
        if(carta == R.drawable.cinco_rojo) {return "5RX"}
        if(carta == R.drawable.seis_rojo) {return "6RX"}
        if(carta == R.drawable.siete_rojo) {return "7RX"}
        if(carta == R.drawable.ocho_rojo) {return "8RX"}
        if(carta == R.drawable.nueve_rojo) {return "9RX"}
        if(carta == R.drawable.saltar_turno_rojo) {return "XRS"}
        if(carta == R.drawable.mas_dos_rojo) {return "XR2"}
        if(carta == R.drawable.cambio_color_rojo) {return "XRR"}
        if(carta == R.drawable.mas_cuatro_rojo) {return "XR4"}

        if(carta == R.drawable.cero_amarillo) {return "0AX"}
        if(carta == R.drawable.uno_amarillo) {return "1AX"}
        if(carta == R.drawable.dos_amarillo) {return "2AX"}
        if(carta == R.drawable.tres_amarillo) {return "3AX"}
        if(carta == R.drawable.cuatro_amarillo) {return "4AX"}
        if(carta == R.drawable.cinco_amarillo) {return "5AX"}
        if(carta == R.drawable.seis_amarillo) {return "6AX"}
        if(carta == R.drawable.siete_amarillo) {return "7AX"}
        if(carta == R.drawable.ocho_amarillo) {return "8AX"}
        if(carta == R.drawable.nueve_amarillo) {return "9AX"}
        if(carta == R.drawable.saltar_turno_amarillo) {return "XAS"}
        if(carta == R.drawable.mas_dos_amarillo) {return "XA2"}
        if(carta == R.drawable.cambio_color_amarillo) {return "XAR"}
        if(carta == R.drawable.mas_cuatro_amarillo) {return "XA4"}

        if(carta == R.drawable.cero_azul) {return "0BX"}
        if(carta == R.drawable.uno_azul) {return "1BX"}
        if(carta == R.drawable.dos_azul) {return "2BX"}
        if(carta == R.drawable.tres_azul) {return "3BX"}
        if(carta == R.drawable.cuatro_azul) {return "4BX"}
        if(carta == R.drawable.cinco_azul) {return "5BX"}
        if(carta == R.drawable.seis_azul) {return "6BX"}
        if(carta == R.drawable.siete_azul) {return "7BX"}
        if(carta == R.drawable.ocho_azul) {return "8BX"}
        if(carta == R.drawable.nueve_azul) {return "9BX"}
        if(carta == R.drawable.saltar_turno_azul) {return "XBS"}
        if(carta == R.drawable.mas_dos_azul) {return "XB2"}
        if(carta == R.drawable.cambio_color_azul) {return "XBR"}
        if(carta == R.drawable.mas_cuatro_azul) {return "XB4"}

        if(carta == R.drawable.cero_verde) {return "0GX"}
        if(carta == R.drawable.uno_verde) {return "1GX"}
        if(carta == R.drawable.dos_verde) {return "2GX"}
        if(carta == R.drawable.tres_verde) {return "3GX"}
        if(carta == R.drawable.cuatro_verde) {return "4GX"}
        if(carta == R.drawable.cinco_verde) {return "5GX"}
        if(carta == R.drawable.seis_verde) {return "6GX"}
        if(carta == R.drawable.siete_verde) {return "7GX"}
        if(carta == R.drawable.ocho_verde) {return "8GX"}
        if(carta == R.drawable.nueve_verde) {return "9GX"}
        if(carta == R.drawable.saltar_turno_verde) {return "XGS"}
        if(carta == R.drawable.mas_dos_verde) {return "XG2"}
        if(carta == R.drawable.cambio_color_verde) {return "XGR"}
        if(carta == R.drawable.mas_cuatro_verde) {return "XG4"}

        if(carta == R.drawable.cambio_color_base) {return "XXR"}
        if(carta == R.drawable.mas_cuatro_base) {return "XX4"}
        return ""
    }*/

    val Cards = mutableListOf<Card>()
    val Gamers = mutableListOf<Gamer>()

    var cimaActual = ""
    var cimaCambiada = false
    var cimaNueva = ""

    var CartaNueva = ""
    var robadaCarta = false
    var cartaAnadida = false

    var miTurno = true

    /*val PruebaCartas = arrayOf("XXC","2RX","XRS","XX4","0GX","8YX","XB2","4BX")
    val PruebaJugadores = arrayOf("Jugador1","Jugador2","Jugador3","Jugador4")
    val PruebaNCartas = arrayOf(4,5,6,7)
    val cimaPrueba = "0GX"*/

    fun comprobarCima() {
        if(cimaActual.isEmpty()) {
            cimaCambiada = true
        }
        else{
            if (cimaActual != cimaNueva) {
                cimaCambiada = true
                cimaActual = cimaNueva
            }
        }
    }

    fun cambiarCima() {
        cimaActual = cimaNueva
        //val imageView1 = findViewById<ImageView>(R.id.image_cima)
        image_cima.setImageResource(traductorCartasToInt(cimaActual))
    }

    val manoActual = mutableListOf<String>()
    private var manoCambiada = false
    private lateinit var manoNueva : Array<String>

    fun comprobarManoNueva() {
        if(manoActual.size == 0 && (manoNueva.isNotEmpty())) { manoCambiada = true }
        else{
            if((manoActual.size < manoNueva.size) || (manoActual.size > manoNueva.size)) {
                manoCambiada = true
            }
            else { //La manoActual y la nueva tienen el mismo tamaño
                var i = 0
                while ((i < manoActual.size) && !manoCambiada) {
                    if ((manoActual[i]) != manoNueva[i]) { manoCambiada = true }
                    i++
                }
            }
        }
    }

    fun cambiarMano() {
        manoActual.removeAll(manoActual)
        val tamano = manoNueva.size -1
        for(i in 0..tamano) { manoActual.add(manoNueva[i]) }
    }

    private val jugadoresActuales = mutableListOf<String>()
    var jugadoresCambiados = false
    lateinit var jugadoresNuevos : Array<String>

    fun comprobarNombresJugadoresNuevos() {
        if(jugadoresActuales.size == 0) {
            jugadoresCambiados = true
        }
        else {
            var i = 0
            while ((i < jugadoresActuales.size) && !jugadoresCambiados) {
                if ((jugadoresActuales[i]) != jugadoresNuevos[i]) {
                    jugadoresCambiados = true
                }
                i++
            }
        }
    }

    fun cambiarJugadoresYCartas() {
        jugadoresActuales.removeAll(jugadoresActuales)
        var tamano = jugadoresNuevos.size -1
        for(i in 0..tamano) {
            jugadoresActuales.add(jugadoresNuevos[i])
        }
        numCartasJugadoresActuales.removeAll(numCartasJugadoresActuales)
        tamano = numCartasJugadoresNuevos.size -1
        for(i in 0..tamano) {
            numCartasJugadoresActuales.add(numCartasJugadoresNuevos[i])
        }
    }

    private val numCartasJugadoresActuales  = mutableListOf<Int>()
    var numCartasJugadoresCambiados = false
    lateinit var numCartasJugadoresNuevos : Array<Int>

    fun comprobarCartasJugadores() {
        if(numCartasJugadoresActuales.size == 0) {
            numCartasJugadoresCambiados = true
        }
        else {
            var i = 0
            while ((i < numCartasJugadoresActuales.size) && !numCartasJugadoresCambiados) {
                if (numCartasJugadoresActuales[i] != numCartasJugadoresNuevos[i]) {
                    numCartasJugadoresCambiados = true
                }
                i++
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tablero)
        session = intent.getStringExtra("session").toString()
        /*manoNueva = PruebaCartas
        jugadoresNuevos = PruebaJugadores
        numCartasJugadoresNuevos = PruebaNCartas
        cimaNueva = cimaPrueba*/

        actualizar()

        /* Tiempo de turno */
        timer = object: CountDownTimer(initial, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                //timerText.text = getString(R.string.time_left, timeLeft.toString())
            }
            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }
        //val putButton = findViewById<View>(R.id.buttonPoner) as Button
        //val pedirUnoButton = findViewById<View>(R.id.buttonPedirUno) as Button
        //val robarButton = findViewById<View>(R.id.buttonRobarCarta) as Button
        //val pasarButton = findViewById<View>(R.id.buttonPasar) as Button
        buttonPoner.setOnClickListener {
            if(miTurno && !cartaPuesta) ponerCarta()
        }
        buttonPedirUno.setOnClickListener{
            if(miTurno) pedirUno()
        }
        buttonRobarCarta.setOnClickListener{
            if(miTurno && !robadaCarta) robarCarta()
        }
        //buttonPasar.setOnClickListener{ if(miTurno) pasarTurno() }
    }

    private var haDichoUnozar = false
    private fun pedirUno() {
        haDichoUnozar = true
    }

    private fun robarCarta() {

        //Pedir robar carta al servidor
        RetrofitClient.instance.draw(TokenRequest(session))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        //CartaNueva = response.body()!!.
                        CartaNueva = "XG2"
                        robadaCarta = true
                        cartaAnadida = true
                        session = response.body()?.token.toString()
                    }
                    else {
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    fun anadirCartaMano(nuevaCarta:String) {
        manoActual.add(nuevaCarta)
        manoNueva += nuevaCarta
    }

    /*private fun pasarTurno() {
        robadaCarta = false
        cartaPuesta = true
        miTurno = false
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
    }*/

    private var cartaPuesta = false
    fun quitarCarta() {
        val auxMano = mutableListOf<String>()
        val tamano = manoActual.size -1
        val cartaExp = nombreRecordado
        lateinit var cartaMia : String
        for(i in 0..tamano) {
            cartaMia = manoActual[i]
            if(cartaMia != cartaExp) auxMano.add(manoActual[i])
        }
        manoNueva = auxMano.toTypedArray()
    }

    private lateinit var colorSelected: String
    private fun ponerCarta() {
        //Si es una +4 o un cambia color
        if(nombreRecordado == "XXC" || nombreRecordado == "XX4") {
            val builder = AlertDialog.Builder(this)
            val items = arrayOf("Red", "Green", "Yellow", "Blue")
            with(builder)
            {
                setTitle("Elija un color")
                setItems(items) { _, which ->
                    //Poner carta
                    Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                    if(items[which].equals("Red") && nombreRecordado == "XXC") {
                        nombreRecordado = "XRC"
                        colorSelected = "R"
                    }
                    else if(items[which].equals("Green") && nombreRecordado == "XXC") {
                        nombreRecordado = "XGC"
                        colorSelected = "G"
                    }
                    else if(items[which].equals("Blue") && nombreRecordado == "XXC") {
                        nombreRecordado = "XBC"
                        colorSelected = "B"
                    }
                    else if(items[which].equals("Yellow") && nombreRecordado == "XXC") {
                        nombreRecordado = "XYC"
                        colorSelected = "Y"
                    }
                    else if(items[which].equals("Red") && nombreRecordado == "XX4") {
                        nombreRecordado = "XR4"
                        colorSelected = "R"
                    }
                    else if(items[which].equals("Green") && nombreRecordado == "XX4") {
                        nombreRecordado = "XG4"
                        colorSelected = "G"
                    }
                    else if(items[which].equals("Blue") && nombreRecordado == "XX4") {
                        nombreRecordado = "XB4"
                        colorSelected = "B"
                    }
                    else if(items[which].equals("Yellow") && nombreRecordado == "XX4") {
                        nombreRecordado = "XY4"
                        colorSelected = "Y"
                    }
                }
                show()
            }
        }

        //Mandar carta al servidor
        /** Queda parámetro de posición de la carta en nuestro mazo**/
        RetrofitClient.instance.playCard(PlayCardRequest(session, record, haDichoUnozar, colorSelected))
            .enqueue(object : Callback<TokenResponse> {
                override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                    if (response.code() == 200) {
                        //La carta se ha puesto con éxito
                        quitarCarta()
                        nombreRecordado = ""
                        record = 0
                        recordCambiado = true
                    } else {
                        Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun anyadirCartas() {
        Cards.clear()
        val tamano = manoActual.size - 1
        Cards.add(Card(1, "inicio", R.drawable.inicio))
        for(i in 0..tamano) {
            Cards.add(Card(i.toLong(), manoActual[i], traductorCartasToInt(manoActual[i])))
        }
        Cards.add(Card(1, "fin", R.drawable.fin))

        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = CardAdapter(Cards)
        rvCard.adapter = adapter
    }

    fun anyadirGamers() {
        Gamers.clear()
        val tamano = jugadoresActuales.size - 1
        for(i in 0..tamano) {
            /*var turno: String = ""
            if (i == turn) turno = "Su turno"*/
            Gamers.add(Gamer(i.toLong(), R.drawable.jesica, jugadoresActuales[i], /* turno */"su turno", numCartasJugadoresActuales[i].toString() + "  Cartas"))
        }
        rvGamer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = GamerAdapter(Gamers)
        rvGamer.adapter = adapter
    }

    fun cambiarElegido() {
        image_record.setImageResource(record)
    }

    private fun actualizar() {
        CoroutineScope(Dispatchers.IO).launch {
            while(!finished) {
                    RetrofitClient.instance.readGame(TokenRequest(session))
                        .enqueue(object : Callback<GameInfoResponse> {
                            override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            } override fun onResponse(call: Call<GameInfoResponse>, response: Response<GameInfoResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    Toast.makeText(applicationContext, "Actualización", Toast.LENGTH_LONG).show()
                                    //image_cima.setImageResource(traductorCartasToInt(response.body()!!.topDiscard))
                                    val prevTurn = turn
                                    turn = response.body()!!.turn

                                    /*** Players info ***/
                                    miTurno = response.body()!!.turn == 0

                                    if(miTurno){
                                        val definirTurno = findViewById<TextView>(R.id.your_turn) as TextView
                                        runOnUiThread {
                                            definirTurno.text = "Tu turno"
                                        }
                                    }
                                    else{
                                        val definirTurno = findViewById<TextView>(R.id.your_turn) as TextView
                                        runOnUiThread {
                                            definirTurno.text = "No es tu turno"
                                        }
                                    }

                                    manoNueva= response.body()!!.playerCards
                                    comprobarManoNueva()
                                    if(manoCambiada){
                                        cambiarMano()
                                        val misNumCartas = findViewById(R.id.your_cards) as TextView
                                        runOnUiThread {
                                            misNumCartas.text = (manoActual.size).toString() + " Cartas"
                                            anyadirCartas()
                                        }
                                        manoCambiada = false
                                    }

                                    if(robadaCarta && cartaAnadida) {
                                        anadirCartaMano(CartaNueva)
                                        //val misNumCartas = findViewById<TextView>(R.id.your_cards) as TextView
                                        runOnUiThread {
                                            your_cards.text = (manoActual.size).toString() + " Cartas"
                                            anyadirCartas()
                                        }
                                        cartaAnadida = false
                                    }

                                    jugadoresNuevos = response.body()!!.playersIds
                                    numCartasJugadoresNuevos = response.body()!!.playersNumCards
                                    comprobarNombresJugadoresNuevos()
                                    comprobarCartasJugadores()
                                    if(jugadoresCambiados || numCartasJugadoresCambiados) {
                                        cambiarJugadoresYCartas()
                                        runOnUiThread { anyadirGamers() }
                                        jugadoresCambiados = false
                                        numCartasJugadoresCambiados = false
                                    }

                                    cimaNueva = response.body()!!.topDiscard
                                    comprobarCima()
                                    if(cimaCambiada) {
                                        runOnUiThread { cambiarCima() }
                                        cimaCambiada = false
                                    }

                                    if(recordCambiado) {
                                        runOnUiThread { cambiarElegido() }
                                        recordCambiado = false
                                    }

                                    if(prevTurn != turn) {  // Cambio de turno
                                        /*timer.cancel()
                                        timer.start()*/
                                    }
                                } else Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            }
                        })
                    delay(500)
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
            PAUSE_ID -> { return true }
            EXIT_ID -> {
                RetrofitClient.instance.quitMatch(TokenRequest(session))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                session = response.body()?.token.toString()
                                Toast.makeText(applicationContext, getString(R.string.quit_game_success), Toast.LENGTH_LONG).show()
                                val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                setResult(Activity.RESULT_OK, intent)
                                finish()
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
