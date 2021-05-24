package eina.unizar.unozar

import adapter.CardAdapter
import adapter.GamerAdapter
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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

    private lateinit var timer : CountDownTimer
    private val initial: Long = 60000
    private val interval: Long = 1000
    private lateinit var session: String
    private lateinit var avatarIds: Array<String>
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_amarillo }
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_azul }
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
            else if(carta[2] == 'R') {return R.drawable.cambio_sentido_verde }
            //else { return 0 }
        }
        else if((carta[0] == 'X') && (carta[1] == 'X')) {
            if(carta[2] == 'C') { return R.drawable.cambio_color_base }
            else if(carta[2] == '4') { return R.drawable.mas_cuatro_base }
            //else { return 0 }
        }
        Toast.makeText(applicationContext, "La carta no existe", Toast.LENGTH_SHORT).show()
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
        //tamano = numCartasJugadoresNuevos.size -1
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

        actualizar()

        /* Tiempo de turno */
        timer = object: CountDownTimer(initial, interval) {
            override fun onTick(millisUntilFinished: Long) {
                val timeLeft = millisUntilFinished / 1000
                timer_text.text = getString(R.string.time_left, timeLeft.toString())
            }
            override fun onFinish() {
                //
            }
        }

        buttonPoner.setOnClickListener {
            if(miTurno && !cartaPuesta) ponerCarta()
        }
        buttonPedirUno.setOnClickListener{
            if(miTurno) {
                if (manoActual.size == 2) {
                    haDichoUnozar = true
                    Toast.makeText(applicationContext, "UNOZAR", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "No puedes", Toast.LENGTH_SHORT).show()
                }
            }
        }
        buttonRobarCarta.setOnClickListener{
            if(miTurno && !robadaCarta) pedirRobada = true
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
                        ponerCarta = true
                    }
                    else if(items[which].equals("Green") && nombreRecordado == "XXC") {
                        nombreRecordado = "XGC"
                        colorSelected = "G"
                        ponerCarta = true
                    }
                    else if(items[which].equals("Blue") && nombreRecordado == "XXC") {
                        nombreRecordado = "XBC"
                        colorSelected = "B"
                        ponerCarta = true
                    }
                    else if(items[which].equals("Yellow") && nombreRecordado == "XXC") {
                        nombreRecordado = "XYC"
                        colorSelected = "Y"
                        ponerCarta = true
                    }
                    else if(items[which].equals("Red") && nombreRecordado == "XX4") {
                        nombreRecordado = "XR4"
                        colorSelected = "R"
                    }
                    else if(items[which].equals("Green") && nombreRecordado == "XX4") {
                        nombreRecordado = "XG4"
                        colorSelected = "G"
                        ponerCarta = true
                    }
                    else if(items[which].equals("Blue") && nombreRecordado == "XX4") {
                        nombreRecordado = "XB4"
                        colorSelected = "B"
                        ponerCarta = true
                    }
                    else if(items[which].equals("Yellow") && nombreRecordado == "XX4") {
                        nombreRecordado = "XY4"
                        colorSelected = "Y"
                        ponerCarta = true
                    }
                }
                show()
            }
        }
        else { ponerCarta = true }
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
                                    haDichoUnozar = false
                                    Toast.makeText(applicationContext, "Carta puesta", Toast.LENGTH_SHORT).show()
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
                    if (manoActual.size -1 == 0) {
                        finished = true
                        winner = myPos
                    }
                } else if(pause && done) {
                    pause = false
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
                                    Toast.makeText(applicationContext, getString(R.string.quit_game_success), Toast.LENGTH_SHORT).show()
                                    val intent = Intent().apply { putExtra("session", response.body()!!.token) }
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                } else {
                                    Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_SHORT).show()
                                    done = true
                                }
                            }
                        })
                } else if(done) {
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
                                            winner = i
                                        }
                                    }
                                    val prevTurn = turn
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

                                    if (prevTurn != turn) {  // Cambio de turno
                                        timer.cancel()
                                        timer.start()
                                    }
                                    done = true
                                } else {
                                    Toast.makeText(applicationContext, response.code(), Toast.LENGTH_SHORT).show()
                                    kicked = true
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
        if (!gone) {
            when {
                finished -> {
                    val builder = AlertDialog.Builder(this@TableroActivity)
                    var mensaje = "DERROTA"
                    if(winner == 0) mensaje = "¡¡VICTORIA!!"
                    builder.setTitle(mensaje)
                    builder.setPositiveButton("Volver") { _: DialogInterface, _: Int -> finish() }
                    builder.show()
                }
                kicked -> Toast.makeText(applicationContext, getString(R.string.kicked), Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(applicationContext, getString(R.string.error_happened), Toast.LENGTH_SHORT).show()
            }
            gone = true
            RetrofitClient.instance.quitMatch(TokenRequest(session))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()?.token.toString()
                            val intent = Intent().apply { putExtra("session", session) }
                            setResult(Activity.RESULT_OK, intent)
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_quit_response), Toast.LENGTH_SHORT).show()
                        }
                    }
                })
        }
    }
}
