package com.app.hardik.studypdf

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

//This Activity is for Changing Cost of any pdfs available in our database

class Changecost : AppCompatActivity() {

    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var parentname : String = ""
    var path : String = ""
    var enc: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_changecost)

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

        //This listener will listen all Pdfs uploaded in database and will populate them in list view
        databaseReference.child("Links").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val pdfname = p0.key.toString() //name of pdf
                val price = p0.child("price").value.toString() //price
                arrayList.add(pdfname+"."+" \n "+"₹ "+price) //adding in list along with current price

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
                //Alert dialogue to display after any item is clicked
                //with an edittext to enter new price
                val alert: AlertDialog.Builder = AlertDialog.Builder(this@Changecost)
                var edittext = EditText(this@Changecost)
                alert.setMessage("Enter New Cost")
                alert.setView(edittext)
                alert.setPositiveButton(
                    "Change"
                ) { dialog, whichButton ->
                    val newcost = edittext.text.toString()
                    val itemname = arrayList.get(position).substringBefore("."," ")
                    //updating pdf price in database tree "Links"
                    databaseReference.child("Links").child(itemname).child("price").setValue(newcost)
                    Toast.makeText(
                        applicationContext,
                        "PDF Price changed successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                    startActivity(intent)
                }
                alert.setNegativeButton(
                    "Cancel"
                ) { dialog, whichButton ->
                    // Canceled.
                }
                alert.show()
            }

        })


    }
}