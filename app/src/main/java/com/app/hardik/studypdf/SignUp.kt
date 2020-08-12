package com.app.hardik.studypdf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

//Sign up page to register in our app

class SignUp : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference
    private lateinit var emailtext: EditText
    private lateinit var passwordtext: EditText
    private lateinit var confpasstext: EditText
    private lateinit var nametext: EditText
    private lateinit var phonenotext: EditText
    private lateinit var signup: Button
    private lateinit var olduser: Button
    lateinit var email: String
    lateinit var password: String
    lateinit var name: String
    lateinit var phoneno: String
    var currentmonth:Int = 0
    var currentyear:Int = 0
    private lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference()


        //assigning views
        emailtext = findViewById(R.id.email)
        passwordtext = findViewById(R.id.password)
        confpasstext = findViewById(R.id.confpassword)
        nametext = findViewById(R.id.fullname)
        signup = findViewById(R.id.signupstd)
        olduser = findViewById(R.id.olduser)
        phonenotext = findViewById(R.id.phoneno)

        //spinner
        spinner = findViewById<ProgressBar>(R.id.progressBar1)

        val c = Calendar.getInstance()
         currentyear = c.get(Calendar.YEAR)
         currentmonth = c.get(Calendar.MONTH)



        //Code for hide keyboard if touched anywhere
        fun View.hideKeyboard() {
            val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
        }
        val pageView: View = findViewById(R.id.signuppage)
        pageView.setOnClickListener {
            it.hideKeyboard()
        }

    }

    //Creates authenticated account in firebase
    fun createAccount(email: String, password: String, name: String, flag: Int) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    // Sign in success, update UI with the signed-in user's information
                    //Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    onAuthSuccess(user!!, name, flag)

                } else if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The email address is badly formatted.") {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    // If sign in fails, display a message to the user.
                   // Log.i("Error of", task.exception.toString())
                    Toast.makeText(
                        baseContext, "Email Address is Invalid!",
                        Toast.LENGTH_LONG
                    ).show()
                } else if (task.exception.toString() == "com.google.firebase.auth.FirebaseAuthWeakPasswordException: The given password is invalid. [ Password should be at least 6 characters ]") {
                    spinner.visibility = View.GONE      //Make spinner Gone
                   // Log.i("Error of", task.exception.toString())
                    Toast.makeText(
                        baseContext, "Password is too Small! (At least 6 characters)",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    spinner.visibility = View.GONE      //Make spinner Gone
                    Toast.makeText(baseContext, "Unknown Error Occurred!", Toast.LENGTH_LONG).show()
                }

                // ...
            }

    }
        //add user details in Users and Auth firebase tree
    fun onAuthSuccess(user: FirebaseUser, name: String, flag: Int) {
            if(flag == 1){
                databaseRef.child("Users").child("Students").child(user.uid).child("Username").setValue(name)
                databaseRef.child("Users").child("Students").child(user.uid).child("UserID").setValue(flag)
                databaseRef.child("Users").child("Students").child(user.uid).child("Email").setValue(email)
                databaseRef.child("Users").child("Students").child(user.uid).child("PhoneNo").setValue(phoneno)
                databaseRef.child("Auth").child("AllUsers").child(user.uid).child("Username").setValue(name)
                databaseRef.child("Auth").child("AllUsers").child(user.uid).child("UserID").setValue(flag)
                databaseRef.child("Auth").child("AllUsers").child(user.uid).child("LoggedInDevice").setValue("1")
                databaseRef.child("Auth").child("AllUsers").child(user.uid).child("ChangedMonth").setValue(currentmonth)
                databaseRef.child("Auth").child("AllUsers").child(user.uid).child("ChangedYear").setValue(currentyear)
                startActivity(Intent(this,Userdashboard::class.java))
                finish()
            }
    }
//move to login
    fun olduser(v: View) {
        startActivity(Intent(this, LoginPage::class.java))
        finish()
    }
//signup function for student
    fun Signupstd(v: View) {
        val flag = 1
        if (emailtext.text.isNullOrEmpty() || passwordtext.text.isNullOrEmpty() || confpasstext.text.isNullOrEmpty() || nametext.text.isNullOrEmpty() || phonenotext.text.isNullOrEmpty()) {
            Toast.makeText(this, "You can't Leave a Field Empty!", Toast.LENGTH_LONG).show()
        } else if (passwordtext.text.toString() != confpasstext.text.toString()) {
            Toast.makeText(this, "Passwords Doesn't Match!", Toast.LENGTH_LONG).show()
        } else {
            spinner.visibility = View.VISIBLE       //Make spinner visible
            email = emailtext.text.toString()
            password = passwordtext.text.toString()
            name = nametext.text.toString()
            phoneno = phonenotext.text.toString()

            createAccount(email, password, name, flag)
        }
    }

    //signup function for admin (Currently moved to diffrent app)
    fun Signupadm(v: View) {
            val flag = 2

            if (emailtext.text.isNullOrEmpty() || passwordtext.text.isNullOrEmpty() || confpasstext.text.isNullOrEmpty() || nametext.text.isNullOrEmpty()) {
                Toast.makeText(this, "You can't Leave a Field Empty!", Toast.LENGTH_LONG).show()
            } else if (passwordtext.text.toString() != confpasstext.text.toString()) {
                Toast.makeText(this, "Passwords Doesn't Match!", Toast.LENGTH_LONG).show()
            } else {
                spinner.visibility = View.VISIBLE       //Make spinner visible
                email = emailtext.text.toString()
                password = passwordtext.text.toString()
                name = nametext.text.toString()
                phoneno = phonenotext.text.toString()
                createAccount(email, password, name, flag)
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
}
