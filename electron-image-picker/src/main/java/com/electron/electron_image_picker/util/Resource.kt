package com.electron.electron_image_picker.util

/**
 * CREATED BY Aagam Koradiya on 20-06-2022
 */

sealed class Resource<T>(val data: T?, val message: String?) {
    class Loading<T>() : Resource<T>(null, null)
    class Success<T>(data: T) : Resource<T>(data, null)
    class Error<T>(message: String) : Resource<T>(null, message)
}