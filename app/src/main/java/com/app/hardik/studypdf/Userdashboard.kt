package com.app.hardik.studypdf

import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

//This Activity is for UserDashboard , it has not much use only to hold and display fragments

class Userdashboard : AppCompatActivity() {
    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var userauth : FirebaseAuth
    lateinit var name : String
    lateinit var helloname : TextView
    var user : FirebaseUser? = null
    lateinit var bottomNavigation: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_userdashboard)

        helloname = findViewById(R.id.welcome)
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference()
        userauth = FirebaseAuth.getInstance()
        user = userauth.currentUser
        //listener to get current users name
        databaseReference.child("Users").child("Students").child(user!!.uid).child("Username").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                 name = p0.value.toString()
                helloname.text ="Welcome " + name
            }
        })
        //Initializing Navigation bar items
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener)
        helloname.visibility = View.GONE
        openFragment(UserHomeFragment.newInstance("", ""))

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
                    R.id.navigation_userhome -> {
                        openFragment(UserHomeFragment.newInstance("", ""))
                        return true
                    }
                    R.id.navigation_search -> {
                        openFragment(SearchFragment.newInstance("", ""))
                        return true
                    }
                    R.id.navigation_mypdf -> {
                        openFragment(MyPdfFragment.newInstance("", ""))
                        return true
                    }
                    R.id.navigation_profile -> {
                        openFragment(UserProfileFragment.newInstance("", ""))
                        return true
                    }
                }
                return false
            }
        }

    //Code for informing and closing app
    private var exit = false
    override fun onBackPressed() {
        if (exit) {
            finish() // finish activity
        } else {
            bottomNavigation.checkItem(R.id.navigation_userhome)
            openFragment(UserHomeFragment.newInstance("",""))
            Toast.makeText(
                this, "Press Back again to Exit.",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed(Runnable { exit = false }, 3 * 1000)
        }
    }
    //function to set Home icon checked by default
    internal fun BottomNavigationView.checkItem(actionId: Int) {
        menu.findItem(actionId)?.isChecked = true
    }
}
