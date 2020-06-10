package com.mysosflash.light.fragment

import android.Manifest
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import com.mysosflash.light.R
import com.mysosflash.light.base.BaseActivity
import com.mysosflash.light.base.BaseFragment
import com.mysosflash.light.helpers.toast
import com.mysosflash.light.helpers.BusProvider
import com.mysosflash.light.helpers.applyColorFilter
import com.mysosflash.light.helpers.isNougatPlus
import com.mysosflash.light.bean.Events
import com.simplemobiletools.flashlight.helpers.MyCameraImpl
import com.squareup.otto.Bus
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private var mBus: Bus? = null
    private var mCameraImpl: MyCameraImpl? = null
    private var mIsFlashlightOn = false
    private lateinit var mActivity: BaseActivity

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity as BaseActivity
        mBus = BusProvider.instance
        flashlight_btn.setOnClickListener {
            mCameraImpl!!.toggleFlashlight()
        }
        sos_btn.setOnClickListener {
            toggleStroboscope(true)
        }
    }


    override fun onResume() {
        super.onResume()
        mCameraImpl!!.handleCameraSetup()
        checkState(MyCameraImpl.isFlashlightOn)
    }

    override fun onStart() {
        super.onStart()
        mBus!!.register(this)

        if (mCameraImpl == null) {
            setupCameraImpl()
        }
    }

    override fun onStop() {
        super.onStop()
        mBus!!.unregister(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }

    private fun setupCameraImpl() {
        mCameraImpl = activity?.let {
            MyCameraImpl.newInstance(
                it
            )
        }
    }

    private fun releaseCamera() {
        mCameraImpl?.releaseCamera()
        mCameraImpl = null
    }


    private fun checkState(isEnabled: Boolean) {
        if (isEnabled) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    private fun enableFlashlight() {
        changeIconColor(Color.YELLOW, flashlight_btn)
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = true
    }

    private fun disableFlashlight() {
        changeIconColor(Color.WHITE, flashlight_btn)
        mActivity!!.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        mIsFlashlightOn = false
    }

    private fun toggleStroboscope(isSOS: Boolean) {
        if (isNougatPlus()) {
            cameraPermissionGranted(isSOS)
        } else {
            mActivity!!.handlePermission(Manifest.permission.CAMERA) {
                if (it) {
                    cameraPermissionGranted(isSOS)
                } else {
                    activity!!.toast(R.string.camera_permission)
                }
            }
        }
    }

    private fun cameraPermissionGranted(isSOS: Boolean) {
        if (isSOS) {
            val isSOSRunning = mCameraImpl!!.toggleSOS()
            sos_btn.setTextColor(if (isSOSRunning) Color.YELLOW else Color.WHITE)
        } else {
            mCameraImpl!!.toggleStroboscope()
        }
    }


    private fun changeIconColor(color: Int, imageView: ImageView?) {
        imageView!!.background.mutate().applyColorFilter(color)
    }

    @Subscribe
    fun stateChangedEvent(event: Events.StateChanged) {
        checkState(event.isEnabled)
        sos_btn.setTextColor(Color.WHITE)
    }

    @Subscribe
    fun stopSOS(event: Events.StopSOS) {
        sos_btn.setTextColor(Color.WHITE)
    }

}