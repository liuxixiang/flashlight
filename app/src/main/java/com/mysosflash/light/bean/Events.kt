package com.mysosflash.light.bean

class Events {
    class StateChanged(val isEnabled: Boolean)
    class CameraUnavailable
    class StopStroboscope
    class StopSOS
}