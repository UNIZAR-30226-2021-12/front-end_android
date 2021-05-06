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
    private lateinit var friends: Array<FriendInfo>
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.oso,
        R.drawable.larry,
        R.drawable.jesica
    )

    private val add = Menu.FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        session = intent.getStringExtra("session").toString()
        code = intent.getStringExtra("code").toString()

        friends = getFriendsRequest()
        val adapter = FriendsListAdapter(this, friends)
        add_friend_list.adapter = adapter

        registerForContextMenu(add_friend_list)
        add_friend.setOnClickListener {
            addFriend()
        }
        cancel_button.setOnClickListener {
            cancel()
        }
    }

    private fun getFriendsRequest(): Array<FriendInfo> {
        var friends1 = arrayOf<FriendInfo>()
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
                            //friends.add(FriendInfo(idArray[i], aliasArray[i], emailArray[i], avatars[avatarArray[i]]))
                            friends1[i] = (FriendInfo(
                                response.body()!!.friendIds?.get(i),
                                response.body()!!.alias?.get(i),
                                response.body()!!.emails?.get(i),
                                avatars[response.body()!!.avatarIds?.get(i)!!]
                            ))
                            /*val addFriend = AlertDialog.Builder(this@Friends)
                            addFriend.setTitle(response.body()!!.emails?.get(i))
                            addFriend.setPositiveButton(getString(R.string.add_button)) { _: DialogInterface, _: Int -> }
                            addFriend.show()*/
                            i++
                        }
                        //Toast.makeText(applicationContext, "Éxito", Toast.LENGTH_LONG).show()
                    } else {
                        //Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_LONG).show()
                        Toast.makeText(applicationContext, response.code(), Toast.LENGTH_LONG).show()
                    }
                }
            })
        return friends1
    }

    private fun deleteFriend() {
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
        }
        friend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
        friend.show()
    }

    private fun addFriend() {
        val addFriend = AlertDialog.Builder(this)
        val customLayout: View = layoutInflater.inflate(R.layout.custom_alertdialog, null)
        addFriend.setView(customLayout)
        addFriend.setTitle(getString(R.string.code))
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
        menu.add(Menu.NONE, add, Menu.NONE, "Eliminar")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { add -> { deleteFriend(); return true }}
        return super.onContextItemSelected(item)
    }
}
