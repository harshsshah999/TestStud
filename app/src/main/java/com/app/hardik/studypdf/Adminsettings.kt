package com.app.hardik.studypdf

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*


//This Activity is for Block User section where an admin can block/unblock any user

class Adminsettings : AppCompatActivity() {

    //Firebase variables declaration
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    //This dictionary stores uuid for each user
    val keyMap = hashMapOf<String,String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_adminsettings)
        //Firebase variables initialization
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        //List view
        val arrayList: ArrayList<String> = ArrayList()
        var arrayAdapter: ArrayAdapter<String?>
        val listView = findViewById<ListView>(R.id.userslist)
        //Listening all "Students" users.
        databaseReference.child("Users").child("Students").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //username of each user
                val user = p0.child("Username").value.toString()
               if (p0.child("isBlocked").exists()) {
                   if (p0.child("isBlocked").value!!.equals(true)) {
                       keyMap.put(/*username as key*/ p0.child("Username").value.toString()+ " :- BLOCKED!",/*uuid as value*/p0.key.toString())
                       arrayList.add(user + " :- BLOCKED!")
                   } else {
                       keyMap.put(/*username as key*/ p0.child("Username").value.toString(),/*uuid as value*/p0.key.toString())
                       arrayList.add(user)
                   }
               }
                else {
                   keyMap.put(/*username as key*/ p0.child("Username").value.toString(),/*uuid as value*/p0.key.toString())
                   arrayList.add(user)
               }
                //adapter
                arrayAdapter = ArrayAdapter(
                    applicationContext,
                    android.R.layout.simple_list_item_1,
                    arrayList as List<String?>?
                )
                listView.adapter = arrayAdapter
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })

        //Onclick listener on listview items
            listView.setOnItemClickListener(object: AdapterView.OnItemClickListener{
                override fun onItemClick(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val uid = keyMap.get(arrayList.get(position))!!
                    //listening on blocked users list
                    //if a user is already blocked then it gets unblocked otherwise block
                    databaseReference.child("blockedUsers").addListenerForSingleValueEvent(object: ValueEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            if (p0.hasChild(uid)){
                                databaseReference.child("blockedUsers").child(uid).setValue(null)
                                databaseReference.child("Users").child("Students").child(uid).child("isBlocked").setValue(false)
                                Toast.makeText(this@Adminsettings,"User is Successfully Unblocked",Toast.LENGTH_LONG).show()
                                view!!.setBackgroundColor(Color.WHITE)
                                finish()
                                startActivity(intent)
                            }
                            else{
                                databaseReference.child("blockedUsers").child(uid).setValue(true)
                                databaseReference.child("Users").child("Students").child(uid).child("isBlocked").setValue(true)
                                Toast.makeText(this@Adminsettings,"User is Successfully Blocked",Toast.LENGTH_LONG).show()
                                view!!.setBackgroundColor(Color.RED)
                                finish()
                                startActivity(intent)
                            }
                        }

                    })
                }

            })

    }
}