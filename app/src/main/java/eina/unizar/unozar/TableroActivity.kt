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
import org.jetbrains.anko.doAsync
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.PlayCardRequest
import server.request.TokenRequest
import server.response.GameInfoResponse
import server.response.TokenResponse
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread

var posCambiado :Long = 0
var recordCambiado = false
var nombreRecordado = ""
var record = 0
private val sharedCounterLock = ReentrantLock()

class TableroActivity : AppCompatActivity() {

    internal lateinit var timer : CountDownTimer
    internal val initial: Long = 60000
    internal val interval: Long = 1000
    private lateinit var session: String
    private var turn: Int = 5
    private var finished = false
    private var done = true
    private var ponerCarta = false

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
            //else { return 0 }
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
            //else{return 0}
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
            //else { return 0 }
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
            //else { return 0 }
        }
        else if((carta[0] == 'X') && (carta[1] == 'X')) {
            if(carta[2] == 'C') { return R.drawable.cambio_color_base }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_base }
            //else { return 0 }
        }
        Toast.makeText(applicationContext, "La carta no existe", Toast.LENGTH_LONG).show()
        return R.drawable.cambio_color_verde
    }

    val Cards = mutableListOf<Card>()
    val Gamers = mutableListOf<Gamer>()

    var cimaActual = ""
    var cimaCambiada = false
    var cimaNueva = ""

    var robadaCarta = false
    var cartaAnadida = false

    var miTurno = true

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

    lateinit var idJugadoresActuales : Array<String>
    lateinit var idJugadoresNuevos : Array<String>
    lateinit var idJugadoresCambiados : Array<Boolean>

    fun comprobarIdsJugadores() {
        var i = 0
        var cont = 0
        while (i < idJugadoresActuales.size) {
             if (!(idJugadoresActuales[i]).equals(idJugadoresNuevos[i])) {
                 cont++
                 idJugadoresCambiados[i] = true
             }
             i++
        }
        if(cont > 0){
            jugadoresCambiados = true
        }
    }

    fun cambiarJugadoresYCartas() {
        jugadoresActuales.removeAll(jugadoresActuales)
        var tamano = jugadoresNuevos.size -1
        for(i in 0..tamano) {
            if(!idJugadoresCambiados[i]) {
                jugadoresActuales.add(jugadoresNuevos[i])
            }
            else{
                jugadoresActuales.add("IA")
            }
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
    lateinit var imgJugadores : Array<Int>

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

        //Pasar nombres de jugadores desde la anterior actividad
        idJugadoresCambiados = arrayOf(false,false,false)
        idJugadoresActuales = intent.getStringArrayExtra("ids")!!
        idJugadoresNuevos = intent.getStringArrayExtra("ids")!!
        jugadoresNuevos = arrayOf("Alberto", "IA")
        //imgJugadores =


        actualizar()

        /* Tiempo de turno */
        /*timer = object: CountDownTimer(initial, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                //timerText.text = getString(R.string.time_left, timeLeft.toString())
            }
            override fun onFinish() {
                TODO("Not yet implemented")
            }
        }*/

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

    var pedirRobada = false

    private fun robarCarta() {
        pedirRobada = true
    }

    private var cartaPuesta = false

    private lateinit var colorSelected: String
    private fun ponerCarta() {
        colorSelected = "Y"
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
        ponerCarta = true
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
        //var adapter = CardAdapter(Cards)
        rvCard.apply{
            adapter = CardAdapter(Cards)
        }
    }

    fun anyadirGamers() {
        Gamers.clear()
        val tamano = jugadoresActuales.size - 1
        for(i in 0..tamano) {
            var turno = ""
            if (i == turn) turno = "Su turno"
            if((jugadoresActuales[i]).equals("IA")){
                Gamers.add(Gamer(i.toLong(), R.drawable.robotia/*Imagen IA*/, jugadoresActuales[i], turno, numCartasJugadoresActuales[i].toString() + "  Cartas"))
            }
            else{
                Gamers.add(Gamer(i.toLong(), imgJugadores[i], jugadoresActuales[i], turno, numCartasJugadoresActuales[i].toString() + "  Cartas"))
            }
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
                //sharedCounterLock.lock()
                if(!ponerCarta && !pedirRobada && done) {
                    done = false
                    RetrofitClient.instance.readGame(TokenRequest(session))
                        .enqueue(object : Callback<GameInfoResponse> {
                            override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                                Toast.makeText(
                                    applicationContext,
                                    getString(R.string.no_response),
                                    Toast.LENGTH_LONG
                                ).show()
                            }

                            override fun onResponse(
                                call: Call<GameInfoResponse>,
                                response: Response<GameInfoResponse>
                            ) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    val prevTurn = turn
                                    turn = response.body()!!.turn
                                    /*** Players info ***/
                                    miTurno = response.body()!!.turn == 0

                                    if (miTurno) {
                                        val definirTurno =
                                            findViewById<TextView>(R.id.your_turn) as TextView
                                        runOnUiThread {
                                            definirTurno.text = "Tu turno"
                                        }
                                    } else {
                                        val definirTurno =
                                            findViewById<TextView>(R.id.your_turn) as TextView
                                        runOnUiThread {
                                            definirTurno.text = "No es tu turno"
                                        }
                                        //Reiniciar variables porque ya no es tu turno
                                        robadaCarta = false
                                    }


                                    manoNueva = response.body()!!.playerCards
                                    comprobarManoNueva()
                                    if (manoCambiada) {
                                        cambiarMano()
                                        your_cards.text =
                                            (manoActual.size).toString() + " Cartas"
                                        runOnUiThread {
                                            anyadirCartas()
                                        }
                                        manoCambiada = false
                                    }

                                    idJugadoresNuevos = response.body()!!.playersIds
                                    numCartasJugadoresNuevos = response.body()!!.playersNumCards
                                    comprobarIdsJugadores()
                                    comprobarCartasJugadores()
                                    if (jugadoresCambiados || numCartasJugadoresCambiados) {
                                        cambiarJugadoresYCartas()
                                        runOnUiThread {
                                            anyadirGamers()
                                        }
                                        jugadoresCambiados = false
                                        numCartasJugadoresCambiados = false
                                    }

                                    cimaNueva = response.body()!!.topDiscard
                                    comprobarCima()
                                    if (cimaCambiada) {
                                        runOnUiThread {
                                            cambiarCima()
                                        }
                                        cimaCambiada = false
                                    }

                                    if (recordCambiado) {
                                        runOnUiThread {
                                            cambiarElegido()
                                        }
                                        recordCambiado = false
                                    }

                                    if (prevTurn != turn) {  // Cambio de turno
                                        /*timer.cancel()
                                            timer.start()*/
                                    }
                                    //sharedCounterLock.unlock()
                                    done = true
                                } else Toast.makeText(
                                    applicationContext,
                                    response.code(),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        })
                    delay(1000)
                }
                else if(pedirRobada && done) {
                    pedirRobada = false
                    done = false
                    //Pedir robar carta al servidor
                    RetrofitClient.instance.draw(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    Toast.makeText(applicationContext, "Carta robada", Toast.LENGTH_LONG).show()
                                    robadaCarta = true
                                    session = response.body()?.token.toString()
                                    done = true
                                }
                                else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                } else if(ponerCarta && done) {
                    ponerCarta = false
                    done = false
                    //Mandar carta al servidor
                    RetrofitClient.instance.playCard(PlayCardRequest(session, posCambiado.toInt(), haDichoUnozar, colorSelected))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_LONG).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    //La carta se ha puesto con éxito
                                    //quitarCarta() No hace falta quitar la carta de la mano
                                    Toast.makeText(applicationContext, "Carta puesta", Toast.LENGTH_LONG).show()
                                    nombreRecordado = ""
                                    record = 0
                                    recordCambiado = true
                                    done = true
                                } else {
                                    Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_LONG).show()
                                }
                            }
                        })
                }

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
