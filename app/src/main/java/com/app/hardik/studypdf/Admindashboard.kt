package com.app.hardik.studypdf

import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView

lateinit var bottomNavigation: BottomNavigationView

//This Activity is for AdminDashboard , it has not much use only to hold and display fragments

class Admindashboard : AppCompatActivity() {

    lateinit var nav_menu: Menu
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admindashboard)
        //Initializing Navigation bar items
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        bottomNavigation.checkItem(R.id.navigation_home)
        nav_menu = bottomNavigation.menu
        nav_menu.findItem(R.id.navigation_settings).setVisible(false)
        nav_menu.findItem(R.id.navigation_upload).setVisible(false)
        nav_menu.findItem(R.id.navigation_home).setVisible(false)
        nav_menu.findItem(R.id.navigation_list).setVisible(false)
        nav_menu.findItem(R.id.navigation_users).setVisible(false)
        //Call default fragment to open
        openFragment(HomeFragment.newInstance("", ""));

    }
    //function to set Home icon checked by default
    internal fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }
    //This function opens fragments
    fun openFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    //Navigation bar listener
   var navigationItemSelectedListener: BottomNavigationView.OnNavigationItemSelectedListener? =
       object : BottomNavigationView.OnNavigationItemSelectedListener {
          override fun onNavigationItemSelected(item: MenuItem): Boolean {
               when (item.getItemId()) {
                   R.id.navigation_settings -> {
                       openFragment(SettingFragment.newInstance("", ""))
                       return true
                   }
                   R.id.navigation_upload -> {
                       openFragment(UploadFragment.newInstance("", ""))
                       return true
                   }
                   R.id.navigation_home -> {
                       openFragment(HomeFragment.newInstance("", ""))

                       return true
                   }
                   R.id.navigation_list -> {
                       openFragment(ListFragment.newInstance("", ""))
                       return true
                   }
                   R.id.navigation_users -> {
                       openFragment(UsersFragment.newInstance("", ""))
                       return true
                   }
               }
               return false
           }
       }
    private var exit = false

    //Back pressed
    override fun onBackPressed() {
        if (exit) {
            finish() // finish activity
        } else {
            bottomNavigation.checkItem(R.id.navigation_home)
            openFragment(HomeFragment.newInstance("",""))
            Toast.makeText(
                this, "Press Back again to Exit.",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed(Runnable { exit = false }, 3 * 1000)
        }
    }
}
