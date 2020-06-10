package com.mysosflash.light

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mysosflash.light.base.BaseActivity
import com.mysosflash.light.fragment.HomeFragment
import com.mysosflash.light.fragment.MineFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    //标题
    private val titles = arrayOf("首页",
//        "资讯",
        "我的")
    private val icons = arrayOf(
        R.drawable.tab_home_selector,
//        R.drawable.tab_news_selector,
        R.drawable.tab_mine_selector
    )
    private val fragmentList: MutableList<Fragment> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    /**
     * 初始化控件
     */
    private fun initViews() {
        //初始化fragment
        fragmentList.add(HomeFragment())
//        fragmentList.add(NewsFragment())
        fragmentList.add(MineFragment())
        //初始化viewPage
        viewPager2!!.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragmentList.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }
        viewPager2.isUserInputEnabled = false; //true:滑动，false：禁止滑动
        viewPager2.offscreenPageLimit = 3
        val tabLayoutMediator = TabLayoutMediator(
            tabLayout,
            viewPager2,
            TabLayoutMediator.TabConfigurationStrategy { tab: TabLayout.Tab, position: Int ->
                tab.text = titles[position]
                tab.icon = ContextCompat.getDrawable(this, icons[position])
            })
        tabLayoutMediator.attach()
    }
}