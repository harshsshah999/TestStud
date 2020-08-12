package com.app.hardik.studypdf

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.multilevelview.MultiLevelRecyclerView
import com.multilevelview.models.RecyclerViewItem
import kotlinx.android.synthetic.main.content_list.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


/**
 * A simple [Fragment] subclass.
 * Use the [ListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
/* currentMode is a class to detect which mode is On
    updateClicked == 1 ; ADD Mode is On
    updateClicked == 2 ; DELETE Mode is On
    by default its 0. this class is made public so that it can be accessed in ListAdapter.java class which
    is an adapter for MultiLevelRecyclerList View
 */
object currentMode {
    var updateClicked : Int = 0
}

//This Fragment shows N-Level hierarchical tree of our database , also admin can add and delete element
//at any level of this tree

class ListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var dbrefer: DatabaseReference
    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var myAdapter: ListAdapter
    lateinit var Level0list: MutableList<Item> //Final list to be passed in Myadapter
    var t = 0


    //
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
        //Copy return statement and store it in new variable "View"
        var view = inflater.inflate(R.layout.content_list, container, false)

        //declaration of multiLevelRecyclerView
        val multiLevelRecyclerView =
            view.findViewById(R.id.rv_list) as MultiLevelRecyclerView

        //multiLevelRecyclerView is attached with Layout Manager
        multiLevelRecyclerView.layoutManager = LinearLayoutManager(this.context)

        //Default Element to include in list (Help item)
        Level0list = ArrayList<RecyclerViewItem>() as MutableList<Item>
        var item = Item(0)
        item.setText("All Available Categories")
        item.setSecondText("Swipe Down to Refresh List")
        //If-Else for changing the text of Help item according to the Mode
        if(currentMode.updateClicked == 1){
            //when ADD Mode is on

            item.setText("New +")
            item.setSecondText("Long Click here to add New Element")
        }
        else{
            //Default text to shown , even in DELETE Mode
            item.setText("All Available Categories")
            item.setSecondText("Swipe Down to Refresh List")
        }
        //adding item in list
        Level0list.add(item)

        //Firebase Database Instance and Referance
        db= FirebaseDatabase.getInstance()
        dbrefer=db.getReference()

        /*
             Calling firstread function to read list from database and display in
             multiLevelRecyclerView. internal functions related multiLevelRecyclerView
             can be found in its Adapter class file.(in this case its ListAdapter.java)
         */
        firstread()

        //assiging values to adapter
        myAdapter = ListAdapter(
            view.context,
            Level0list,
            multiLevelRecyclerView
        )
        //assiging adapter to multiLevelRecyclerView
        multiLevelRecyclerView.adapter = myAdapter

        //setting onclicklistener for respective buttons for their respective modes
        view.add.setOnClickListener{
            if(t==0) {
                currentMode.updateClicked = 1
                t=1
                Toast.makeText(view.context,"ADD Mode is ON. Long Click on parent element to add",Toast.LENGTH_LONG).show()
            }
            else if(t==1){
                currentMode.updateClicked = 0
                t=0
                Toast.makeText(view.context,"ADD Mode is OFF",Toast.LENGTH_LONG).show()
            }
            else if (t==2){
                Toast.makeText(view.context,"Disable DELETE Mode First",Toast.LENGTH_LONG).show()
            }
            else if (t==3){
                Toast.makeText(view.context,"Disable RENAME Mode First",Toast.LENGTH_LONG).show()
            }
            reload()
        }
        view.delete.setOnClickListener{
            if(t==0){
                currentMode.updateClicked = 2
                t=2
                Toast.makeText(view.context,"DELETE Mode is ON. Long Click on element to delete it!",Toast.LENGTH_LONG).show()
            }
            else if(t==1){
                Toast.makeText(view.context,"Disable ADD Mode First",Toast.LENGTH_LONG).show()
            }
            else if (t==2){
                currentMode.updateClicked = 0
                t=0
                Toast.makeText(view.context,"DELETE Mode is OFF",Toast.LENGTH_LONG).show()
            }
            else if (t==3){
                Toast.makeText(view.context,"Disable RENAME Mode First",Toast.LENGTH_LONG).show()
            }
            reload()
        }
        view.rename.setOnClickListener {
            if(t==0) {
                currentMode.updateClicked = 3
                t=3
                Toast.makeText(view.context,"RENAME Mode is ON. Long Click on any element to rename",Toast.LENGTH_LONG).show()
            }
            else if(t==1){
                Toast.makeText(view.context,"Disable ADD Mode First",Toast.LENGTH_LONG).show()
            }
            else if (t==2){
                Toast.makeText(view.context,"Disable DELETE Mode First",Toast.LENGTH_LONG).show()
            }
            else if (t==3){
                currentMode.updateClicked = 0
                t=0
                Toast.makeText(view.context,"RENAME Mode is OFF",Toast.LENGTH_LONG).show()
            }
            reload()
        }
        //pull to refresh implementation
        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
                        reload()
                      pullToRefresh.isRefreshing = false
        }
        return view

    }

    fun firstread () {
        //adding child event listener on Root Node of our Database tree
        dbrefer.child("StreamList").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                Toast.makeText(context,p0.toString(),Toast.LENGTH_LONG).show()
            }
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                //useless list and item , its just for filling arguments for listreader function
                var demolist = ArrayList<RecyclerViewItem>()
                val demoitem = Item(0)
                //calling listreader function
                listreader(demoitem,demolist,p0,0)

                //Updating Adapter to detect changes
                myAdapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })
    }


        // function to reload the current fragment
    fun reload () {
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }


