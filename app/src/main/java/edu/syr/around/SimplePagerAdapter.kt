package edu.syr.around

import android.util.Log
import androidx.fragment.app.Fragment

class SimplePagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) :
        androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
    override fun getItem(p0: Int): Fragment {
        Log.i("SimplePagerAdapter", "item index $p0" )
        when(p0) {
//            0 -> return PostFragment.newInstance()
            0 -> return PostMapFragment.newInstance()
            1 -> return PostListFragment.newInstance()
            2 -> return SelfProfileFragment.newInstance()
        }
        return PostMapFragment.newInstance()
    }

    override fun getCount(): Int {
        return 3
    }
}