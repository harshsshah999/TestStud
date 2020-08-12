package com.app.hardik.studypdf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//This Fragment Shows the Current User's profile details , also signout option is present here

/**
 * A simple [Fragment] subclass.
 * Use the [UserProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserProfileFragment : Fragment() {

    lateinit var database: FirebaseDatabase
    lateinit var databaseReference: DatabaseReference
    lateinit var userauth : FirebaseAuth
    lateinit var username : TextView
    lateinit var email : TextView
    lateinit var education_edittext : EditText
    lateinit var education_textview : TextView
    lateinit var education_value : String
    lateinit var logout : Button
    private lateinit var spinner: ProgressBar

    var user : FirebaseUser? = null
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
        val view: View = inflater!!.inflate(R.layout.fragment_user_profile, container, false)

        username = view.findViewById(R.id.Username)
        email = view.findViewById(R.id.Email)
        education_edittext = view.findViewById(R.id.EducationEditText)
        education_textview = view.findViewById(R.id.EducationTextView)
        spinner = view.findViewById(R.id.progressBar1)
        logout = view.findViewById(R.id.logout)
        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference()
        userauth = FirebaseAuth.getInstance()
        user = userauth.currentUser
        email.text = user!!.email

        spinner.visibility = View.VISIBLE


        view.setOnClickListener{
            it.hideKeyboard()
        }

        logout.setOnClickListener {
            activity!!.getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                .putBoolean("isLoggedin", false).apply()
            activity!!.getSharedPreferences("Loggedin", Context.MODE_PRIVATE).edit()
                .putString("Flag","Null").apply()

            //decrease count of device while logout
            device_count = (device_count.toInt() - 1).toString()
            databaseReference.child("Auth").child("AllUsers").child(user!!.uid).child("LoggedInDevice").setValue(device_count)

            startActivity(Intent(view.context,LoginPage::class.java))
        }

        databaseReference.child("Users").child("Students").child(user!!.uid).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                username.text  = p0.child("Username").value.toString()
                if(p0.child("Education").exists()){                     //Check if user has already enter education field
                    spinner.visibility = View.GONE
                    education_edittext.visibility = View.GONE
                    education_textview.visibility = View.VISIBLE
                    education_textview.text = p0.child("Education").value.toString()
                }
                else{
                    education_edittext.visibility = View.VISIBLE
                    spinner.visibility = View.GONE
                    education_edittext.setOnEditorActionListener { v, actionId, event ->
                        if(actionId == EditorInfo.IME_ACTION_DONE && !education_edittext.text.isNullOrEmpty()){              //If Enter is clicked and the field is not empty
                            education_edittext.visibility = View.GONE
                            education_textview.visibility = View.VISIBLE

                            education_value = education_edittext.text.toString()

                            //Adding value in firebase
                            databaseReference.child("Users").child("Students").child(user!!.uid).child("Education").setValue(education_value)

                            education_textview.text = education_value
                            view.hideKeyboard()
                            true
                        } else {
                            Toast.makeText(view.context, "You can't Leave a Field Empty!", Toast.LENGTH_LONG).show()
                            false
                        }
                    }

                }
            }
        })


        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UserProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UserProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    fun View.hideKeyboard() {
        val inputMethodManager = context!!.getSystemService(android.content.Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(this.windowToken, 0)
    }
}
