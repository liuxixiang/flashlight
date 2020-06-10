package com.mysosflash.light

import android.app.Application
//import com.linkin.newssdk.NewsConfig
//import com.linkin.newssdk.NewsSdk


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initNewsSdk()
    }

    private fun initNewsSdk() {
//        val newsConfig = NewsConfig.Builder()
//            .appId(Config.APP_ID)
//            .debug(BuildConfig.DEBUG)
//            .build()
//        NewsSdk.getInstance().init(this, newsConfig, null)
    }


}