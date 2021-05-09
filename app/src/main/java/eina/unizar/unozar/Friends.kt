package eina.unizar.unozar

import adapter.FriendsListAdapter
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import data.FriendInfo
import kotlinx.android.synthetic.main.activity_friends.*
import kotlinx.android.synthetic.main.custom_alertdialog.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import server.request.AddFriendRequest
import server.request.TokenRequest
import server.response.FriendsListResponse
import server.response.TokenResponse

class Friends : AppCompatActivity() {

    private lateinit var session: String
    private lateinit var code: String
    private lateinit var friends: ArrayList<FriendInfo>
    private lateinit var adapter: FriendsListAdapter
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.oso,
        R.drawable.larry,
        R.drawable.jesica
    )

    private val delete = Menu.FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        session = intent.getStringExtra("session").toString()
        code = intent.getStringExtra("code").toString()
        friends = ArrayList()
        getFriendsRequest()

        registerForContextMenu(add_friend_list)
        add_friend.setOnClickListener {
            addFriend()
        }
        cancel_button.setOnClickListener {
            cancel()
        }
    }

    private fun getFriendsRequest() {
        RetrofitClient.instance.getFriends(TokenRequest(session))
            .enqueue(object : Callback<FriendsListResponse> {
                override fun onFailure(call: Call<FriendsListResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                } override fun onResponse(call: Call<FriendsListResponse>, response: Response<FriendsListResponse>) {
                    if (response.code() == 200) {
                        var i = 0
                        session = response.body()?.token.toString()
                        while (i < response.body()!!.avatarIds?.size!!) {
                            friends.add(FriendInfo(
                                response.body()!!.friendIds?.get(i),
                                response.body()!!.alias?.get(i),
                                response.body()!!.emails?.get(i),
                                avatars[response.body()!!.avatarIds?.get(i)!!])
                            )
                            i++
                        }
                        adapter = FriendsListAdapter(this@Friends, friends)
                        add_friend_list.adapter = adapter
                    } else {
                        Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
    }

    private fun addFriend() {
        val addFriend = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
        addFriend.setView(customLayout)
        addFriend.setTitle(getString(R.string.add_friend_message))
        addFriend.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int ->
            RetrofitClient.instance.addFriend(AddFriendRequest(session, customLayout.inputCode.text.toString().trim()))
                .enqueue(object : Callback<TokenResponse> {
                    override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                        //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()?.token.toString()
                            Toast.makeText(applicationContext, "Amigo añadido", Toast.LENGTH_LONG).show()
                            getFriendsRequest()
                        } else {
                            //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                        }
                    }
                })
        }
        addFriend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
        addFriend.show()
    }

    private fun cancel() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View?, menuInfo: ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu.add(Menu.NONE, delete, Menu.NONE, "Eliminar")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { delete -> {
            val info = item.menuInfo as AdapterContextMenuInfo
            val friend = AlertDialog.Builder(this)
            friend.setTitle(getString(R.string.delete_friend_message))
            friend.setPositiveButton(getString(R.string.delete_button)) { _: DialogInterface, _: Int ->
                RetrofitClient.instance.deleteFriend(AddFriendRequest(session, friends[info.position].id.toString()))
                    .enqueue(object : Callback<TokenResponse> {
                        override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                            //Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_LONG).show()
                            Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                        } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                            if (response.code() == 200) {
                                Toast.makeText(applicationContext, "Amigo borrado", Toast.LENGTH_LONG).show()
                                val intent = Intent().apply { putExtra("session", response.body()?.token.toString()) }
                                setResult(Activity.RESULT_OK, intent)
                            } else {
                                //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                                Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                            }
                        }
                    })
            }
            friend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
            friend.show()
            return true
        }}
        return super.onContextItemSelected(item)
    }
}
