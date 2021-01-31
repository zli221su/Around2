package edu.syr.around

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var vp: androidx.viewpager.widget.ViewPager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent = Intent(this, PostActivity::class.java)
        startActivity(intent)
    }

    override fun onClick(v: View?) {
        val tag: Int = v?.tag.toString().toInt()
        var i = 0

        while (i < 3){
            viewPagerTabPanel.findViewWithTag<TextView>(i).isSelected = tag == i
            i++
        }
        vp.currentItem = tag
    }
}
