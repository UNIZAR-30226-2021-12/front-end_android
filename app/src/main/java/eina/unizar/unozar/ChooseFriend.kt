package eina.unizar.unozar

import adapter.FriendsListAdapter
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import data.FriendInfo
import kotlinx.android.synthetic.main.activity_choose_friend.*
import kotlinx.android.synthetic.main.activity_choose_friend.add_friend_list
import kotlinx.android.synthetic.main.activity_friends.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.TokenRequest
import server.response.FriendsListResponse

class ChooseFriend : AppCompatActivity() {

    private var CODE = 73
    private val add = Menu.FIRST
    private lateinit var session: String
    private lateinit var code: String
    private lateinit var email: String
    private lateinit var friends: ArrayList<FriendInfo>
    private lateinit var adapter: FriendsListAdapter
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.oso,
        R.drawable.larry,
        R.drawable.jesica
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_friend)
        session = intent.getStringExtra("session").toString()
        code = intent.getStringExtra("code").toString()

        friends = ArrayList()
        getFriendsRequest()

        registerForContextMenu(add_friend_list)

        exit.setOnClickListener { cancel() }
    }

    private fun getFriendsRequest() {
        RetrofitClient.instance.getFriends(TokenRequest(session))
            .enqueue(object : Callback<FriendsListResponse> {
                override fun onFailure(call: Call<FriendsListResponse>, t: Throwable) {
                    //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<FriendsListResponse>, response: Response<FriendsListResponse>) {
                    if (response.code() == 200) {
                        var i = 0
                        session = response.body()?.token.toString()
                        /*val emailArray = response.body()!!.emails
                        val aliasArray = response.body()!!.alias
                        val avatarArray = response.body()!!.avatarIds
                        val idArray = response.body()!!.friendIds*/
                        while (i < response.body()!!.avatarIds?.size!!) {
                            Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                            friends.add(FriendInfo(
                                response.body()!!.friendIds?.get(i),
                                response.body()!!.alias?.get(i),
                                response.body()!!.emails?.get(i),
                                avatars[response.body()!!.avatarIds?.get(i)!!])
                            )
                            i++
                        }
                        adapter = FriendsListAdapter(this@ChooseFriend, friends)
                        add_friend_list.adapter = adapter
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun addFriend(friend: String) {
        val addFriend = AlertDialog.Builder(this)
        addFriend.setTitle(getString(R.string.add_to_game, friend))
        addFriend.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto: $email")
            intent.putExtra(Intent.EXTRA_EMAIL, "UNOZAR")
            intent.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.email_message, code)
            )
            startActivityForResult(intent, CODE)
        }
        addFriend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int -> }
        addFriend.show()
    }

    private fun cancel() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, add, Menu.NONE, getString(R.string.add_friend))
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { add -> {
            val info = item.menuInfo as AdapterView.AdapterContextMenuInfo
            email = friends[info.position].email.toString()
            addFriend(friends[info.position].alias.toString())
            return true
        }}
        return super.onContextItemSelected(item)
    }

    override fun onActivityResult (requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CODE) {
            val intent = Intent().apply { putExtra("session", session) }
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else { super.onActivityResult(requestCode, resultCode, data) }
    }
}