//A recursive function to traverse a n-level hierarchical database tree
fun listreader (parent:Item,parentlist:ArrayList<RecyclerViewItem>,p0:DataSnapshot,lvl:Int) {
       val parentlist = parentlist as MutableList<Item>
        var lvl = lvl
    if (!(p0.hasChildren()) && lvl != 0 && !(p0.key.toString().equals("name"))){
            Log.i("Leaf node", p0.key.toString()+" "+parent.getText() +"at level $lvl")
        parent.setSecondText(p0.key.toString())
        }
        else if(lvl == 0){
        val Level1list = ArrayList<RecyclerViewItem>() //as MutableList<Item>
        val lvl0 = Item(lvl)
        if (p0.key.toString().equals("name")){

        }
        else if(p0.child("name").exists()){
            lvl0.setText(p0.child("name").value.toString())
            lvl0.setSecondText(p0.key.toString())
            Level0list.add(lvl0)
            var newlevel = lvl + 1
            listreader(lvl0,Level1list,p0,newlevel)
        }
        else {
            lvl0.setText(p0.key.toString())
            lvl0.setSecondText(p0.key.toString())
            Level0list.add(lvl0)
            var newlevel = lvl + 1
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
                    item.setSecondText(i.key.toString())
                }
                else {
                    item.setText(i.key.toString())
                    item.setSecondText(i.key.toString())
                }
                parentlist.add(item)
                parent.addChildren(parentlist as MutableList<RecyclerViewItem>)
                val futurelist = ArrayList<RecyclerViewItem>()
                var newlevel = lvl + 1
                listreader(item,futurelist,i,newlevel)
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
         * @return A new instance of fragment ListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}

/*
IGNORE IGNORE IGNORE

Value Event
DataSnapshot { key = StreamList, value = {Engineering={Computer Science={SEM 6={CSS=CSS, SE=SE, DWM=DWM, SPCC=SPCC, ML=ML}, SEM 3={AOA=AOA}, SEM 1={AC=AC, BEE=BEE, EM=EM, AM=AM, AP=AP}, SEM 2={AC2=AC2, AP2=AP2, SPA=SPA, AM2=AM2, ED=ED}}, Electrical Engineering={SEM 3={EX3=EX3, EX2=EX2, EX1=EX1}, SEM 4={EE=EE}, SEM 1={EE1=EE1, EE3=EE3, EE2=EE2}, SEM 2={EXE2=EXE2, EXE3=EXE3, EXE1=EXE1}}, Information Technology={SEM 3={IT=IT}, SEM 1={CS=CS, CE=CE, IT=IT}, SEM 2={CS1=CS1, IT1=IT1, CE1=CE1}}}} }
DataSnapshot { key = Engineering, value = {Computer Science={SEM 6={CSS=CSS, SE=SE, DWM=DWM, SPCC=SPCC, ML=ML}, SEM 3={AOA=AOA}, SEM 1={AC=AC, BEE=BEE, EM=EM, AM=AM, AP=AP}, SEM 2={AC2=AC2, AP2=AP2, SPA=SPA, AM2=AM2, ED=ED}}, Electrical Engineering={SEM 3={EX3=EX3, EX2=EX2, EX1=EX1}, SEM 4={EE=EE}, SEM 1={EE1=EE1, EE3=EE3, EE2=EE2}, SEM 2={EXE2=EXE2, EXE3=EXE3, EXE1=EXE1}}, Information Technology={SEM 3={IT=IT}, SEM 1={CS=CS, CE=CE, IT=IT}, SEM 2={CS1=CS1, IT1=IT1, CE1=CE1}}} }

        for ( i in 0..depth-1 ) {
            var item = Item(levelNumber)
            item.setText(String.format(Locale.ENGLISH, title, i));
            item.setSecondText(String.format(Locale.ENGLISH, title.toLowerCase(), i));
            if(depth % 2 == 0){
                item.addChildren(recursivePopulateFakeData(levelNumber + 1, depth/2) as MutableList<RecyclerViewItem>?)
            }
            //Log.i("itemt",item.toString())
            itemList.add(item)
            //Log.i("itemlistt",itemList.toString())
        }
        return itemList;
    }
    IGNORE IGNORE IGNORE

     */
/* IGNORE IGNORE IGNORE
              HARD CODED WAY TO TRAVERSE LIST
              val Level1list = ArrayList<RecyclerViewItem>() //as MutableList<Item>
               val lvl0 = Item(0)
               lvl0.setText(p0.key.toString())
               Level0list.add(lvl0)
               listreader(lvl0,Level1list,p0,1)
             for (i in p0.children) {
                       val lvl1 = Item(1)
                       lvl1.setText(i.key.toString())
                       Level1list.add(lvl1)
                       lvl0.addChildren(Level1list as MutableList<RecyclerViewItem>)
                   val Level2list = ArrayList<RecyclerViewItem>() as MutableList<Item>
                   for (j in i.children) {
                       val lvl2 = Item(2)
                       lvl2.setText(j.key.toString())
                       Level2list.add(lvl2)
                       lvl1.addChildren(Level2list as MutableList<RecyclerViewItem>)
                       val Level3list = ArrayList<RecyclerViewItem>() as MutableList<Item>
                       for (k in j.children){
                           val lvl3 = Item(3)
                           lvl3.setText(k.key.toString())
                           Level3list.add(lvl3)
                           lvl2.addChildren(Level3list as MutableList<RecyclerViewItem>)

                       }
                   }
                   listreader(lvl1,Level2list,i,2)
               }
               IGNORE IGNORE IGNORE */