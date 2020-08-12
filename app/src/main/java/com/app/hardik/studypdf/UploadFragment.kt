package com.app.hardik.studypdf


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.multilevelview.MultiLevelRecyclerView
import com.multilevelview.models.RecyclerViewItem
import kotlinx.android.synthetic.main.fragment_upload.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/* Everything Related to MultiRecyclerview (Comments stuff) can be found in ListFragment.kt file
as the code related to it is exactly same
* */

//This Fragment shows N level hierarchical Database tree similar to ListFragment.kt
//You can select leaf node and Press Upload button for further upload procedure


//Global function to be used in both UploadFragment and UploadAdapter
object activate {
    var isClickable : Int = 1
    var title : String = ""
    var currentpath: String = ""
}
/**
 * A simple [Fragment] subclass.
 * Use the [UploadFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UploadFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var dbrefer: DatabaseReference
    lateinit var db: FirebaseDatabase
    lateinit var auth: FirebaseAuth
    lateinit var myAdapter: UploadAdapter
    lateinit var Level0list: MutableList<Item>
    lateinit var gotoupload: Button

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
        var view = inflater.inflate(R.layout.fragment_upload, container, false)
        val multiLevelRecyclerView =
                view.findViewById(R.id.rv_list) as MultiLevelRecyclerView
        multiLevelRecyclerView.layoutManager = LinearLayoutManager(this.context)
        //Default Element to include
        Level0list = ArrayList<RecyclerViewItem>() as MutableList<Item>
        gotoupload = view.findViewById<Button>(R.id.gotoupload);

        var item = Item(0)
        //default
        if(activate.isClickable == 1) {
            item.setText("Choose Category of File!")
            item.setSecondText("Long Click on Subject to Select it. (You can Only Select Subject!)")
            view.gotoupload.setActivated(false)
            gotoupload.setEnabled(false)
        }
        //if any subject is selected
        else if (activate.isClickable == 0){
            item.setText("You have Selected "+activate.title+" .")
            item.setSecondText("Long Click Here to Cancel and select other subject")
            view.gotoupload.setActivated(true)
            gotoupload.setEnabled(true)
            gotoupload.setBackgroundResource(R.drawable.my_bg_btn)
            gotoupload.setTextColor(Color.rgb(255,160,0))
        }
        Level0list.add(item)
        db= FirebaseDatabase.getInstance()
        dbrefer=db.getReference()
        readlist()
        myAdapter = UploadAdapter(view.context, Level0list, multiLevelRecyclerView,UploadFragment())
        multiLevelRecyclerView.adapter = myAdapter

            //pull to refresh
        val pullToRefresh: SwipeRefreshLayout = view.findViewById(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            reload()
            pullToRefresh.isRefreshing = false
        }
        view.refresh.setOnClickListener{
            reload()
        }
        view.gotoupload.setOnClickListener{
            if(view.gotoupload.isActivated) {
                //sending intent of selected node name and its database path
                val intent = Intent(getActivity(), Uploadsection::class.java)
                intent.putExtra("name",activate.title)
                intent.putExtra("path",activate.currentpath)
                getActivity()!!.startActivity(intent)
            }
        }

        return view
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UploadFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UploadFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun readlist (){
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
    fun listreader (parent:Item,parentlist:ArrayList<RecyclerViewItem>,p0:DataSnapshot,lvl:Int) {
        val parentlist = parentlist as MutableList<Item>
        var lvl = lvl
        if (!(p0.hasChildren()) && lvl != 0 && !(p0.key.toString().equals("name"))){
            Log.i("Leaf node", p0.key.toString()+" at level $lvl")
            parent.setSecondText("__")
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
                parentlist.add(item)
                parent.addChildren(parentlist as MutableList<RecyclerViewItem>)
                val futurelist = ArrayList<RecyclerViewItem>()
                var newlevel = lvl + 1
                listreader(item,futurelist,i,newlevel)
            }
        }
    }
    fun reload () {
        val ft: FragmentTransaction = fragmentManager!!.beginTransaction()
        if (Build.VERSION.SDK_INT >= 26) {
            ft.setReorderingAllowed(false)
        }
        ft.detach(this).attach(this).commit()
    }

}
