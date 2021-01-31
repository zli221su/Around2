package edu.syr.around

import android.util.Log
import androidx.fragment.app.Fragment

class ProfilePagerAdapter(fragmentManager: androidx.fragment.app.FragmentManager) :
    androidx.fragment.app.FragmentPagerAdapter(fragmentManager) {
    override fun getItem(p0: Int): Fragment {
        Log.i("SimplePagerAdapter", "item index $p0" )
        when(p0) {
//            0 -> return PostFragment.newInstance()
            0 -> return ZhiProfileFragment.newInstance()
            1 -> return LuqiProfileFragment.newInstance()
        }
        return ZhiProfileFragment.newInstance()
    }

    override fun getCount(): Int {
        return 2
    }
}