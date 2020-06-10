package com.mysosflash.light

import android.annotation.TargetApi
import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Handler
import com.mysosflash.light.bean.Events
import com.squareup.otto.Bus

internal class MarshmallowCamera constructor(val context: Context) {
    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var cameraId: String? = null

    init {
        try {
            cameraId = manager.cameraIdList[0] ?: "0"
        } catch (ignored: Exception) {
        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    fun toggleMarshmallowFlashlight(bus: Bus, enable: Boolean) {
        try {
            manager.setTorchMode(cameraId!!, enable)
        } catch (e: java.lang.Exception) {
            val mainRunnable = Runnable {
                bus.post(Events.CameraUnavailable())
            }
            Handler(context.mainLooper).post(mainRunnable)
        }
    }
}