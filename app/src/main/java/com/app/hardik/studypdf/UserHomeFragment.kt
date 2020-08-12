package com.app.hardik.studypdf

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.multilevelview.MultiLevelRecyclerView
import com.multilevelview.models.RecyclerViewItem

lateinit var device_count:String




// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [UserHomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

/* Everything Related to MultiRecyclerview (Comments stuff) can be found in ListFragment.kt file
as the code related to it is exactly same
* */

class UserHomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var dbrefer: DatabaseReference
    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var myAdapter: UserAdapter
    lateinit var Level0list: MutableList<Item>
    lateinit var welcome: TextView
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
        val view = inflater.inflate(R.layout.fragment_user_home, container, false)
        welcome = view.findViewById(R.id.welcometext)
        auth = FirebaseAuth.getInstance()
        db= FirebaseDatabase.getInstance()
        dbrefer=db.getReference()
        val user = auth.currentUser

        //to get current user's username
        dbrefer.child("Auth").child("AllUsers").child(user!!.uid).addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                welcome.text = "Welcome "+p0.child("Username").value.toString()+" !"

                //Declaration of device count
                device_count = p0.child("LoggedInDevice").value.toString()
            }

        })

        val multiLevelRecyclerView =
            view.findViewById(R.id.rv_list) as MultiLevelRecyclerView
        multiLevelRecyclerView.layoutManager = LinearLayoutManager(this.context)
        //Default Element to include
        Level0list = ArrayList<RecyclerViewItem>() as MutableList<Item>
        var item = Item(0)
        item.setText("All Available list of Notes!")
        Level0list.add(item)
        readlist()
        myAdapter = UserAdapter(view.context, Level0list, multiLevelRecyclerView)
        multiLevelRecyclerView.adapter = myAdapter
        //on click listener for multiLevelRecyclerView
        multiLevelRecyclerView.setOnItemClick { view, item, position ->

            //gets selected node's database path from database itself
            if(Level0list.get(position).text.substring(0,2).equals("->")){
                val name = Level0list.get(position).text.substringAfter("-> ","")
                dbrefer.child("SubjectPath").child(name).addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {

                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        val path = p0.value.toString()
                        val intent = Intent(view.context,Pdflist::class.java)
                        intent.putExtra("path",path)
                        intent.putExtra("name",name)
                        startActivity(intent)
                    }

                })
            }

        }
        //pull to refresh
        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            reload()
            pullToRefresh.isRefreshing = false
        }

        // Inflate the layout for this fragment
        return view
    }
    //listener to read whole list
    fun readlist(){
        dbrefer.child("StreamList").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,p0.toString(), Toast.LENGTH_LONG).show()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val demolist = ArrayList<RecyclerViewItem>()
                val demoitem = Item(0)
                listreader(demoitem,demolist,p0,0)
                myAdapter.notifyDataSetChanged()

            }
            override fun onChildRemoved(p0: DataSnapshot) {
                reload()
            }
        })
    }
    //reload function
    fun reload () {
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }
    //function which can read any type of N-level hierarchical tree
    fun listreader (parent:Item,parentlist:ArrayList<RecyclerViewItem>,p0:DataSnapshot,lvl:Int) {
        val parentlist = parentlist as MutableList<Item>
        var lvl = lvl
        if (!(p0.hasChildren()) && lvl != 0 && !(p0.key.toString().equals("name"))){
            //to check if given subject has pdfs available or not
            var name = p0.key.toString()
            Log.i("Leaf node", name+" at level $lvl")
           var itemname =  parent.getText()
            parent.setText("-> "+itemname)
            //IMP :- This "->" and "__" is used as Indicator for detecting leaf node so DO NOT CHANGE IT (You can replace but you have replace all its occurences)
            parent.setSecondText("__")
            db = FirebaseDatabase.getInstance()
            //listener for getting pdf count for leaf node
            var count = 0
            dbrefer.child("Links").addChildEventListener(object : ChildEventListener{
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    if(p0.child("parent").value!!.equals(name)){
                        count++
                    }
                    if(count == 0 && (parent.text.substring(0,2).equals("->"))){
                        parent.setSecondText("No Pdf Available")
                    }
                    else if (count > 0 && (parent.text.substring(0,2).equals("->"))){
                        parent.setSecondText(count.toString()+" Pdfs Available")
                    }
                    if(!(parent.text.substring(0,2).equals("->")))
                    {
                        parent.setSecondText("")
                    }
                    myAdapter.notifyDataSetChanged()
                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }

            })
        }
        else if(lvl == 0){
            val Level1list = ArrayList<RecyclerViewItem>() //as MutableList<Item>
            val lvl0 = Item(lvl)
            if (p0.key.toString().equals("name")){

            }
            else if(p0.child("name").exists()){
                lvl0.setText(p0.child("name").value.toString())
                Level0list.add(lvl0)
                var newlevel = lvl + 1
                lvl0.setSecondText("")
                listreader(lvl0,Level1list,p0,newlevel)
            }
            else {
                lvl0.setText(p0.key.toString())
                Level0list.add(lvl0)
                var newlevel = lvl + 1
                lvl0.setSecondText("")
                listreader(lvl0,Level1list,p0,newlevel)
            }

        }
        else {
            for (i in p0.children){
                val item = Item(lvl)
               if (i.key.toString().equals("name")){
                   continue
               }
                else if(i.child("name").exists()){
                    item.setText(i.child("name").value.toString())
                }
                else {
                   item.setText(i.key.toString())
               }
                item.setSecondText("")
                parentlist.add(item)
                parent.addChildren(parentlist as MutableList<RecyclerViewItem>)
                val futurelist = ArrayList<RecyclerViewItem>()
                var newlevel = lvl + 1
                listreader(item,futurelist,i,newlevel)
                parent.setSecondText("")
            }
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserHomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserHomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
