package com.app.hardik.studypdf

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

//Login page for user to get logged in our app
//here also user asked for Storage Permission

class LoginPage : AppCompatActivity() {
    lateinit var loginbtn : Button
    lateinit var newusrbtn : Button
    lateinit var forgotbtn: Button
    lateinit var emailtext : EditText
    lateinit var passwordtext : EditText
    lateinit var email: String
    lateinit var password: String
    lateinit var loggedindevice: String
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth
    private lateinit var spinner: ProgressBar
    var currentdate:Int = 0
    var currentmonth:Int = 0
    var currentyear:Int = 0
    var totaldays:Int = 0
    var remainingdays:Int = 0
    var dbmonth:Int = 0
    var dbyear:Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        //If App has not allowed Storage permission then ask for it
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1
            )

        }


        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        //Variables
        loginbtn = findViewById(R.id.login)
        newusrbtn = findViewById(R.id.newuser)
        forgotbtn = findViewById(R.id.forgotpwd)
        emailtext = findViewById(R.id.email)
        passwordtext = findViewById(R.id.password)
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference().child("Auth").child("AllUsers")

        //spinner
        spinner = findViewById<ProgressBar>(R.id.progressBar1)

        //Getting current calendar details
        val c = Calendar.getInstance()
        currentyear = c.get(Calendar.YEAR)
        currentmonth = c.get(Calendar.MONTH)
        currentdate = c.get(Calendar.DAY_OF_MONTH)
        totaldays = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        remainingdays = totaldays - currentdate
        Log.d("Days current",currentdate.toString())
        Log.d("Days total",totaldays.toString())
        Log.d("Days remain",remainingdays.toString())

        //On clicklistener for Forgot Button
        forgotbtn.setOnClickListener {
            startActivity(Intent(this@LoginPage,ForgotPWD::class.java))
        }

        // Code for Enter key
             passwordtext.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
                 if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                     //Perform Code
                     Login(v)
                     return@OnKeyListener true
                 }
                 false
             })

        //Code for hide keyboard if touched anywhere
        fun View.hideKeyboard() {
            val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
        }
        val pageView: View = findViewById(R.id.loginpage)
        pageView.setOnClickListener {
            it.hideKeyboard()
        }
        //Sign up Intent Fun
    }


