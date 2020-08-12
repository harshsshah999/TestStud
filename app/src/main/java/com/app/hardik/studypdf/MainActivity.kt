package com.app.hardik.studypdf

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.*

//Splashscreen for our app also checks if user is already logged in

class MainActivity : AppCompatActivity() {

            //Global declaration of variables
            lateinit var anim_fade_in : Animation
            lateinit var logo : ImageView
            var currentmonth:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Declaration of sharedpreference for first time run
        val isFirstRun = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getBoolean("isFirstRun", true)

        //Declaration of sharedpreference for keeping logged in
        val isLoggedin = getSharedPreferences("Loggedin", Context.MODE_PRIVATE).getBoolean("isLoggedin", false)
        val flag = getSharedPreferences("Loggedin",Context.MODE_PRIVATE).getString("Flag","Null")

        //Declaration of sharedpreference for saving last month details
        val lastmonth = getSharedPreferences("Loggedin", Context.MODE_PRIVATE).getInt("lastmonth", 0)
        val c = Calendar.getInstance()

        currentmonth = c.get(Calendar.MONTH)
        if(isFirstRun){
            getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putInt("lastmonth", currentmonth).apply()
        }
        else{
            //if month changed then set logged in details to start and go to login page
            if(currentmonth != lastmonth){
                getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putInt("lastmonth", currentmonth).apply()
                getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putBoolean("isLoggedin", false).apply()
                getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putString("Flag","Null").apply()
               // startActivity(Intent(this@MainActivity,LoginPage::class.java))
            }
        }


            //Assigning ID's of Views to Variables
            logo = findViewById(R.id.logo)

            //Create Animation
            anim_fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in)

            anim_fade_in.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }
            override fun onAnimationEnd(animation: Animation) {

                //Shared Preference Used here
                if (isFirstRun) {
                    //Open acivity only once
                    startActivity(Intent(this@MainActivity, Introscreen::class.java))
                    finish()
                }
                //Else opens next activity
                else{
                    if (isLoggedin){
                        if (isOnline(this@MainActivity)){
                            if (flag == "1"){
                                startActivity(Intent(this@MainActivity,Userdashboard::class.java))
                                finish()
                            }
                            else if (flag == "2"){
                                startActivity(Intent(this@MainActivity,Admindashboard::class.java))
                                finish()
                            }
                            else{
                                startActivity(Intent(this@MainActivity,LoginPage::class.java))
                                finish()
                            }

                        }
                        else{
                            if (flag == "1"){
                                startActivity(Intent(this@MainActivity,Userdashboard::class.java))
                                finish()
                            }
                            else if (flag == "2"){
                                startActivity(Intent(this@MainActivity,Admindashboard::class.java))
                                finish()
                            }
                            else{
                                startActivity(Intent(this@MainActivity,LoginPage::class.java))
                                finish()
                            }
                            Toast.makeText(this@MainActivity,"You are offline",Toast.LENGTH_LONG).show()
                        }

                    }
                    else{
                        startActivity(Intent(this@MainActivity, LoginPage::class.java))
                        finish()
                    }
                }

                getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).edit()
                    .putBoolean("isFirstRun", false).apply()

            }
            override fun onAnimationRepeat(animation: Animation) {
            }
        })

            //Trigger Animation
        logo.setImageResource(R.drawable.finalappicon)
        logo.startAnimation(anim_fade_in)
    }

}
//Function to check user is online or not
fun isOnline(context: Context): Boolean {
    val connectivity =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if(connectivity!=null){
    val info = connectivity.allNetworkInfo
        if(info!=null){
            for(i in 0..info.size - 1){
                if(info[i].state == NetworkInfo.State.CONNECTED){
                    return true
                }
            }
        }
    }
    return false
}
