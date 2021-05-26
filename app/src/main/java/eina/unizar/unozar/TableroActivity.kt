package eina.unizar.unozar

import adapter.CardAdapter
import adapter.GamerAdapter
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
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

var posCambiado :Long = 0
var recordCambiado = false
var nombreRecordado = ""
var record = 0

class TableroActivity : AppCompatActivity() {

    private lateinit var session: String
    private lateinit var avatarIds: Array<String>
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
    private var backCards = arrayListOf(
        R.drawable.reverso1,
        R.drawable.reverso2,
        R.drawable.reverso3,
        R.drawable.reverso4,
        R.drawable.reverso5
    )
    private var myPos: Int = 5
    private var turn: Int = 5
    private var finished = false
    private var winner = 0
    private var gone = false
    private var done = true
    private var pause = false
    private var quit = false
    private var kicked = false
    private var ponerCarta = false

    private fun traductorCartasToInt(carta: String): Int {
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_rojo }
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_amarillo }
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_azul }
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_verde }
        }
        else if((carta[0] == 'X') && (carta[1] == 'X')) {
            if(carta[2] == 'C') { return R.drawable.cambio_color_base }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_base }
        }
        return R.drawable.cambio_color_verde
    }

    private val cards = mutableListOf<Card>()
    private val gamers = mutableListOf<Gamer>()

    private var cimaActual = ""
    private var cimaCambiada = false
    private var cimaNueva = ""

    var robadaCarta = false

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
        for(i in manoNueva.indices) { manoActual.add(manoNueva[i]) }
    }

    private val jugadoresActuales = mutableListOf<String>()
    var jugadoresCambiados = false
    private lateinit var jugadoresNuevos : Array<String>

    lateinit var idJugadoresActuales : Array<String>
    lateinit var idJugadoresNuevos : Array<String>
    private lateinit var idJugadoresCambiados : Array<Boolean>

    fun comprobarIdsJugadores() {
        var cont = 0
        for (i in idJugadoresActuales.indices) {
             if (!(idJugadoresActuales[i]).equals(idJugadoresNuevos[i])) {
                 cont++
                 idJugadoresCambiados[i] = true
             }
        }
        if (cont > 0) { jugadoresCambiados = true }
    }

    fun cambiarJugadoresYCartas() {
        jugadoresActuales.removeAll(jugadoresActuales)
        for(i in jugadoresNuevos.indices) {
            if(!idJugadoresCambiados[i]) {
                jugadoresActuales.add(jugadoresNuevos[i])
            }
            else{
                jugadoresActuales.add("IA")
            }
        }
        numCartasJugadoresActuales.removeAll(numCartasJugadoresActuales)
        for(i in numCartasJugadoresNuevos.indices) {
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
                if (numCartasJugadoresActuales[i] == 0) {
                    finished = true
                    winner = i
                }
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
        idJugadoresCambiados = arrayOf(false,false,false,false)
        idJugadoresActuales = intent.getStringArrayExtra("ids")!!
        idJugadoresNuevos = intent.getStringArrayExtra("ids")!!
        myPos = intent.getIntExtra("myPosition", 0)
        avatarIds = intent.getStringArrayExtra("avatars")!!
        jugadoresNuevos = intent.getStringArrayExtra("names")!!
        if(intent.getIntExtra("board", 0) != 0)
            board_layout.setBackgroundResource(boards[intent.getIntExtra("board", 0)])
        image_mazo.setBackgroundResource(backCards[intent.getIntExtra("card", 0)])

        actualizar()

        buttonPoner.setOnClickListener {
            pause = false
            if(miTurno && !cartaPuesta) {
                if (cimaActual[0].isDigit() && cimaActual[0] == nombreRecordado[0])
                    ponerCarta()
                else if ((cimaActual[1] != 'X' && cimaActual[1] == nombreRecordado[1]) || nombreRecordado[1] == 'X')
                    ponerCarta()
                else if (cimaActual[2] != 'X' && cimaActual[2] == nombreRecordado[2])
                    ponerCarta()
                else
                    Toast.makeText(applicationContext, getString(R.string.cant_play), Toast.LENGTH_SHORT).show()
            }
        }
        buttonPedirUno.setOnClickListener{
            if(miTurno) {
                if (manoActual.size == 2) {
                    haDichoUnozar = true
                    Toast.makeText(applicationContext, "UNOZAR", Toast.LENGTH_SHORT).show()
                    buttonPedirUno.setTextColor(0xFFFFFFF)
                    buttonPedirUno.setBackgroundColor(0x4C4845)
                } else
                    Toast.makeText(applicationContext, "No puedes", Toast.LENGTH_SHORT).show()
            }
        }
        buttonRobarCarta.setOnClickListener{
            if(miTurno && !robadaCarta) {
                pedirRobada = true
                pause = false
            }
        }
    }

    private var haDichoUnozar = false

    private var pedirRobada = false

    private var cartaPuesta = false

    private lateinit var colorSelected: String
    private fun ponerCarta() {
        colorSelected = "Y"
        //Si es una +4 o un cambia color
        if(nombreRecordado.equals("XXC") || nombreRecordado.equals("XX4")) {
            val builder = AlertDialog.Builder(this)
            val items = arrayOf(
                getString(R.string.red),
                getString(R.string.green),
                getString(R.string.yellow),
                getString(R.string.blue)
            )
            with(builder)
            {
                setTitle("Elija un color")
                setItems(items) { _, which ->
                    //Poner carta
                    if(items[which].equals(getString(R.string.red)) && nombreRecordado == "XXC") {
                        nombreRecordado = "XRC"
                        colorSelected = "R"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.green)) && nombreRecordado == "XXC") {
                        nombreRecordado = "XGC"
                        colorSelected = "G"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.blue)) && nombreRecordado == "XXC") {
                        nombreRecordado = "XBC"
                        colorSelected = "B"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.yellow)) && nombreRecordado == "XXC") {
                        nombreRecordado = "XYC"
                        colorSelected = "Y"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.red)) && nombreRecordado == "XX4") {
                        nombreRecordado = "XR4"
                        colorSelected = "R"
                    }
                    else if(items[which].equals(getString(R.string.green)) && nombreRecordado == "XX4") {
                        nombreRecordado = "XG4"
                        colorSelected = "G"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.blue)) && nombreRecordado == "XX4") {
                        nombreRecordado = "XB4"
                        colorSelected = "B"
                        ponerCarta = true
                        pause = false
                    }
                    else if(items[which].equals(getString(R.string.yellow)) && nombreRecordado == "XX4") {
                        nombreRecordado = "XY4"
                        colorSelected = "Y"
                        ponerCarta = true
                        pause = false
                    }
                }
                show()
            }
        }
        else {
            ponerCarta = true
            pause = false
        }
    }

    private fun anyadirCartas() {
        cards.clear()
        cards.add(Card(1, "inicio", R.drawable.inicio))
        for(i in manoActual.indices) {
            cards.add(Card(i.toLong(), manoActual[i], traductorCartasToInt(manoActual[i])))
        }
        cards.add(Card(1, "fin", R.drawable.fin))

        rvCard.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvCard.apply{ adapter = CardAdapter(cards) }
    }

    fun anyadirGamers() {
        gamers.clear()
        for(i in idJugadoresActuales.indices) {
            var turno = ""
            if (!(idJugadoresActuales[i].equals(session.substring(0,32)))) {
                if (i == turn) turno = "Su turno"
                if((jugadoresActuales[i]).equals("IA")){
                    gamers.add(Gamer(i.toLong(), avatars[avatarIds[i].toInt()], jugadoresActuales[i], turno, numCartasJugadoresActuales[i].toString() + "  Cartas"))
                }
                else{
                    gamers.add(Gamer(i.toLong(), avatars[avatarIds[i].toInt()], jugadoresActuales[i], turno, numCartasJugadoresActuales[i].toString() + "  Cartas"))
                }
            }

        }
        rvGamer.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = GamerAdapter(gamers)
        rvGamer.adapter = adapter
    }

    private fun actualizar() {
        CoroutineScope(Dispatchers.IO).launch {
            while(!finished && !gone && !kicked) {
                if(pedirRobada && done) {
                    pedirRobada = false
                    done = false
                    //Pedir robar carta al servidor
                    RetrofitClient.instance.draw(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    Toast.makeText(applicationContext, "Carta robada", Toast.LENGTH_SHORT).show()
                                    robadaCarta = true
                                    done = true
                                    if (haDichoUnozar) {
                                        haDichoUnozar = false
                                        buttonPedirUno.setTextColor(0x000000)
                                        buttonPedirUno.setBackgroundColor(0xFF7D55)
                                    }
                                } else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                    done = true
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
                                Toast.makeText(applicationContext, "El servidor no responde", Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    if (haDichoUnozar) {
                                        haDichoUnozar = false
                                        buttonPedirUno.setTextColor(0x000000)
                                        buttonPedirUno.setBackgroundColor(0xFF7D55)
                                    }
                                    nombreRecordado = ""
                                    record = 0
                                    recordCambiado = true
                                    done = true
                                } else {
                                    Toast.makeText(applicationContext, "Quizás se haya caido el servidor", Toast.LENGTH_SHORT).show()
                                    done = true
                                }
                            }
                        })
                } else if(pause && done) {
                    done = false
                    //Pedir robar carta al servidor
                    RetrofitClient.instance.pause(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    Toast.makeText(applicationContext, "Pausa", Toast.LENGTH_SHORT).show()
                                    done = true
                                } else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                    done = true
                                }
                            }
                        })
                } else if(quit && done) {
                    quit = false
                    done = false
                    gone = true
                    //Pedir robar carta al servidor
                    RetrofitClient.instance.quitMatch(TokenRequest(session))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_SHORT).show()
                                    done = true
                                }
                            }
                        })
                } else if(done && !pause) {
                    done = false
                    RetrofitClient.instance.readGame(TokenRequest(session))
                        .enqueue(object : Callback<GameInfoResponse> {
                            override fun onFailure(call: Call<GameInfoResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<GameInfoResponse>, response: Response<GameInfoResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    for (i in response.body()!!.playersNumCards.indices) {
                                        if (response.body()!!.playersNumCards[i] == 0) {
                                            finished = true
                                            gone = true
                                            winner = i
                                            val builder = AlertDialog.Builder(this@TableroActivity)
                                            var mensaje = "DERROTA"
                                            if (winner == myPos) mensaje = "¡¡VICTORIA!!"
                                            builder.setTitle(mensaje)
                                            builder.setPositiveButton("Volver") { _: DialogInterface, _: Int ->
                                                val intent = Intent().apply { putExtra("session", session) }
                                                setResult(Activity.RESULT_OK, intent)
                                                finish()
                                            }
                                            builder.show()
                                        }
                                    }
                                    turn = response.body()!!.turn
                                    /*** Players info ***/
                                    miTurno = response.body()!!.turn == myPos

                                    if (miTurno) {
                                        runOnUiThread { your_turn.text = getString(R.string.your_turn) }
                                    } else {
                                        runOnUiThread { your_turn.text = getString(R.string.not_your_turn) }
                                        //Reiniciar variables porque ya no es tu turno
                                        robadaCarta = false
                                    }

                                    manoNueva = response.body()!!.playerCards
                                    comprobarManoNueva()
                                    if (manoCambiada) {
                                        cambiarMano()
                                        your_cards.text =getString(R.string.your_cards, manoActual.size.toString())
                                        runOnUiThread { anyadirCartas() }
                                        manoCambiada = false
                                    }

                                    idJugadoresNuevos = response.body()!!.playersIds
                                    numCartasJugadoresNuevos = response.body()!!.playersNumCards
                                    comprobarIdsJugadores()
                                    comprobarCartasJugadores()
                                    if (jugadoresCambiados || numCartasJugadoresCambiados) {
                                        cambiarJugadoresYCartas()
                                        runOnUiThread { anyadirGamers() }
                                        jugadoresCambiados = false
                                        numCartasJugadoresCambiados = false
                                    }

                                    cimaNueva = response.body()!!.topDiscard
                                    comprobarCima()
                                    if (cimaCambiada) {
                                        runOnUiThread { cambiarCima() }
                                        cimaCambiada = false
                                    }
                                    if (recordCambiado) {
                                        runOnUiThread { image_record.setImageResource(record) }
                                        recordCambiado = false
                                    }
                                    done = true
                                } else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                    kicked = true
                                    gone = true
                                    done = true
                                    finish()
                                }
                            }
                        })
                    delay(2000)
                }
            }
        }
    }

    fun showMenu(v : View){
        val popup = PopupMenu(this, v)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.game_menu, popup.menu)
        popup.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.pause-> if(miTurno) pause = true
                R.id.exit-> quit = true
            }
            true
        }
        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this@TableroActivity)
        builder.setTitle("¿Desea salir de la partida?")
        builder.setPositiveButton(getString(R.string.alert_possitive_button)) { _: DialogInterface, _: Int -> quit = true }
        builder.setNegativeButton(getString(R.string.alert_negative_button)) {_: DialogInterface, _: Int -> }
        builder.show()
    }
}
