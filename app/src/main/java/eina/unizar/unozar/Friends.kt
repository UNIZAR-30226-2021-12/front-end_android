package eina.unizar.unozar

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_friends.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Friends : AppCompatActivity() {

    private lateinit var session: String
    private lateinit var code: String
    private lateinit var email: String
    private val add = Menu.FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        session = intent.getStringExtra("session").toString()
        code = intent.getStringExtra("code").toString()

        /*val friends: Array<String> = getFriendsRequest()
        val adapter = ArrayAdapter(this, R.layout.activity_friends, friends)
        add_friend_list.adapter = adapter

        registerForContextMenu(add_friend_list)*/

        /*add_friend_list.onItemClickListener=
            AdapterView.OnItemClickListener { parent, view, position, id -> addFriend() }*/
    }

    /*private fun getFriendsRequest(): Array<String> {
        lateinit var r: Response<FriendsListResponse>
        RetrofitClient.instance.userGetFriendsList(session.substring(0, 32), FriendsListRequest(session))
            .enqueue(object : Callback<FriendsListResponse> {
                override fun onFailure(call: Call<FriendsListResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<FriendsListResponse>, response: Response<FriendsListResponse>) {
                    if (response.code() == 200) {
                        Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                        r = response
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
        return r.body()!!.friends
    }*/

    @SuppressLint("SetTextI18n")
    fun goToChangeEmail(@Suppress("UNUSED_PARAMETER") view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    private fun addFriend() {
        val friend = AlertDialog.Builder(this)
        friend.setTitle(getString(R.string.friend))
        friend.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
            /*RetrofitClient.instance.userAddFriend(session.substring(0, 32), AddFriendRequest(email, session))
                .enqueue(object : Callback<BasicResponse> {
                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                        //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                        if (response.code() == 200) {
                            Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })*/
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto: $email")
            intent.putExtra(Intent.EXTRA_EMAIL, "UNOZAR")
            intent.putExtra(Intent.EXTRA_SUBJECT,
                "Escribe este código en la app para unirte a una partida: $code"
            )
            startActivity(intent)
        }
        friend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
        friend.show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, add, Menu.NONE, "Añadir jugador")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { add -> { addFriend(); return true }}
        return super.onContextItemSelected(item)
    }
}
