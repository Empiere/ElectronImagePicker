package com.electron.electron_image_picker.model

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

data class Image(
    val id: Long,
    val name: String,
    val thumbnail: Bitmap,
    val parentFolderName: String,
    val size: Long,
    val contentUri: Uri,
    val filePath: String
) {
    fun toElectronImage(context: Context) =
        ElectronImage(
            id = this.id,
            name = this.name,
            sizeInBytes = this.size,
            contentUri = this.contentUri.toString(),
            filePath = this.filePath,
            formattedSize = android.text.format.Formatter.formatFileSize(context, this.size)
        )
}
