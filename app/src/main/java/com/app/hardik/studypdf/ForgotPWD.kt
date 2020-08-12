package com.app.hardik.studypdf

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

//This Activity is for User to reset their password

class ForgotPWD : AppCompatActivity() {
    lateinit var femail : EditText
    lateinit var  fsend : Button
    var emaillist = mutableListOf<Any>()
    private lateinit var databaseRef: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_p_w_d)
        femail = findViewById(R.id.Femail)
        fsend = findViewById(R.id.Fsend)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference()
        databaseRef.child("Users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                //Saves all registered emails in email list
                p0.children.forEach {
                    Log.d("Emails",it.toString())
                    it.children.mapNotNullTo(emaillist) {
                        it.child("Email").value
                    }
                }
            }
        })

        //Sends email to change passworf
        fsend.setOnClickListener {
            if(femail.text.isNullOrEmpty()){
                Toast.makeText(this@ForgotPWD,"You can't leave this field Empty !",Toast.LENGTH_SHORT).show()
            }
            else{
                auth.sendPasswordResetEmail(femail.text.toString())
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this@ForgotPWD,"Email Sent Successfully",Toast.LENGTH_SHORT).show()

                            //Opens email app
                            Handler().postDelayed(Runnable {
                                val intent =
                                    packageManager.getLaunchIntentForPackage("com.google.android.gm")
                                startActivity(intent)
                                finish()
                            }, 2000)
                        }
                        else {
                            //If unsuccessful to send email then Check if the email is registered or not
                            var notregister = true
                            for (i in emaillist){
                                if (i.toString() == femail.text.toString()){ notregister == false}
                            }
                            if (notregister){
                                Toast.makeText(this@ForgotPWD,femail.text.toString()+" is not registered",Toast.LENGTH_SHORT).show()
                            }
                            else{
                                Toast.makeText(this@ForgotPWD,"Failed \nTry Again !",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }
    }
}