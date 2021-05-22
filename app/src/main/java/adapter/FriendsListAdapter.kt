package adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import data.FriendInfo
import eina.unizar.unozar.R


class FriendsListAdapter(private val context: Activity, private val friends: ArrayList<FriendInfo>)
    : BaseAdapter() {

    private class ViewHolder(row: View) {
        val alias: TextView = row.findViewById<View>(R.id.friend_alias) as TextView
        val email: TextView = row.findViewById<View>(R.id.friend_email) as TextView
        val avatar: ImageView = row.findViewById<View>(R.id.friend_avatar) as ImageView

    }

    /*private val inflater: LayoutInflater
        = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater */

    override fun getCount(): Int {
        return friends.size
    }

    override fun getItem(position: Int): Any {
        return friends[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        val row: View
        val holder: ViewHolder

        if (view == null) {
            val inflater = LayoutInflater.from(context)
            row = inflater.inflate(R.layout.friends_row, parent, false)
            holder = ViewHolder(row)
            row.tag = holder
        } else {
            row = view
            holder = row.tag as ViewHolder
        }

        val friend = getItem(position) as FriendInfo

        holder.avatar.setImageResource(friend.avatar!!)
        holder.alias.text = friend.alias
        holder.email.text = friend.email

        return row
    }
}
