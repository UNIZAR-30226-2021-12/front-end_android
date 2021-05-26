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
import server.request.IdRequest
import server.request.TokenRequest
import server.response.FriendsListResponse
import server.response.PlayerInfo
import server.response.TokenResponse

class Friends : AppCompatActivity() {
    private var normalCode = 73

    private lateinit var session: String
    private lateinit var friends: ArrayList<FriendInfo>
    private lateinit var adapter: FriendsListAdapter
    private var avatars = arrayListOf(
        R.drawable.test_user,
        R.drawable.robotia,
        R.drawable.avatar2,
        R.drawable.avatar3,
        R.drawable.avatar4
    )

    private val profile = Menu.FIRST
    private val delete = Menu.FIRST+1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        session = intent.getStringExtra("session").toString()
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
                    Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                } override fun onResponse(call: Call<FriendsListResponse>, response: Response<FriendsListResponse>) {
                    if (response.code() == 200) {
                        session = response.body()?.token.toString()
                        var i = 0
                        friends = ArrayList()
                        while (i < response.body()!!.avatarIds!!.size) {
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
                        Toast.makeText(applicationContext, getString(R.string.bad_creation_response) + response.code(), Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                    } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                        if (response.code() == 200) {
                            session = response.body()?.token.toString()
                            getFriendsRequest()
                        } else {
                            Toast.makeText(applicationContext, getString(R.string.bad_friend_add_response), Toast.LENGTH_SHORT).show()
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
        menu.add(Menu.NONE, profile, Menu.NONE, "Ver perfil")
        menu.add(Menu.NONE, delete, Menu.NONE, "Eliminar")
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            delete -> {
                val info = item.menuInfo as AdapterContextMenuInfo
                val friend = AlertDialog.Builder(this)
                friend.setTitle(getString(R.string.delete_friend_message))
                friend.setPositiveButton(getString(R.string.delete_button)) { _: DialogInterface, _: Int ->
                    RetrofitClient.instance.deleteFriend(AddFriendRequest(session, friends[info.position].id.toString()))
                        .enqueue(object : Callback<TokenResponse> {
                            override fun onFailure(call: Call<TokenResponse>, t: Throwable) {
                                Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                            } override fun onResponse(call: Call<TokenResponse>, response: Response<TokenResponse>) {
                                if (response.code() == 200) {
                                    session = response.body()?.token.toString()
                                    getFriendsRequest()
                                } else {
                                    Toast.makeText(applicationContext, getString(R.string.bad_delete_friend_response), Toast.LENGTH_SHORT).show()
                                }
                            }
                        })
                }
                friend.setNegativeButton(getString(R.string.cancel)) { _: DialogInterface, _: Int ->}
                friend.show()
                return true
            }
            profile -> {
                val info = item.menuInfo as AdapterContextMenuInfo
                RetrofitClient.instance.readPlayer(IdRequest(friends[info.position].id.toString()))
                    .enqueue(object : Callback<PlayerInfo> {
                        override fun onFailure(call: Call<PlayerInfo>, t: Throwable) {
                            Toast.makeText(applicationContext, getString(R.string.no_response), Toast.LENGTH_SHORT).show()
                        } override fun onResponse(call: Call<PlayerInfo>, response: Response<PlayerInfo>) {
                            if (response.code() == 200) {
                                val intent = Intent(this@Friends, FriendProfile::class.java)
                                intent.putExtra("avatar", response.body()!!.avatarId)
                                intent.putExtra("alias", response.body()!!.alias)
                                intent.putExtra("email", response.body()!!.email)
                                intent.putExtra("total_matches", (response.body()!!.privateTotal) + response.body()!!.publicTotal).toString()
                                intent.putExtra("total_wins", (response.body()!!.privateWins + response.body()!!.publicWins).toString())
                                intent.putExtra("friend_matches", response.body()!!.privateTotal.toString())
                                intent.putExtra("friend_wins", response.body()!!.privateWins.toString())
                                startActivityForResult(intent, normalCode)
                            } else {
                                Toast.makeText(applicationContext, getString(R.string.bad_friend_profile_response), Toast.LENGTH_SHORT).show()
                            }
                        }
                    })
            }
        }
        return super.onContextItemSelected(item)
    }

    override fun onBackPressed() {
        val intent = Intent().apply { putExtra("session", session) }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}
