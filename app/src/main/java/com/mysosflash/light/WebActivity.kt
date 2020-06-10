package com.mysosflash.light

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import com.mysosflash.light.base.BaseActivity
import kotlinx.android.synthetic.main.activity_web.*

class WebActivity : BaseActivity() {

    companion object {
        fun startActivity(context: Context, url: String) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra("url", url)
            context.startActivity(intent)
        }
    }

    private lateinit var mWebView: WebView

    private var mUrl: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        mUrl = intent.getStringExtra("url")
        if (TextUtils.isEmpty(mUrl)) {
            finish()
            return
        }

        addWebView()
        initListener()

        mWebView.loadUrl(mUrl)
    }


    private fun addWebView() {
        mWebView = WebView(this)
        mWebView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                tv_title.text = title
            }
        }
        mWebView.webViewClient = WebViewClient()

        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        container.addView(mWebView, 0, layoutParams)
    }


    private fun initListener() {
        iv_back.setOnClickListener {
            onBackPressed()
        }
        iv_close.setOnClickListener {
            onBackPressed()
        }
    }


    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView.parent?.let {
            val viewGroup = it as ViewGroup
            viewGroup.removeView(mWebView)
        }
        mWebView.settings.javaScriptEnabled = false
        mWebView.stopLoading()
        mWebView.destroy()
    }
}
