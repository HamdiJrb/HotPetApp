package tn.esprit.smartpet.view

import android.R
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import tn.esprit.smartpet.adapters.SlideAdapter


class OnBoarding : AppCompatActivity() {
    //Variables
    var viewPager: ViewPager? = null
    var dotsLayout: LinearLayout? = null
    var sliderAdapter: SlideAdapter? = null
    lateinit var dots: Array<TextView?>
    var letsGetStarted: Button? = null
    var currentPos = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(tn.esprit.smartpet.R.layout.activity_on_boarding)

        //Hooks
        viewPager = findViewById(tn.esprit.smartpet.R.id.slider)
        dotsLayout = findViewById(tn.esprit.smartpet.R.id.dots)
        letsGetStarted = findViewById(tn.esprit.smartpet.R.id.get_started_btn)

        //Call adapter
        sliderAdapter = SlideAdapter(this)
        with(viewPager) { this?.setAdapter(sliderAdapter) }

        //Dots
        addDots(0)
        with(viewPager) { this?.addOnPageChangeListener(changeListener) }

        letsGetStarted!!.setOnClickListener {
            startActivity(Intent(this, Signin::class.java))
            finish()
        }

    }
// Skip
    fun skip(view: View?) {
        startActivity(Intent(this, Signin::class.java))
        finish()
    }

    fun next(view: View?) {
        viewPager!!.currentItem = currentPos + 1
    }

    private fun addDots(position: Int) {
        dots = arrayOfNulls(4)
        dotsLayout!!.removeAllViews()
        for (i in dots.indices) {
            dots[i] = TextView(this)
            dots[i]!!.text = Html.fromHtml("&#8226;")
            dots[i]!!.textSize = 35f
            dotsLayout!!.addView(dots[i])
        }
        if (dots.size > 0) {
            dots[position]!!.setTextColor(resources.getColor(tn.esprit.smartpet.R.color.primaryColor))
        }
    }

    var changeListener: OnPageChangeListener = object : OnPageChangeListener {
        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {
        }

        override fun onPageSelected(position: Int) {
            addDots(position)
            currentPos = position
            if (position == 0) {
                letsGetStarted?.setVisibility(View.INVISIBLE)
            } else if (position == 1) {
                letsGetStarted?.setVisibility(View.INVISIBLE)
            } else if (position == 2) {
                letsGetStarted?.setVisibility(View.INVISIBLE)
            }
            else  {
                letsGetStarted?.setVisibility(View.VISIBLE)
            }
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }


}