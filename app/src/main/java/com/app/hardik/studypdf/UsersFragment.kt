package com.app.hardik.studypdf

import android.app.ActionBar
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment



// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

//This Fragment gives list of All Users and their total revenue

/**
 * A simple [Fragment] subclass.
 * Use the [UsersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UsersFragment : Fragment() {
    var count: Int = 0
    var last: Int = 0
    lateinit var names: String
    lateinit var emails: String
    lateinit var revenue: String
    lateinit var nav_menu: Menu
    lateinit var root2:LinearLayout
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
        val view: View = inflater!!.inflate(R.layout.fragment_users, container, false)
        root2 = view.findViewById(R.id.root2)

        //Hide navigation menu untill it loads all cards
        nav_menu = bottomNavigation.menu
        nav_menu.findItem(R.id.navigation_settings).setVisible(false)
        nav_menu.findItem(R.id.navigation_upload).setVisible(false)
        nav_menu.findItem(R.id.navigation_home).setVisible(false)
        nav_menu.findItem(R.id.navigation_list).setVisible(false)
        nav_menu.findItem(R.id.navigation_users).setVisible(false)
        //Creating Cards In view
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                //Call your function here
                if(isOnline(view.context)){
                    count = menu.indexOfFirst { true }
                    last  = menu.indexOfLast { true }
                    while (count <= last){
                        names = menu.get(count).toString()
                        emails = email.get(count).toString()
                        revenue = finalcost.get(count).toString()
                        new()
                        count = count + 1
                    }
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
        }, 550)

        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment UsersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            UsersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun new() {
        // Initialize a new CardView instance

        val card_view = CardView(view!!.context)
        card_view.id = count

        // Initialize a new LayoutParams instance, CardView width and height
        val layoutParams = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT, // CardView width
            ActionBar.LayoutParams.WRAP_CONTENT // CardView height
        )

        // Set bottom margin for card view
        layoutParams.bottomMargin = 10
        layoutParams.topMargin = 40
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
            startActivity(Intent(view!!.context,ExpandCard::class.java))
        }

        // Add an TextView to the CardView
        card_view.addView(generatename())
        card_view.addView(generateemail())
        card_view.addView(generateprice())
        card_view.addView(generateImageView())
        // Finally, add the CardView in root layout
        root2.addView(card_view)
    }

    // Custom method to generate an image view
    private fun generateImageView(): ImageView {
        val imageView = ImageView(view?.context)
        val params = ActionBar.LayoutParams(55, 55)
        var width = root2.measuredWidth
        var width2 = width*15/100
        params.setMargins(width-width2,80,0,0)
        imageView.layoutParams = params
        imageView.setImageResource(R.drawable.ic_appintro_arrow_forward_white)
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        imageView.setBackgroundColor(Color.TRANSPARENT)
        return imageView
    }

    //Custom method to generate an text view
    private fun generatename(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        params.leftMargin = 10
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = names
        textView.textSize = 22F
        return textView
    }
    private fun generateemail(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        params.setMargins(10,80,0,0)
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = emails
        textView.textSize = 18F
        return textView
    }
    private fun generateprice(): TextView {
        val textView = TextView(view?.context)
        val params = ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT )
        var width = root2.measuredWidth
        var width2 = width*29/100
        params.setMargins(width-width2,0,0,0)
        textView.layoutParams = params
        textView.setTextColor(Color.WHITE)
        textView.text = "₹ " + revenue
        textView.textSize = 20F
        return textView
    }

}
