package com.app.hardik.studypdf

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.io.File

//PDFViewer displays the pdf on screen its an inbuilt pdfviewer in our app

class Pdfviewer : AppCompatActivity() {

    lateinit var progressBar: ProgressBar
    lateinit var pdfView: PDFView
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //this prevents user from any type of screen capture
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_pdfviewer)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        val path = intent.getStringExtra("path")
        progressBar = findViewById(R.id.pdfprogressBar)
        pdfView = findViewById<PDFView>(R.id.pdfView)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser!!.uid
        var watermark = " "
        //listener to get username and email of user for watermark
        databaseReference.child("Users").child("Students").child(uid).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                watermark = p0.child("Email").value.toString()+" \n "+p0.child("Username").value.toString()
            }

        })
        //create file
        val yourFile = File(path)
        Log.i("bingo",yourFile.toString())
        //pdfviewer function
        pdfView.fromFile(yourFile)
            .onDrawAll { canvas, pageWidth, pageHeight, displayedPage ->
                val paint = Paint()

                paint.style = Paint.Style.FILL
                paint.color = Color.GRAY
                paint.textSize = pageWidth*4/100
                paint.textAlign = Paint.Align.CENTER

                canvas.drawText(watermark, pageWidth/3, 50F, paint)
            }
            .load()


    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }


}