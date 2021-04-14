package eina.unizar.unozar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Profile : AppCompatActivity() {

    var session = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        session = intent.getStringExtra("password").toString()
        // getStatistics
    }

    /*override fun onStart(){
        super.onStart()
        if(!SharedPrefManager.getInstance(this).isLoggedIn){
            val intent = Intent(applicationContext,Login::class.java/*Actividad*/)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }*/

    fun goToChangeEmail(view: View) {
        val intent = Intent(this, EmailChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    fun goToChangePassword(view: View) {
        val intent = Intent(this, PasswordChange::class.java)
        intent.putExtra("session", session)
        startActivity(intent)
    }

    fun deleteAccount(view: View) {
        RetrofitClient.instance.userDeleteAccount(session)
            .enqueue(object: Callback<BasicResponse> {
                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {
                    Toast.makeText(applicationContext, "1: " + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {
                    Toast.makeText(
                        applicationContext,
                        "2: " + response.body()?.message,
                        Toast.LENGTH_LONG
                    ).show()
                    //val intent = Intent(this, Register::class.java)
                    //startActivity(intent)
                }
            })
    }
}
