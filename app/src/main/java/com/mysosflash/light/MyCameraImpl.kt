package com.simplemobiletools.flashlight.helpers

import android.content.Context
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Handler
import com.mysosflash.light.MarshmallowCamera
import com.mysosflash.light.R
import com.mysosflash.light.helpers.toast
import com.mysosflash.light.helpers.BusProvider
import com.mysosflash.light.helpers.isMarshmallowPlus
import com.mysosflash.light.helpers.isNougatPlus
import com.mysosflash.light.bean.Events
import com.squareup.otto.Bus
import java.io.IOException

class MyCameraImpl(val context: Context) {
    var stroboFrequency = 1000L

    companion object {
        var isFlashlightOn = false
        private val SOS = arrayListOf(
            250L,
            250L,
            250L,
            250L,
            250L,
            250L,
            500L,
            250L,
            500L,
            250L,
            500L,
            250L,
            250L,
            250L,
            250L,
            250L,
            250L,
            1000L
        )

        private var camera: Camera? = null
        private var params: Camera.Parameters? = null
        private var bus: Bus? = null
        private var isMarshmallow = false
        private var shouldEnableFlashlight = false
        private var isStroboSOS = false     // are we sending SOS, or casual stroboscope?

        private var marshmallowCamera: MarshmallowCamera? = null
        @Volatile
        private var shouldStroboscopeStop = false
        @Volatile
        private var isStroboscopeRunning = false
        @Volatile
        private var isSOSRunning = false

        fun newInstance(context: Context) = MyCameraImpl(context)
    }

    init {
        isMarshmallow = isMarshmallowPlus()

        if (bus == null) {
            bus = BusProvider.instance
            bus!!.register(this)
        }

        handleCameraSetup()
    }

    fun toggleFlashlight() {
        isFlashlightOn = !isFlashlightOn
        checkFlashlight()
    }

    fun toggleStroboscope(): Boolean {
        if (isSOSRunning) {
            stopSOS()
            return false
        }

        isStroboSOS = false
        if (!isStroboscopeRunning) {
            disableFlashlight()
        }

        if (!tryInitCamera()) {
            return false
        }

        return if (isStroboscopeRunning) {
            stopStroboscope()
            false
        } else {
            Thread(stroboscope).start()
            true
        }
    }

    fun stopStroboscope() {
        shouldStroboscopeStop = true
        bus!!.post(Events.StopStroboscope())
    }

    fun toggleSOS(): Boolean {
        if (isStroboscopeRunning) {
            stopStroboscope()
            return false
        }

        isStroboSOS = true
        if (isStroboscopeRunning) {
            stopStroboscope()
        }

        if (!tryInitCamera()) {
            return false
        }

        if (isFlashlightOn) {
            disableFlashlight()
        }

        return if (isSOSRunning) {
            stopSOS()
            false
        } else {
            Thread(stroboscope).start()
            true
        }
    }

    fun stopSOS() {
        shouldStroboscopeStop = true
        bus!!.post(Events.StopSOS())
    }

    private fun tryInitCamera(): Boolean {
        if (!isNougatPlus()) {
            if (camera == null) {
                initCamera()
            }

            if (camera == null) {
                context.toast(R.string.camera_error)
                return false
            }
        }
        return true
    }

    fun handleCameraSetup() {
        if (isMarshmallow) {
            setupMarshmallowCamera()
        } else {
            setupCamera()
        }
    }

    private fun setupMarshmallowCamera() {
        if (marshmallowCamera == null) {
            marshmallowCamera = MarshmallowCamera(context)
        }
    }

    private fun setupCamera() {
        if (isMarshmallow) {
            return
        }

        if (camera == null) {
            initCamera()
        }
    }

    private fun initCamera() {
        try {
            camera = Camera.open()
            params = camera!!.parameters
            params!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera!!.parameters = params
        } catch (e: Exception) {
            bus!!.post(Events.CameraUnavailable())
        }
    }

