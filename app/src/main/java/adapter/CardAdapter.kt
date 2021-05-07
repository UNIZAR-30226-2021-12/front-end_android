package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import data.Card
import eina.unizar.unozar.*
import kotlinx.android.synthetic.main.item_card.view.*


//Permite recoger todos los elementos hacer la configuracion para el view
class CardAdapter(val cards: List<Card>):RecyclerView.Adapter<CardAdapter.CardHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return CardHolder(layoutInflater.inflate(R.layout.item_card, parent, false))
    }

    override fun onBindViewHolder(holder: CardHolder, position: Int) {
        holder.render(cards[position])
    }

    override fun getItemCount(): Int {//Cuantas cartas tiene
            return cards.size
    }

    class CardHolder(val view: View):RecyclerView.ViewHolder(view){
        //val main = MainActivity()
        fun render(card: Card){
            Picasso.get().load(card.image).into(view.card_image)
            view.setOnClickListener{
                //main.showRecord(card.image,view.card_image)
                //Si la carta es válida, permite cogerla
                if(card.name != "inicio" || card.name != "fin"){ //No se pueden coger las cartas del inicio y del fin
                    posCambiado = card.id
                    record = card.image
                    nombreRecordado = card.name
                    recordCambiado=true
                }
            }
        }
    }
}


