package edu.syr.around

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_author.*

class AuthorActivity :
    AppCompatActivity(),
    View.OnClickListener {

    private lateinit var vp: androidx.viewpager.widget.ViewPager
    private var pageNum = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author)

        // View Pager

        vp = findViewById(R.id.viewPagerContainer)
        vp.adapter = ProfilePagerAdapter(supportFragmentManager)
        vp.currentItem = 0

        txtPagerSelectZhi.setOnClickListener(this)
        txtPagerSelectZhi.tag = 0
        txtPagerSelectZhi.isSelected = true
        txtPagerSelectLu.setOnClickListener(this)
        txtPagerSelectLu.tag = 1

        vp.addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(p0: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
//                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageSelected(p0: Int) {
                var i = 0
                while (i < pageNum){
                    txtPanel.findViewWithTag<TextView>(i).isSelected = p0 == i
                    i++
                }
            }
        })

        vp.setPageTransformer(false) { p0, p1 ->
            val norm = Math.abs(Math.abs(p1) - 1)
            p0.scaleX = norm/2 + 0.5f
            p0.scaleY = norm/2 + 0.5f
        }
    }

    override fun onClick(v: View?) {
        val tag: Int = v?.tag.toString().toInt()
        var i = 0

        while (i < pageNum){
            txtPanel.findViewWithTag<TextView>(i).isSelected = tag == i
            i++
        }
        vp.currentItem = tag
    }
}
