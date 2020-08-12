package com.app.hardik.studypdf

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import java.io.File

//This Activity is for viewing demo version of PDF before even buying it.
//This only shows first page of PDF there is button to buy full version of pdf

class Demopdfviewer : AppCompatActivity() {
    lateinit var pdfView: PDFView
    lateinit var full: Button
    lateinit var pdfname: String
    lateinit var price: String
    lateinit var url: String
    lateinit var key: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //This statement causes user to prohibit from any type of screen capturing
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        setContentView(R.layout.activity_demopdfviewer)
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //PDFViewer
        pdfView = findViewById(R.id.demopdf)
        full = findViewById(R.id.fullbutton)
         pdfname = intent.getStringExtra("pdfname2")
        price = intent.getStringExtra("price")
        url = intent.getStringExtra("url")
        key = intent.getStringExtra("key")
        Log.i("pdfname2 list",pdfname)

        //demopdf is already downloaded and we get its file location path from this intent
        val path = intent.getStringExtra("path")

        //Create File for that path
        val yourFile = File(path)
        Log.i("demo",yourFile.toString())
        //Load PDF file
        pdfView.fromFile(yourFile)
            .pages(0)
            .load()

    }
    //Onclick function for buy full version button
    fun onClick (v: View){
        val intent = Intent(this,Paymentpage::class.java)
        intent.putExtra("pdfname",pdfname)
        intent.putExtra("price",price)
        intent.putExtra("url",url)
        intent.putExtra("key",key)
        startActivity(intent)
    }


    override fun onBackPressed() {
            finish()
        }
    }
