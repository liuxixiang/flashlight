package com.mysosflash.light.helpers

import android.graphics.PorterDuff
import android.graphics.drawable.Drawable

fun Drawable.applyColorFilter(color: Int) = mutate().setColorFilter(color, PorterDuff.Mode.SRC_IN)