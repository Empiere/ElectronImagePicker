package com.electron.electron_image_picker.util

import android.os.Build
import android.view.View

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

inline fun <T> sdk30AndUp(onSdk30: () -> T): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        onSdk30()
    } else null
}

inline fun <T> sdk29AndUp(onSdk29AndUp: () -> T, onSdk28AndDown: () -> T): T {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        onSdk29AndUp()
    } else {
        onSdk28AndDown()
    }
}

fun View.setVisible() {
    this.visibility = View.VISIBLE
}

fun View.setInVisible() {
    this.visibility = View.INVISIBLE
}

fun View.setGone() {
    this.visibility = View.GONE
}