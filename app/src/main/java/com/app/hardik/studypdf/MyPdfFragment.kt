package com.app.hardik.studypdf

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.multilevelview.MultiLevelRecyclerView
import com.multilevelview.models.RecyclerViewItem
import org.json.JSONObject
import java.io.File

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//This Fragment gives List of All PDFs Bought by user it also works offline

/**
 * A simple [Fragment] subclass.
 * Use the [MyPdfFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyPdfFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var dbrefer: DatabaseReference
    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var myAdapter: Pdfadapter
    lateinit var Pdfs: MutableList<Item>
    lateinit var pdflistsharedlist: MutableList<Item>
    lateinit var storage: FirebaseStorage
    lateinit var progressDialog: ProgressDialog
    lateinit var storageRef: StorageReference
    lateinit var path : String
    val mapKey = "map"
    var pdflist = mutableListOf<Any>()
    var encryptnameshared : String = ""
    var pdflistshare = mutableListOf<Any>()
    var usernames = mutableListOf<Any>()     //List of usernames from transaction
    val list: MutableList<String> = ArrayList()
    var user: FirebaseUser? = null
    var username : String = ""
    var url : String = ""
    var encryptname : String = ""
    var urlMap = hashMapOf<String,String>()
    var keyMap = hashMapOf<String,String>()
    var pdfname = ""
    var pdflistshared : MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view =  inflater.inflate(R.layout.fragment_my_pdf, container, false)
        val multiLevelRecyclerView = view.findViewById(R.id.rv_list) as MultiLevelRecyclerView
        multiLevelRecyclerView.layoutManager = LinearLayoutManager(view.context)
        Pdfs = ArrayList<RecyclerViewItem>() as MutableList<Item>

        //Storing pdf list in shared prefereance list to access it while offline
        pdflistsharedlist = ArrayList<RecyclerViewItem>() as MutableList<Item>
        db = FirebaseDatabase.getInstance()
        dbrefer = db.getReference()
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser
        storage = Firebase.storage
        storageRef = storage.reference
        progressDialog = ProgressDialog(view.context)
        progressDialog.setTitle("Loading PDF...")
        progressDialog.setCanceledOnTouchOutside(false)

        //get list from  shared preference
        var pdflistsave = activity!!.getSharedPreferences("pdflist",Context.MODE_PRIVATE).getStringSet("pdflistsave",null)
        if (pdflistsave.isNullOrEmpty()){
            //if Empty
            val mutableSet : MutableSet<String> = mutableSetOf()
            mutableSet.add("NO PDFS FOUND")
            pdflistsave = mutableSet
        }

        pdflistshared  = pdflistsave.toMutableList()

        //get username of current user
        dbrefer.child("Users").child("Students").child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                username = p0.child("Username").value.toString()

            }

        })

        //Default item to include
        var item = Item(0)
        item.setText("Your Saved PDFs")
        item.setSecondText("Click to Open!")
        Pdfs.add(item)
        //read list
        readlist()
        //check if user is online and according to it load list
        if(isOnline(view.context)) {
            myAdapter = Pdfadapter(view.context, Pdfs, multiLevelRecyclerView)
            multiLevelRecyclerView.adapter = myAdapter
        }
        else {
            //in case of offline , turlmap which is collection of url of each pdf
            //keymap is collection of encryptname of each pdf
            //both of them are loaded from shared preferences

            urlMap = loadUrl()
            keyMap = loadKey()

            //this filters the pdfs from pdf list which only shows the pdfs bought by user
            for(i in pdflistsave) {
                var item = Item(0)
                item.setText(i.toString())
                pdflistsharedlist.add(item)
            }
            Log.i("sharedpref2",pdflistsharedlist.toString())
            //handler to cause delay
            Handler().postDelayed({
                Log.i("sharedpref3",pdflistsharedlist.toString())
                myAdapter = Pdfadapter(view.context, pdflistsharedlist , multiLevelRecyclerView)
                multiLevelRecyclerView.adapter = myAdapter
            },2000)

        }

        multiLevelRecyclerView.setOnItemClick { view, item, position ->
            //Click on pdf to open it
            //Load file from internal storage and show it in pdfviewer
            //in case if file is missing then download it
            if(isOnline(view.context)) {
                if (Pdfs.get(position).getSecondText().equals("Click to Open!")) {

                } else {
                    pdfname = Pdfs.get(position).getText().toString()
                    readurl()

                }
            }
            else {
                    pdfname = pdflistsharedlist.get(position).getText().toString()
                     encryptnameshared = keyMap.get(pdfname).toString()
                val rootPath =
                    File(Environment.getExternalStorageDirectory(), ".rha/"+encryptnameshared)
                val localFile = File(rootPath, pdfname+".pdf")
                if(localFile.exists()){
                    val path = localFile.absolutePath
                    val intent = Intent (activity,Pdfviewer::class.java)
                    intent.putExtra("path",path)
                    startActivity(intent)
                }
                else {
                    Toast.makeText(view.context,"File Doesnt Exist",Toast.LENGTH_SHORT).show()
                }
0
            }

        }
        //this listener to store urls and encrypted name of each pdf in their respected hashmaps
        dbrefer.child("Links").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                urlMap.put(p0.key.toString(),p0.child("url").value.toString())
                keyMap.put(p0.key.toString(),p0.child("encryptname").value.toString())
                urlMapfun(urlMap)
                keyMapfun(keyMap)

            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

        })
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MyPdfFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyPdfFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//this function is used to download file
    fun downloadTask() {
        val httpsReference = storage.getReferenceFromUrl(
            url
        )
        val rootPath =
            File(Environment.getExternalStorageDirectory(), ".rha/"+encryptname)
        val localFile = File(rootPath, pdfname+".pdf")

        if(localFile.exists()){
            val path = localFile.absolutePath
            val intent = Intent (activity,Pdfviewer::class.java)
            intent.putExtra("path",path)
            startActivity(intent)
            return
        }
        if (!rootPath.exists()) {
            rootPath.mkdirs()
        }
        httpsReference.getFile(localFile)
            .addOnSuccessListener(OnSuccessListener<FileDownloadTask.TaskSnapshot?> {
                Log.e("firebase ", ";local tem file created  created $localFile")
                //  updateDb(timestamp,localFile.toString(),position);
                progressDialog.dismiss()
                path = localFile.absolutePath
                Log.i("pathh",path)
                val intent = Intent (activity,Pdfviewer::class.java)
                intent.putExtra("path",path)
                startActivity(intent)
            }).addOnFailureListener(OnFailureListener { exception ->
                Log.e(
                    "firebase ",
                    ";local tem file not created  created $exception"
                )
                Toast.makeText(activity,"Error:- $exception", Toast.LENGTH_LONG).show()
            }).addOnProgressListener {
                progressDialog.show()
                val progress: Double =
                    100.0 * it.getBytesTransferred() / it.getTotalByteCount()
                progressDialog.setMessage(
                    "Loaded " +
                            progress.toInt() + "%"
                )
            }
    }

    //This function reads all pdfs and also filters according to user
    fun readlist () {
        dbrefer.child("Transactions").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.mapNotNullTo(pdflist) {
                    it.child("pdf").value
                }
                p0.children.mapNotNullTo(usernames){
                    it.child("name").value
                }
                val count = pdflist.count()
                Log.i("count",count.toString())
                for (i in 0..count-1){
                    Log.i("pdfname",usernames.get(i).toString())
                    if(usernames.get(i)==username){
                        var item = Item(0)
                        item.setText(pdflist.get(i).toString())
                        pdflistshare.add(pdflist.get(i).toString())
                        Log.i("pdfna2",pdflist.get(i).toString())
                        Pdfs.add(item)
                    }
                }
                val pdfset = HashSet<String>()
                for ( i in pdflistshare){
                    pdfset.add(i.toString())
                }
                activity!!.getSharedPreferences("pdflist",Context.MODE_PRIVATE)
                    .edit().putStringSet("pdflistsave",pdfset as MutableSet<String>).apply()
                Log.i("sharedpref",Pdfs.toString())
                myAdapter.notifyDataSetChanged()
            }

        })

    }

    //This function is used to read url and encryptname
    fun readurl(){

        dbrefer.child("Links").child(pdfname).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                url = p0.child("url").value.toString()
                encryptname = p0.child("encryptname").value.toString()
                downloadTask()
            }

        })
    }

    //Mapping function
    private fun urlMapfun(inputMap: HashMap<String, String>) {
        val pSharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences(
            "url",
            Context.MODE_PRIVATE
        )
        if (pSharedPref != null) {
            val jsonObject = JSONObject(inputMap)
            val jsonString: String = jsonObject.toString()
            val editor = pSharedPref.edit()
            editor.remove(mapKey).apply()
            editor.putString(mapKey, jsonString)
            editor.commit()
        }
    }

    //Mapping function
    private fun keyMapfun(inputMap: HashMap<String, String>) {
        val pSharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences(
            "encryptname",
            Context.MODE_PRIVATE
        )
        if (pSharedPref != null) {
            val jsonObject = JSONObject(inputMap)
            val jsonString: String = jsonObject.toString()
            val editor = pSharedPref.edit()
            editor.remove(mapKey).apply()
            editor.putString(mapKey, jsonString)
            editor.commit()
        }
    }

    //storing urlmap in sharedpreferences
    private fun loadUrl(): HashMap<String, String> {
        var outputMap: HashMap<String, String> = HashMap()
        val pSharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences(
            "url",
            Context.MODE_PRIVATE
        )
        try {
            if (pSharedPref != null) {
                val jsonString =
                    pSharedPref.getString(mapKey, JSONObject().toString())
                val jsonObject = JSONObject(jsonString)
                val keysItr = jsonObject.keys()
                while (keysItr.hasNext()) {
                    val key = keysItr.next()
                    outputMap
                    outputMap.put(key, jsonObject[key].toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputMap
    }

    //storing keymap in sharedpreferences
    private fun loadKey(): HashMap<String, String> {
        var outputMap: HashMap<String, String> = HashMap()
        val pSharedPref: SharedPreferences = activity!!.applicationContext.getSharedPreferences(
            "encryptname",
            Context.MODE_PRIVATE
        )
        try {
            if (pSharedPref != null) {
                val jsonString =
                    pSharedPref.getString(mapKey, JSONObject().toString())
                val jsonObject = JSONObject(jsonString)
                val keysItr = jsonObject.keys()
                while (keysItr.hasNext()) {
                    val key = keysItr.next()
                    outputMap
                    outputMap.put(key, jsonObject[key].toString())
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputMap
    }

}
