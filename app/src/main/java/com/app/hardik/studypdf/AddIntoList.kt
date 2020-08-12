package com.app.hardik.studypdf

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class AddIntoList : AppCompatActivity() {

    lateinit var Departmentval: String
    lateinit var Streamval: String
    lateinit var Semesterval: String
    lateinit var Subjectval: String
    lateinit var finalpath: String

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_into_list)

        val Department = findViewById<EditText>(R.id.DepeditText)
        val Stream = findViewById<EditText>(R.id.StreameditText)
        val Semester = findViewById<EditText>(R.id.SemeditText)
        val Subject = findViewById<EditText>(R.id.SubeditText)
        val Done = findViewById<Button>(R.id.Done)
      //  val Delete = findViewById<Button>(R.id.Delete)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseRef = database.getReference()
        Done.setOnClickListener {

            Departmentval = Department.text.toString().trim()
            Streamval = Stream.text.toString().trim()
            Semesterval = Semester.text.toString().trim()
            Subjectval = Subject.text.toString().trim()
            finalpath = "StreamList/"+Streamval+"/"+Departmentval+"/"+Semesterval+"/"+Subjectval
            if(Departmentval.isNullOrEmpty() || Streamval.isNullOrEmpty() || Semesterval.isNullOrEmpty() || Subjectval.isNullOrEmpty() ){
                Toast.makeText(this,"You Can't Leave a Field Empty!",Toast.LENGTH_SHORT).show()
            }
            else {
            databaseRef.child("StreamList").child(Streamval).child(Departmentval)
                .child(Semesterval).child(Subjectval).setValue(Subjectval)
                databaseRef.child("SubjectPath").child(Subjectval).setValue(finalpath)
            Toast.makeText(this,"Added Successfully",Toast.LENGTH_LONG).show()
                }

        }

    }
}
