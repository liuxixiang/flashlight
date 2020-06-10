package com.mysosflash.light.helpers

import android.os.Build
import android.os.Looper

fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isOnMainThread() = Looper.myLooper() == Looper.getMainLooper()
