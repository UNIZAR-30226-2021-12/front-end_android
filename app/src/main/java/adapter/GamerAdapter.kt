package adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import data.Card
import data.Gamer
import eina.unizar.unozar.R
import kotlinx.android.synthetic.main.item_card.view.*
import kotlinx.android.synthetic.main.item_gamer.view.*
import java.lang.reflect.Array.get

class GamerAdapter (val gamers:List<Gamer>): RecyclerView.Adapter<GamerAdapter.GamerHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GamerHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return GamerHolder(layoutInflater.inflate(R.layout.item_gamer,parent,false))
    }

    override fun onBindViewHolder(holder: GamerHolder, position: Int) {
        holder.render(gamers[position])
    }

    override fun getItemCount(): Int {//Cuantas cartas tiene
        return gamers.size
    }

    class GamerHolder(val view: View): RecyclerView.ViewHolder(view){
        fun render(gamer: Gamer){
            view.gamer_name.text = gamer.name
            view.gamer_Ncards.text = gamer.nCartas
            Picasso.get().load(gamer.image).into(view.gamer_image)
            view.gamer_turn.text = gamer.turno
        }
    }
}