//Login function ,automatically authenticated by firebase
    fun loginAccount (email:String,password:String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    // Sign in success, update UI with the signed-in user's information
                    //Log.i(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    onAuthSuccess(user!!)

                } else if(task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted."){
                    spinner.visibility = View.GONE      //Make spinner Gone
                    Toast.makeText(baseContext, "User Doesn't Exist.",
                        Toast.LENGTH_LONG).show()
                }
                else if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.") {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    // If sign in fails, display a message to the user.
                    Log.i("Error of",task.exception.toString())
                    Toast.makeText(baseContext, "Email Address is Invalid!",
                        Toast.LENGTH_LONG).show()
                }
                else if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password."){
                    spinner.visibility = View.GONE      //Make spinner Gone
                    Toast.makeText(baseContext, "Password is Incorrect!",
                        Toast.LENGTH_LONG).show()
                }
                else {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    // If sign in fails, display a message to the user.
                    Log.i("Error of", task.exception.toString())
                    Toast.makeText(baseContext, "Unknown Error Occurred",
                        Toast.LENGTH_LONG).show()
                    // ...
                }

                // ...
            }
    }

    //Display Admin or User panel according to logged in user's Role id
    //1 for User, 2 for admin
    //Also stores Information about total sessions
    fun onAuthSuccess(user: FirebaseUser) {
        databaseRef.child(user.uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val flag = p0.child("UserID").value.toString()
                //Log.i("flagID",flag)

                loggedindevice = p0.child("LoggedInDevice").value.toString()

                //If loggedin devices is more than three then don't save shared preference
                //So it'll not keep user logged in
                if(loggedindevice != "3") {
                    getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                        .putBoolean("isLoggedin", true).apply()
                    getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                        .putString("Flag", flag).apply()
                }

                if(flag=="1") {
                    //Check if month changed or not
                    dbmonth = p0.child("ChangedMonth").value.toString().toInt()
                    dbyear = p0.child("ChangedYear").value.toString().toInt()
                    if (dbmonth != currentmonth){
                        databaseRef.child(user.uid).child("LoggedInDevice").setValue("1")
                        databaseRef.child(user.uid).child("ChangedMonth").setValue(currentmonth)
                        databaseRef.child(user.uid).child("ChangedYear").setValue(currentyear)
                        getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putBoolean("isLoggedin", true).apply()
                        getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putString("Flag", flag).apply()
                        startActivity(Intent(this@LoginPage, Userdashboard::class.java))
                        finish()
                    }
                    //If month is same then check year
                    else if (dbmonth == currentmonth && dbyear != currentyear){
                        databaseRef.child(user.uid).child("LoggedInDevice").setValue("1")
                        databaseRef.child(user.uid).child("ChangedMonth").setValue(currentmonth)
                        databaseRef.child(user.uid).child("ChangedYear").setValue(currentyear)
                        getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putBoolean("isLoggedin", true).apply()
                        getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit().putString("Flag", flag).apply()
                        startActivity(Intent(this@LoginPage, Userdashboard::class.java))
                        finish()
                    }
                    else if (loggedindevice == "3"){
                        val builder = AlertDialog.Builder(this@LoginPage)
                        builder.setTitle("Warning- Exceed Device Limit")
                        builder.setMessage("If want to LOGIN on this Device \n"+"\nYou have to LOGOUT from another devices\n" +
                                "\t\t\t\t\t\t\t\tOr\n" +
                                "Try again after "+remainingdays+" days")
                        builder.setPositiveButton("Continue") { dialog, which ->
                            //Toast.makeText(applicationContext, "continuar", Toast.LENGTH_SHORT).show()
                        }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                    else if (loggedindevice == "0"){
                        databaseRef.child(user.uid).child("LoggedInDevice").setValue("1")
                        startActivity(Intent(this@LoginPage, Userdashboard::class.java))
                        finish()
                    }
                    else if (loggedindevice == "1"){
                        databaseRef.child(user.uid).child("LoggedInDevice").setValue("2")
                        startActivity(Intent(this@LoginPage, Userdashboard::class.java))
                        finish()
                    }
                    else if (loggedindevice == "2"){
                        databaseRef.child(user.uid).child("LoggedInDevice").setValue("3")
                        startActivity(Intent(this@LoginPage, Userdashboard::class.java))
                        finish()
                    }
                }
                else if (flag=="2"){
                    startActivity(Intent(this@LoginPage, Admindashboard::class.java))
                    finish()
                }
            }
        })

    }
    //move to signup
    fun newuser (v: View){
        startActivity(Intent(this,SignUp::class.java))
        finish()
    }

    fun Login (v: View){
        if (emailtext.text.isNullOrEmpty() || passwordtext.text.isNullOrEmpty())
        {
            Toast.makeText(this,"You can't leave a field empty!", Toast.LENGTH_LONG).show()
        }

        else {
            email = emailtext.text.toString()
            password = passwordtext.text.toString()
            spinner.visibility = View.VISIBLE       //Make spinner visible
            loginAccount(email,password)
        }

    }

    //Code for informing and closing app
    private var exit = false
    override fun onBackPressed() {
        if (exit) {
            finish() // finish activity
        } else {
            Toast.makeText(
                this, "Press Back again to Exit.",
                Toast.LENGTH_SHORT
            ).show()
            exit = true
            Handler().postDelayed(Runnable { exit = false }, 3 * 1000)
        }
    }

    //Code for granting permission from user for storage
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.count() > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    this,
                    "Storage Permission Granted",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else {
                Toast.makeText(
                    this,
                    "Storage Permission Denied",
                    Toast.LENGTH_SHORT
                )
                    .show();
            }
        }
    }




}
