package com.mysosflash.light.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mysosflash.light.Config
import com.mysosflash.light.R
import com.mysosflash.light.WebActivity
import com.mysosflash.light.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_mine.*

class MineFragment : BaseFragment(R.layout.fragment_mine) {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
//        layout_version.setOnClickListener {
//            activity!!.toast("您的应用已是最新版本")
//        }

        layout_agreement.setOnClickListener {
            WebActivity.startActivity(context!!, Config.AGREEMENT)
        }

        layout_policy.setOnClickListener {
            WebActivity.startActivity(context!!, Config.PRIVACY_POLICY)
        }

    }
}