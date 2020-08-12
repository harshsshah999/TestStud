package com.app.hardik.studypdf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.google.firebase.database.*

//This Activity is for Delete PDF section where an admin can Remove any PDF available in our database

class Deletepdf : AppCompatActivity() {

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var parentname : String = ""
    var path : String = ""
    var enc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_deletepdf)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()

        val arrayList: ArrayList<String> = ArrayList()
        var arrayAdapter: ArrayAdapter<String?>
        val listView = findViewById<ListView>(R.id.pdflist)
        arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_list_item_1,
            arrayList as List<String?>?
        )

        //getting list of all available pdfs
        databaseReference.child("Links").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val pdfname = p0.key.toString()
                arrayList.add(pdfname)

                listView.adapter = arrayAdapter
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                arrayAdapter.notifyDataSetChanged()

            }

        })

        listView.setOnItemClickListener(object : AdapterView.OnItemClickListener{
            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                //deleting selected pdf file from both "Links" tree
                databaseReference.child("Links").child(arrayList.get(position)).setValue(null)
                Toast.makeText(applicationContext,"PDF is Deleted Successfully",Toast.LENGTH_LONG).show()
                finish()
                startActivity(intent)
            }

        })


    }
}