package com.electron.electron_image_picker.model

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

data class ElectronImage(
    val id: Long,
    val name: String,
    val sizeInBytes: Long,
    val formattedSize: String,
    val contentUri: String,
    val filePath: String
)
