package data

data class Gamer (
        val id: Long,
        val image: Int, //Para la imagen del jugador
        val name: String,
        val turno: String, //Para mostrar si tiene el turno(imagen)
        val nCartas: String //Para mostrar cuantas cartas tiene
)