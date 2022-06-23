package com.electron.electron_image_picker.viewmodel

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentUris
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.electron.electron_image_picker.model.Image
import com.electron.electron_image_picker.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

private const val TAG = "ElectronImagePickerView"

class ElectronImagePickerViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    @SuppressLint("StaticFieldLeak")
    private val context = application.applicationContext
    private val width: Int = context.resources.displayMetrics.widthPixels

    private val _images = MutableLiveData<Resource<List<Image>>>()
    val images: LiveData<Resource<List<Image>>> = _images

    private val _parentFolderNameSet = mutableSetOf<String>()
    val parentFolderNameSet get() = _parentFolderNameSet.toTypedArray()

    val selectionType: SelectionType = savedStateHandle.get<SelectionType>(SELECTION_TYPE) ?: SelectionType.SINGLE
    val maxMultiSelectionSize: MaxMultiSelectionSize =
        if (selectionType == SelectionType.MULTIPLE)
            savedStateHandle.get<MaxMultiSelectionSize>(MAX_MULTI_SELECTION_SIZE) ?: MaxMultiSelectionSize.TWO
        else
            MaxMultiSelectionSize.ONE
    private val minImageSizeInBytes = savedStateHandle.get<String>(MIN_IMAGE_SIZE_IN_BYTE) ?: getBytesFromKB(1f)
    private val maxImageSizeInBytes = savedStateHandle.get<String>(MAX_IMAGE_SIZE_IN_BYTE) ?: getBytesFromMB(5f)

    init {
        loadPhotosFromExternalStorage()
    }

    fun loadPhotosFromExternalStorage() {
        Log.i(TAG, "loadPhotosFromExternalStorage: called")

        if (!isReadExternalStoragePermissionGranted()) {
            _images.postValue(Resource.Error("Don't have Read External Storage Permission.\nPlease enable it from settings after refresh the page"))
            return
        }

        _images.postValue(Resource.Loading())
        _parentFolderNameSet.clear()
        _parentFolderNameSet.add(GALLERY)

        viewModelScope.launch(Dispatchers.IO) {

            val collection = sdk30AndUp {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
            } ?: MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE,
            )

            val images = mutableListOf<Image>()
            context.contentResolver.query(
                collection,
                projection,
                "${MediaStore.Images.Media.SIZE} >= ? and ${MediaStore.Images.Media.SIZE} <= ?",
                arrayOf(minImageSizeInBytes, maxImageSizeInBytes),
                "${MediaStore.Images.Media.DATE_MODIFIED} DESC"
            )?.use { cursor ->

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val size = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(displayNameColumn)
                    val data = cursor.getString(dataColumn)
                    val imageSize = cursor.getLong(size)
//                    Log.i(TAG, "loadPhotosFromExternalStorage: size - $imageSize : " + android.text.format.Formatter.formatFileSize(context, imageSize))

                    if (imageSize > 0L) {
                        val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                        val thumbnails: Bitmap = sdk29AndUp(
                            onSdk29AndUp = {
                                context.contentResolver.loadThumbnail(contentUri, Size(width / 4, width / 4), null)
                            },
                            onSdk28AndDown = {
                                MediaStore.Images.Thumbnails.getThumbnail(context.contentResolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
                            }
                        )
                        val parentFolderName = data.substringBeforeLast("/").substringAfterLast("/")
                        _parentFolderNameSet.add(parentFolderName)

                        images.add(
                            Image(
                                id = id,
                                name = displayName,
                                contentUri = contentUri,
                                filePath = data,
                                thumbnail = thumbnails,
                                parentFolderName = parentFolderName,
                                size = imageSize
                            )
                        )
                    }

                }
                _images.postValue(Resource.Success(images.toList()))
            } ?: _images.postValue(Resource.Error("Something went wrong!"))
        }
    }

    private fun isReadExternalStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}