package com.app.hardik.studypdf

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

//This is where admin selects file from internal storage
//set its name and price and uploads it to firebase storage

class Uploadsection : AppCompatActivity() {
    lateinit var choose: Button
    lateinit var firebaseDatabase: FirebaseDatabase
    lateinit var firebaseStorage: StorageReference
    lateinit var databaseReference: DatabaseReference
    lateinit var dbrefr : DatabaseReference
    val PICK_PDF_CODE = 2342
    //val PICK_PDF_CODE = 2
    lateinit var textViewStatus: TextView
    lateinit var progressBar: ProgressBar
    lateinit var editTextFilename: EditText
    lateinit var filetitle: String
    lateinit var path: String
    lateinit var upload: Button
    lateinit var price: EditText
    lateinit var priceval: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_uploadsection)

        choose = findViewById(R.id.chooser)

        textViewStatus = findViewById(R.id.textViewStatus)
        progressBar = findViewById(R.id.progressbarupload)
        price = findViewById(R.id.price)
        upload = findViewById(R.id.upload)
        editTextFilename = findViewById(R.id.editTextFileName)
        filetitle = intent.getStringExtra("name")
        path = intent.getStringExtra("path") + "/"
        editTextFilename.setText(filetitle)
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference()
        dbrefr = firebaseDatabase.getReference()
        firebaseStorage = FirebaseStorage.getInstance().getReference()
        upload.isEnabled = false
    }

    //choosefile button
    fun choosefile(v: View) {
        getPDF()
    }
    //function makes user to browse internal storage to choose appropriate file
    private fun getPDF() {
        //storage permission check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            );
            return
        }

        //creating an intent for file chooser
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PICK_PDF_CODE)

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        //when the user choses the file
        if (requestCode == PICK_PDF_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            //if a file is selected
            if (data.data != null) {
                //uploading the file
                upload.isEnabled = true
                upload.setBackgroundResource(R.drawable.my_bg_btn)
                upload.setTextColor(Color.rgb(255,160,0))
                Toast.makeText(this,"Type Appropriate Name for your File!",Toast.LENGTH_SHORT).show()
                upload.setOnClickListener{
                    if(editTextFilename.text.isNullOrBlank() || price.text.isNullOrBlank()){
                        Toast.makeText(this,"You can't leave fields empty!",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        //this listener checks if pdf file name is already exists
                        dbrefr.child("Links").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {

                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.hasChild(editTextFilename.text.toString())){
                                    Toast.makeText(this@Uploadsection,"File Name Already exists",Toast.LENGTH_LONG).show()
                                }
                                else {
                                    priceval = price.text.toString().trim()
                                    uploadFile(data.data)
                                }
                            }

                        })

                    }
                }
            } else {
                Toast.makeText(this, "No file chosen", Toast.LENGTH_SHORT).show()
            }
        }
    }
    //upload task
    private fun uploadFile (data: Uri){
        progressBar.visibility = View.VISIBLE
        val filename = editTextFilename.getText().toString().trim()
        val sRef: StorageReference =
            firebaseStorage.child(path + System.currentTimeMillis() + ".pdf")
        var uploadTask = sRef.putFile(data)
        uploadTask
            .continueWithTask{task ->
            if(!task.isSuccessful){
                task.exception?.let {
                    throw it
                }
            }
            sRef.downloadUrl
        }
            .addOnCompleteListener{task ->
                val key = databaseReference.push().getKey()!!
                if(task.isSuccessful){
                val downloadUri = task.result
                val upload = Upload(
                    filename,
                    downloadUri.toString(),priceval,filetitle,key
                )
                textViewStatus.text = "File Uploaded Successfully"
                progressBar.visibility = View.GONE

                    //store details related to uploaded file in firebase database
                    databaseReference.child("Links").child(filename).setValue(upload)
                    databaseReference.child("Uploads").child(path).child(key).setValue(upload)

            }
            else{
                Toast.makeText(this,"Error",Toast.LENGTH_SHORT).show()
            }
        }
        //shows progress of uploading
        uploadTask.addOnProgressListener { taskSnapshot ->
            val progress = ((100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount).toInt()
            textViewStatus.setText("$progress % Uploading...")
        }
    }
}


