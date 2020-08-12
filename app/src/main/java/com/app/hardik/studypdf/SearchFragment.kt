package com.app.hardik.studypdf

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.database.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//This page is used to search pdf , it also shows pdf's details


/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    lateinit var searchView: SearchView
    lateinit var searchlist: ListView
    lateinit var searchimg: ImageView
    lateinit var searchscroll: ScrollView
    lateinit var searchtext: TextView
    lateinit var anim_translate_top: Animation
    lateinit var anim_fade_out : Animation
    lateinit var myAdapter: ArrayAdapter<String>
    var c: Int = 0
    lateinit var name: String
    lateinit var Depflag: String

    var newsublist: MutableList<String> = ArrayList()
    var tempsublist: MutableList<String> = ArrayList()

    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference

    var subkey = mutableListOf<String>()
    var subpath = mutableListOf<String>()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        //Fetching subjects and paths from database
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference()
        //Get data from databse
        databaseReference.child("SubjectPath").addValueEventListener(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                //Saves usernames in menu list
                p0.children.mapNotNullTo(subpath) {
                    it.value.toString()     //Path of subject
                }
                p0.children.mapNotNullTo(subkey){
                    it.key                  //Value of subject
                }
            }
        })
        searchView = view.findViewById(R.id.SearchView)
        searchimg = view.findViewById(R.id.Searchimage)
        searchscroll = view.findViewById(R.id.scrollViewSEARCH)
        searchtext = view.findViewById(R.id.textsearch)
        searchlist = view.findViewById(R.id.SEARCH_LIST)
        anim_translate_top = AnimationUtils.loadAnimation(view.context,R.anim.translate_top)
        anim_fade_out = AnimationUtils.loadAnimation(view.context, R.anim.fade_out)

        //Initializing list view
        tempsublist = subpath
        myAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_list_item_1, tempsublist
        )
        searchlist.adapter = myAdapter
        searchlist.visibility = View.GONE

        //Trigger Animation
        searchimg.setOnClickListener{
            searchimg.startAnimation(anim_translate_top)
            searchtext.startAnimation(anim_fade_out)
        }
        searchtext.setOnClickListener{
            searchimg.startAnimation(anim_translate_top)
            searchtext.startAnimation(anim_fade_out)
        }
        anim_translate_top.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
            }
            override fun onAnimationEnd(animation: Animation) {
                searchView.visibility = View.VISIBLE
                searchView.onActionViewExpanded()
                searchimg.visibility = View.GONE
                searchtext.visibility = View.GONE
                searchscroll.visibility = View.VISIBLE
            }
            override fun onAnimationRepeat(animation: Animation) {
            }
        })

        //getting text from search view and processing it
        Handler().postDelayed({
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.d("Submit", query)
                    if (query != null){
                       view.hideKeyboard()
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    //Toast.makeText(view.context,newText,Toast.LENGTH_SHORT).show()
                    if(isOnline(view.context)){
                        if (newText != null  && TextUtils.getTrimmedLength(newText) > 0) {
                            searchlist.visibility = View.VISIBLE
                            name = newText
                            Log.d("NAME", name)
                            if (newlist()){
                                //If found something then show templist
                                myAdapter = ArrayAdapter(
                                    view.context,
                                    android.R.layout.simple_list_item_1, tempsublist
                                )
                                if (tempsublist.isEmpty()){
                                    //Else show 'Not Found'
                                    tempsublist.add(0, " '" + name+ "' " + " Not Found")
                                }
                                searchlist.adapter = myAdapter
                            }
                        }
                        else{
                            searchlist.visibility = View.GONE
                        }
                    }
                    else{
                        Toast.makeText(view.context,"Please check connection !",Toast.LENGTH_SHORT).show()
                    }

                    return true
                }

            })
            //Clicking on any pdf will take it to Pdflist page
            searchlist.setOnItemClickListener(object : AdapterView.OnItemClickListener {
                override fun onItemClick(
                    arg0: AdapterView<*>?,
                    arg1: View?,
                    position: Int,
                    id: Long
                ) {

                    databaseReference.child("SubjectPath").addValueEventListener(object :
                        ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) {


                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val name = tempsublist.get(position)
                            if(name.isNullOrBlank() || name.isEmpty()){}
                            else{
                                val subname = name.substringBefore("\n"," ")
                                Log.i("subbname",subname)
                                val path = p0.child(subname).value.toString()
                                val intent = Intent(view.context,Pdflist::class.java)
                                intent.putExtra("name",subname)
                                intent.putExtra("path",path)
                                startActivity(intent)
                            }

                        }

                    })
                }
            })
        }, 5000)

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

//function to hide keyboard after clicking anywhere else
    fun View.hideKeyboard() {
        val inputMethodManager =
            context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
    }

    //If Entered string is found in the list of subject path then return true store that subject path in templist
    //Else templist will be empty
    fun newlist(): Boolean {
        c = 0
        newsublist.clear()
        for (value in subpath) {
            if (value.toUpperCase().contains(name.toUpperCase().toRegex())) {
                Log.i("subpath",value)
                Depflag = value.substring(value.indexOf("/")+1)
                if (Depflag.contains("/")){
                Depflag = Depflag.substring(Depflag.indexOf("/")+1)}
                if (Depflag.contains("/")){
                Depflag = Depflag.substring(0,Depflag.indexOf("/"))}
                newsublist.add(c,subkey[subpath.indexOf(value)] + "\n" + Depflag)
                c += 1
            }
        }
        Log.d("List",newsublist.toString())
        tempsublist = newsublist
        return true
    }
}

