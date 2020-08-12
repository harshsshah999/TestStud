package com.app.hardik.studypdf

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.razorpay.Checkout
import com.razorpay.PaymentResultListener
import org.json.JSONObject
import java.io.File
import java.util.*

//This is Payment form in which user enter his name email phone number
//its necessary for razorpay payment page

class Paymentpage : AppCompatActivity(), PaymentResultListener {
    lateinit var fname: EditText
    lateinit var emailadd: EditText
    lateinit var phoneno: EditText
    lateinit var subname: TextView
    lateinit var amount: TextView
    lateinit var pay: Button
    lateinit var pdfname: String
    lateinit var price: String
    lateinit var url: String
    lateinit var encryptname: String
    lateinit var storageRef: StorageReference
    lateinit var databaseReference: DatabaseReference
    lateinit var storage: FirebaseStorage
    lateinit var progressDialog: ProgressDialog
     var path : String = ""
    var user : FirebaseUser? = null
    lateinit var userauth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_paymentpage)

        //preloading razorpay checkout
        Checkout.preload(getApplicationContext())

        fname = findViewById(R.id.fullname)
        emailadd = findViewById(R.id.email)
        phoneno = findViewById(R.id.phone)
        subname = findViewById(R.id.subject)
        amount = findViewById(R.id.amount)
        pay = findViewById(R.id.pay)
        progressDialog = ProgressDialog(this)
        pdfname = intent.getStringExtra("pdfname")
        price = intent.getStringExtra("price")
        url = intent.getStringExtra("url")
        encryptname = intent.getStringExtra("key")
        subname.text = pdfname
        amount.text = "₹ "+price
        storage = Firebase.storage
        storageRef = storage.reference
        userauth = FirebaseAuth.getInstance()
        progressDialog.setTitle("Downloading PDF...");
        progressDialog.setCanceledOnTouchOutside(false)
        user = userauth.currentUser


        pay.setOnClickListener {
            if (fname.text.isNullOrBlank() || emailadd.text.isNullOrBlank() || phoneno.text.isNullOrBlank()) {
                Toast.makeText(this, "You can't leave a field empty", Toast.LENGTH_SHORT).show()
            } else {
                //payment function
                startPayment()
            }

        }


    }
//Inbuilt Razorpay function for payment gateway
    private fun startPayment() {
        /*
        *  You need to pass current activity in order to let Razorpay create CheckoutActivity
        * */
        val activity: Activity = this
        val co = Checkout()

        try {
            //objects to be included in razorpay payment interface
            val options = JSONObject()
            options.put("name", fname.text.toString())
            options.put("description", "Transaction for PDF $pdfname \n Price :- $price")
            options.put("currency", "INR")
            options.put("amount", price + "00")

            val prefill = JSONObject()
            prefill.put("email", emailadd.text.toString())
            prefill.put("contact", phoneno.text.toString())
            options.put("prefill", prefill)
            co.open(activity, options)
        } catch (e: Exception) {
            Toast.makeText(activity, "Error in payment: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentError(errorCode: Int, response: String?) {
        Toast.makeText(this, "Payment Error $response", Toast.LENGTH_SHORT).show()

    }

    override fun onPaymentSuccess(razorpayPaymentId: String?) {
        var name = ""
        try {
            Toast.makeText(
                this,
                "Payment Successful \n Payment ID :- $razorpayPaymentId ",
                Toast.LENGTH_SHORT
            ).show()
            //generates random transaction id
            var rnds = (100..100000).random()
            val db = FirebaseDatabase.getInstance()
             val dbrefer = db.getReference()

            //get username of current user
            dbrefer.child("Users").child("Students").child(user!!.uid).addValueEventListener( object:  ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(p0: DataSnapshot) {
                    name = p0.child("Username").value.toString()
                    //to check for same transaction id
                    dbrefer.child("Transactions").addChildEventListener(object : ChildEventListener{
                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                        }

                        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                        }

                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            if (p0.hasChild("Transactions ID :- "+rnds.toString())){
                                rnds = (100..100000).random()
                                dbrefer.child("Transactions").child("Transactions ID :- "+rnds.toString()).child("name").setValue(name)
                            }
                            else {
                                //add transaction details in database
                                dbrefer.child("Transactions").child("Transactions ID :- "+rnds.toString()).child("name").setValue(name)
                            }
                        }

                        override fun onChildRemoved(p0: DataSnapshot) {
                        }

                    })
                }

            })
            var month2 = ""
            var day2 = ""
            dbrefer.child("Transactions").child("Transactions ID :- "+rnds.toString()).child("pdf").setValue(pdfname)
            dbrefer.child("Transactions").child("Transactions ID :- "+rnds.toString()).child("value").setValue(price)
         //Date calculation
            val calendar: Calendar = Calendar.getInstance(TimeZone.getDefault())
            val day = calendar[Calendar.DATE]
            //Note: +1 the month for current month
            val month = (calendar[Calendar.MONTH] + 1)
            if(month<10){
                month2 = ("0"+month).toString()
            }
            else {
                month2 = month.toString()
            }
            if(day<10){
                day2 = ("0"+day).toString()
            }
            else {
                day2 = day.toString()
            }
            val year = calendar[Calendar.YEAR].toString()
            val date = day2+"/"+month2+"/"+year
            dbrefer.child("Transactions").child("Transactions ID :- "+rnds.toString()).child("date").setValue(date)
            downloadTask()
        }

        catch (e: Exception){
            Toast.makeText(this, "Error in success: " + e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }

    }
//Downloading pdf from firebase storage
    fun downloadTask() {
        val httpsReference = storage.getReferenceFromUrl(
            url
        )
        val rootPath =
            File(Environment.getExternalStorageDirectory(), ".rha/"+encryptname)
        val localFile = File(rootPath, pdfname + ".pdf")

        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }

        httpsReference.getFile(localFile)
            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                Log.e("firebase ", ";local tem file created  created $localFile")
                progressDialog.dismiss()
                path = localFile.absolutePath
                Log.i("pathh",path)
                val intent = Intent(this,Pdfviewer::class.java)

                //send all intents to pdfviewer
                intent.putExtra("encryptname",encryptname)
                intent.putExtra("pdfname",pdfname)
                intent.putExtra("url",url)
                intent.putExtra("path",path)
                startActivity(intent)
                finish()
            }).addOnFailureListener(OnFailureListener { exception ->
                Log.e(
                    "firebase ",
                    ";local tem file not created  created $exception"
                )
                Toast.makeText(this,"Error:- $exception",Toast.LENGTH_LONG).show()
            }).addOnProgressListener {
                progressDialog.show()
                val progress: Double =
                    100.0 * it.getBytesTransferred() / it.getTotalByteCount()
                progressDialog.setMessage(
                    "Downloaded " +
                            progress.toInt() + "%"
                )
            }
    }


}
