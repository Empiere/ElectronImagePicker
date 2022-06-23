package com.electron.electron_image_picker.util

import com.electron.electron_image_picker.model.ElectronImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

internal const val GALLERY = "Gallery"

/**
 * For User Customization
 * */

const val SELECTION_TYPE = "SELECTION_TYPE"
const val MAX_MULTI_SELECTION_SIZE = "MAX_MULTI_SELECTION_SIZE"

const val MAX_IMAGE_SIZE_IN_BYTE = "MAX_IMAGE_SIZE_IN_BYTE"
const val MIN_IMAGE_SIZE_IN_BYTE = "MIN_IMAGE_SIZE_IN_BYTE"

const val MESSAGE = "MESSAGE"
const val ELECTRON_IMAGES_DATA = "ELECTRON_IMAGES_DATA"


enum class SelectionType() {
    SINGLE,
    MULTIPLE
}

enum class MaxMultiSelectionSize(val value: Int) {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7),
    EIGHT(8),
    NINE(9),
    TEN(10),
}

fun getBytesFromMB(mb: Float) = (mb * 1048576).toString()
fun getBytesFromKB(kb: Float) = (kb * 1024).toString()


val type: Type = object : TypeToken<List<ElectronImage>>() {}.type
fun getElectronImageListFromData(data: String): List<ElectronImage> {
    return Gson().fromJson(data, type)
}