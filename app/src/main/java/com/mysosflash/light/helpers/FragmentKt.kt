package com.mysosflash.light.helpers

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mysosflash.light.R

fun FragmentActivity.addFragment(fragment: Fragment?) {
    fragment?.let {
        supportFragmentManager.beginTransaction()
            .add(R.id.container, it, it::class.java.simpleName)
            .commitAllowingStateLoss()
    }
}

fun Fragment.addFragment(fragment: Fragment?) {
    fragment?.let {
        childFragmentManager.beginTransaction()
            .add(R.id.container, it, it::class.java.simpleName)
            .commitAllowingStateLoss()
    }
}
