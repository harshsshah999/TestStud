package com.app.hardik.studypdf

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

//This is Onboarding/Introscreen of our app which is only shown to user once

class Introscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introscreen)

        //Variable Declaration
        val welcomeimg = findViewById<ImageView>(R.id.imageView2)
        val welcometxt = findViewById<TextView>(R.id.textView2)
        val seek = findViewById<SeekBar>(R.id.seekBar)
        val nextbtn = findViewById<Button>(R.id.next)
        val leftslidebtn = findViewById<Button>(R.id.rightslide)
        val rightslidebtn = findViewById<Button>(R.id.leftslide)
        val skip = findViewById<Button>(R.id.skip)
        val Introlayout = findViewById<ConstraintLayout>(R.id.Introlayout)
        lateinit var animl : Animation
        lateinit var animr : Animation

        //Create Animation
        animl = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_left)
        animr = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.to_right)

        //function for animate images and texts
        fun leftanim(){
            //Trigger Left Animation
            welcomeimg.startAnimation(animl)
            welcometxt.startAnimation(animl)
            return
        }
        fun rightanim(){
            //Trigger Right Animation
            welcomeimg.startAnimation(animr)
            welcometxt.startAnimation(animr)
            return
        }
        //function to change images and texts according to seekbar progress
        fun change(){
            if (seek.progress == 0){
                welcomeimg.setImageResource(R.drawable.intro2)
                welcometxt.setText("Technology is not an event. It's just part of everyday learning")
                nextbtn.text = "Next"
                return
            }
            else{
                if (seek.progress == 1){
                    welcomeimg.setImageResource(R.drawable.intro1)
                    welcometxt.setText("Learning is MORE effective when it is ACTIVE rather than a passive process.")
                    nextbtn.text = "Next"
                    return
                }
                else{
                    if  (seek.progress == 2) {
                        welcomeimg.setImageResource(R.drawable.final1)
                        welcometxt.setText("Accessible anytime anywhere!")
                        nextbtn.text = "Next"
                        return
                    }

                } }
        }

        //function for next slide & previous slide
        fun nextslide(){
            if(seek.progress != seek.max)
            {
                seek.progress = seek.progress + 1
                nextbtn.text = "Next"
                leftanim()
                change()
            }
            if (seek.progress==seek.max)
            {
                nextbtn.text = "Done"
            }
        }
        fun previousslide(){
            if(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    seek.progress != seek.min
                } else {
                    TODO("VERSION.SDK_INT < O")
                }
            ){
                seek.progress = seek.progress - 1
                nextbtn.text = "Next"
                rightanim()
                change()
            }
        }

        //Skip button function
        fun gotologin (){
            startActivity(Intent(this,LoginPage::class.java))
            finish()
        }
        //seekbar class creation
        seek?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                //To change body of created functions use File | Settings | File Templates.
                change()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //To change body of created functions use File | Settings | File Templates.
            }
        })

        //onClick listeners for images,texts,buttons
        nextbtn.setOnClickListener {
            if(nextbtn.text=="Done")
            {
                gotologin()
            }
            nextslide()
        }
        rightslidebtn.setOnClickListener {
            previousslide()
        }
        leftslidebtn.setOnClickListener {
            nextslide()
        }
        skip.setOnClickListener{
            gotologin()
        }

        //Adding gestures to this activity
        welcomeimg.setOnTouchListener(object : OnSwipeTouchListener(this@Introscreen) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                nextslide()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                previousslide()
            }
        })
        welcometxt.setOnTouchListener(object : OnSwipeTouchListener(this@Introscreen) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                nextslide()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                previousslide()
            }
        })
        leftslidebtn.setOnTouchListener(object : OnSwipeTouchListener(this@Introscreen) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                nextslide()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                previousslide()
            }
        })
        rightslidebtn.setOnTouchListener(object : OnSwipeTouchListener(this@Introscreen) {
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                nextslide()
            }

            override fun onSwipeRight() {
                super.onSwipeRight()
                previousslide()
            }
        })
    }
}