    private fun checkFlashlight() {
        if (camera == null) {
            handleCameraSetup()
        }

        if (isFlashlightOn) {
            enableFlashlight()
        } else {
            disableFlashlight()
        }
    }

    fun enableFlashlight() {
        shouldStroboscopeStop = true
        if (isStroboscopeRunning || isSOSRunning) {
            shouldEnableFlashlight = true
            return
        }

        if (isMarshmallow) {
            toggleMarshmallowFlashlight(true)
        } else {
            if (camera == null || params == null) {
                return
            }

            params!!.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            camera!!.parameters = params
            camera!!.startPreview()
        }

        val mainRunnable = Runnable { stateChanged(true) }
        Handler(context.mainLooper).post(mainRunnable)
    }

    private fun disableFlashlight() {
        if (isStroboscopeRunning || isSOSRunning) {
            return
        }

        if (isMarshmallow) {
            toggleMarshmallowFlashlight(false)
        } else {
            if (camera == null || params == null) {
                return
            }

            params!!.flashMode = Camera.Parameters.FLASH_MODE_OFF
            camera!!.parameters = params
        }
        stateChanged(false)
        releaseCamera()
    }

    private fun stateChanged(isEnabled: Boolean) {
        isFlashlightOn = isEnabled
        bus!!.post(Events.StateChanged(isEnabled))
    }

    private fun toggleMarshmallowFlashlight(enable: Boolean) {
        marshmallowCamera!!.toggleMarshmallowFlashlight(bus!!, enable)
    }

    fun releaseCamera() {
        if (isFlashlightOn) {
            disableFlashlight()
        }

        camera?.release()
        camera = null

        bus?.unregister(this)
        isFlashlightOn = false
        shouldStroboscopeStop = true
    }

    private val stroboscope = Runnable {
        if (isStroboscopeRunning || isSOSRunning) {
            return@Runnable
        }

        shouldStroboscopeStop = false
        if (isStroboSOS) {
            isSOSRunning = true
        } else {
            isStroboscopeRunning = true
        }

        var sosIndex = 0
        if (isNougatPlus()) {
            while (!shouldStroboscopeStop) {
                try {
                    marshmallowCamera!!.toggleMarshmallowFlashlight(bus!!, true)
                    val onDuration =
                        if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequency
                    Thread.sleep(onDuration)
                    marshmallowCamera!!.toggleMarshmallowFlashlight(bus!!, false)
                    val offDuration =
                        if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequency
                    Thread.sleep(offDuration)
                } catch (e: Exception) {
                    shouldStroboscopeStop = true
                }
            }
        } else {
            if (camera == null) {
                initCamera()
            }

            val torchOn = camera!!.parameters
            val torchOff = camera!!.parameters
            torchOn.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            torchOff.flashMode = Camera.Parameters.FLASH_MODE_OFF

            val dummy = SurfaceTexture(1)
            try {
                camera!!.setPreviewTexture(dummy)
            } catch (e: IOException) {
            }

            camera!!.startPreview()

            while (!shouldStroboscopeStop) {
                try {
                    camera!!.parameters = torchOn
                    val onDuration =
                        if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequency
                    Thread.sleep(onDuration)
                    camera!!.parameters = torchOff
                    val offDuration =
                        if (isStroboSOS) SOS[sosIndex++ % SOS.size] else stroboFrequency
                    Thread.sleep(offDuration)
                } catch (e: Exception) {
                }
            }

            try {
                if (camera != null) {
                    camera!!.parameters = torchOff
                    if (!shouldEnableFlashlight || isMarshmallow) {
                        camera!!.release()
                        camera = null
                    }
                }
            } catch (e: RuntimeException) {
            }
        }

        shouldStroboscopeStop = false
        if (isStroboSOS) {
            isSOSRunning = false
        } else {
            isStroboscopeRunning = false
        }

        if (shouldEnableFlashlight) {
            enableFlashlight()
            shouldEnableFlashlight = false
        }
    }
}
