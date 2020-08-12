package com.app.hardik.studypdf

import android.app.ActionBar
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

//since this fragment is opened first , hence all global variables are declared here
//to be used in other activities/fragments directly

var menu = mutableListOf<Any>()         //List of usernames from Users
var email = mutableListOf<Any>()        //List of emails from Users
var costs = mutableListOf<Any>()        //List of costs from transaction
var finalcost = mutableListOf<Any>()    //List of total cost per usernames
var costname = mutableListOf<Any>()     //List of usernames from transaction
var pdf = mutableListOf<Any>()          //List of pdf's from transaction
var date = mutableListOf<Any>()          //List of date's from transaction
var position: Int = 0                   //positon of card clicked
var Total_revenue: Int = 0

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//This Fragment is for Home Screen of Admin
//It shows Statistics of users,downloads,revenue and transaction history

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {

    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    var count: Int = 0
    var last: Int = 0
    var count2: Int = 0
    var last2: Int = 0
    var index:Int = 0
    var costnamefirst: Int = 0
    var costnamelast: Int = 0
    var cost: Int = 0
    lateinit var recyclerView: RecyclerView
    lateinit var imageModelArrayList: ArrayList<Stats>
    lateinit var adapter: HotizontalAdapter

    lateinit var names: String
    lateinit var pdfs: String
    lateinit var revenue: String
    lateinit var dates: String
    val dateMap = hashMapOf<String,String>()

    var sorteddates = listOf<Any>()

    private lateinit var spinner: ProgressBar
    lateinit var transactions: LinearLayout

    lateinit var nav_menu: Menu

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
        val view = inflater.inflate(R.layout.fragment_home,container,false)

        //spinner
        spinner = view.findViewById(R.id.progressBar1)
        transactions= view.findViewById(R.id.transactions)
        spinner.visibility = View.VISIBLE
        nav_menu = bottomNavigation.menu

        //Set navigation bar invisible until it loads all data from firebase
        nav_menu.findItem(R.id.navigation_settings).setVisible(false)
        nav_menu.findItem(R.id.navigation_upload).setVisible(false)
        nav_menu.findItem(R.id.navigation_home).setVisible(false)
        nav_menu.findItem(R.id.navigation_list).setVisible(false)
        nav_menu.findItem(R.id.navigation_users).setVisible(false)

        recyclerView = view.findViewById(R.id.recycler)
        imageModelArrayList =  ArrayList<Stats>()







        if (menu.isEmpty()){
            Log.i("Empty","True")
        }
        else{
            //clear all lists if it is not empty
            menu.clear()
            email.clear()
            costs.clear()
            finalcost.clear()
            costname.clear()
            pdf.clear()
            date.clear()
            position = 0
            Total_revenue = 0
            Log.i("Empty","False")
        }
        //Fetching Details from database to mutable lists
        var Done :Boolean = false           //Used to know if fetching from db is over or not
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference()

        //this listener used to get total count of users also stores each users username and email in hashmaps
        databaseReference.child("Users").child("Students").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                //Saves usernames in menu list
               // initializing stats
                var userstats = Stats()
                p0.children.mapNotNullTo(menu) {
                    it.child("Username").value
                }
                //Saves emails in email list
                p0.children.mapNotNullTo(email) {
                    it.child("Email").value
                }
                if(p0.exists()){
                    //show count of users
                    userstats.name =  p0.childrenCount.toString()

                }
                else {
                    userstats.name =  "NA"
                }
                userstats.image_drawable = R.drawable.users4
                imageModelArrayList.add(userstats)
            }
        })

        //This listener for listening all transactions to get total downloads and also stores data
        //related to transaction of each user in hashmaps
        databaseReference.child("Transactions").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                var downloadstats = Stats()
                p0.children.mapNotNullTo(costs) {
                    it.child("value").value
                }
                p0.children.mapNotNullTo(costname) {
                    it.child("name").value
                }
                p0.children.mapNotNullTo(pdf) {
                    it.child("pdf").value
                }
                p0.children.mapNotNullTo(date) {
                    it.child("date").value
                }
                if(p0.exists()){
                    //show count of downloads
                    downloadstats.name = p0.childrenCount.toString()

                }
                else {
                }
                downloadstats.image_drawable = R.drawable.downloading2
                imageModelArrayList.add(downloadstats)

                // Calculation of total cost
                var revenuestats = Stats()
                count = menu.indexOfFirst { true }
                last  = menu.indexOfLast { true }
                while (count <= last){
                    costnamefirst = costname.indexOfFirst { true }
                    costnamelast = costname.indexOfLast { true }
                    while ( costnamefirst <= costnamelast ){
                        if (costname.get(costnamefirst) == menu.get(count))
                        {
                            cost += Integer.parseInt(costs.get(costnamefirst).toString())
                        }
                        costnamefirst += 1
                    }
                    Total_revenue = Total_revenue + cost
                    finalcost.add(count,cost)
                    cost = 0
                    count += 1
                }
                //show total revenue
                revenuestats.name = Total_revenue.toString()
                revenuestats.image_drawable = R.drawable.rupees2
                imageModelArrayList.add(revenuestats)


                adapter = HotizontalAdapter(view.context, imageModelArrayList)
                recyclerView.adapter = adapter
                recyclerView.layoutManager = LinearLayoutManager(
                    view.context,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                //Sorting lists associated with transactions according to dates
              //  val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
               var format = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                    var date1 : Date? = null
                sorteddates = date.sortedByDescending {
                   // LocalDate.parse(it as CharSequence?, dateTimeFormatter)
                    format.parse(it.toString())
                }
                for ( i in date){
                    val new = i.toString().replace("/","")
                    Log.i("newdate",new)
                    dateMap.put(i.toString(),new)
                }
                //Sort all lists according to the sorted dates
                count = sorteddates.indexOfFirst { true }
                last  = sorteddates.indexOfLast { true }
                index = date.indexOfFirst { true }
                while (count <= last){
                    count2 = date.indexOfFirst { true }
                    last2 = date.indexOfLast { true }
                    while (count2 <= last2){
                        if (sorteddates[count] == date[count2]){

                            var temp = date[count2]
                            date[count2]=date[count]
                            date[count]=temp

                            temp = costname[count2]
                            costname[count2]= costname[count]
                            costname[count] = temp

                            temp = pdf[count2]
                            pdf[count2]= pdf[count]
                            pdf[count] = temp

                            temp = costs[count2]
                            costs[count2]= costs[count]
                            costs[count] = temp

                            count2 += 1
                        }
                        else{count2 += 1}
                    }
                    count += 1
                }

                Done = true
            }
        })
        //Check if user is online or not
        if (isOnline(view.context)){
            //Creating cards
            val handler = Handler()
            handler.postDelayed(object : Runnable {
                override fun run() {
                    //If the loading and sorting of data is done then show card view
                    if (Done == true){
                        Log.d("Menu after done", menu.toString())
                        count = costname.indexOfFirst { true }
                        last  = costname.indexOfLast { true }
                        while (count <= last){
                            if(count == 0 || date[count] != date[count-1]){
                                dates = date.get(count).toString()
                                newdatecard()           //Dates
                            }
                            names = costname.get(count).toString()
                            pdfs = pdf.get(count).toString()
                            revenue = costs.get(count).toString()
                            new()                       //User information
                            count = count + 1
                        }

                        spinner.visibility = View.GONE

                        //Set navigation bar visible
                        nav_menu = bottomNavigation.menu
                        nav_menu.findItem(R.id.navigation_settings).setVisible(true)
                        nav_menu.findItem(R.id.navigation_upload).setVisible(true)
                        nav_menu.findItem(R.id.navigation_home).setVisible(true)
                        nav_menu.findItem(R.id.navigation_list).setVisible(true)
                        nav_menu.findItem(R.id.navigation_users).setVisible(true)
                    }
                    else {
                        handler.postDelayed(this, 1000)//1 sec delay
                    }
                }
            }, 0)

        }
        else{
            //Set navigation bar visible
            spinner.visibility = View.GONE
            nav_menu = bottomNavigation.menu
            nav_menu.findItem(R.id.navigation_settings).setVisible(true)
            nav_menu.findItem(R.id.navigation_upload).setVisible(true)
            nav_menu.findItem(R.id.navigation_home).setVisible(true)
            nav_menu.findItem(R.id.navigation_list).setVisible(true)
            nav_menu.findItem(R.id.navigation_users).setVisible(true)
            Toast.makeText(view.context,"Check network connection !",Toast.LENGTH_LONG).show()
        }

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
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    fun newdatecard() {

        // Initialize a new CardView instance for dates
        val card_view2 = CardView(view!!.context)

        // Initialize a new LayoutParams instance, CardView width and height
        val layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.WRAP_CONTENT, // CardView width
            ActionBar.LayoutParams.WRAP_CONTENT // CardView height
        )

        // Set bottom margin for card view
        layoutParams.topMargin = 40

        layoutParams.leftMargin = 20

        // Set the card view layout params
        card_view2.layoutParams = layoutParams

        // Set the card view corner radius
        card_view2.radius = 5F

        // Set the card view content padding
        card_view2.setContentPadding(25, 12, 25, 12)

        // Set the card view background color
        card_view2.setCardBackgroundColor(Color.WHITE)

        // Set a click listener for card view
        card_view2.setOnClickListener {
            // position = card_view.id
            // startActivity(Intent(view!!.context,ExpandCard::class.java))
        }

        // Add an TextView to the CardView
        card_view2.addView(generatedate())

        // Finally, add the CardView in root layout
        transactions.addView(card_view2)
    }

    fun new() {
        // Initialize a new CardView instance for user info
        val card_view = CardView(view!!.context)
        card_view.id = count

        // Initialize a new LayoutParams instance, CardView width and height
        val layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT, // CardView width
            ActionBar.LayoutParams.WRAP_CONTENT // CardView height
        )

        // Set bottom margin for card view
        layoutParams.bottomMargin = 40
        layoutParams.leftMargin = 8
        layoutParams.rightMargin = 8

        // Set the card view layout params
        card_view.layoutParams = layoutParams

        // Set the card view corner radius
        card_view.radius = 12F

        // Set the card view content padding
        card_view.setContentPadding(25, 25, 25, 25)

        // Set the card view background color
        card_view.setCardBackgroundColor(Color.rgb(98,0,238))

        // Set card view elevation
        card_view.cardElevation = 20F

        // Set card view maximum elevation
        card_view.maxCardElevation = 12F

        // Set a click listener for card view
        card_view.setOnClickListener {
            position = card_view.id
            // startActivity(Intent(view!!.context,ExpandCard::class.java))
        }

        // Add an TextView to the CardView
        card_view.addView(generatename())
        card_view.addView(generatepdf())
        card_view.addView(generateprice())

        // Finally, add the CardView in root layout
        transactions.addView(card_view)
    }

    //Custom method to generate an text view
    private fun generatename(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        params.leftMargin = 20
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = names
        textView.textSize = 22F
        return textView
    }
    private fun generatepdf(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        params.setMargins(20,80,0,0)
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = pdfs
        textView.textSize = 19F
        return textView
    }
    private fun generateprice(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        var width = transactions.measuredWidth
        var width2 = width*25/100
        params.setMargins(width-width2,80,0,0)
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = "₹ " + revenue
        textView.textSize = 20F
        return textView
    }
    private fun generatedate(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        textView.layoutParams = params
        textView.setTextColor(Color.rgb(254,137,1))
        textView.text = dates
        textView.textSize = 15F
        return textView
    }

}
