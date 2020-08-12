package com.app.hardik.studypdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


//This fragment is for admin panel where admin can access block user , pdf cost change , delete pdf and sign out


/**
 * A simple [Fragment] subclass.
 * Use the [SettingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SettingFragment : Fragment() {

    lateinit var logout : Button
    lateinit var block : Button
    lateinit var delete : Button
    lateinit var cost : Button


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
        val view: View = inflater!!.inflate(R.layout.fragment_setting, container, false)

        logout = view.findViewById(R.id.logoutadmin)
        block = view.findViewById(R.id.block)
        delete = view.findViewById(R.id.delete)
        cost = view.findViewById(R.id.changecost)

        //Intents to move to the page after clicking Respective buttons
        logout.setOnClickListener {
            activity!!.getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedin", false).apply()
            activity!!.getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                .putString("Flag","Null").apply()
            startActivity(Intent(view.context,LoginPage::class.java))
           // startActivity(Intent(view.context,LoginPage::class.java))
        }

        block.setOnClickListener {
            val intent = Intent(view.context,Adminsettings::class.java)
            startActivity(intent)
        }
        delete.setOnClickListener {
            val intent = Intent(view.context,Deletepdf::class.java)
            startActivity(intent)
        }

        cost.setOnClickListener {
            val intent = Intent(view.context,Changecost::class.java)
            startActivity(intent)
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
         * @return A new instance of fragment SettingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SettingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
