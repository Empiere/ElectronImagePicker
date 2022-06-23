package com.electron.electron_image_picker.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.RecyclerView
import com.electron.electron_image_picker.R
import com.electron.electron_image_picker.adapter.ImagesAdapter
import com.electron.electron_image_picker.adapter.MyItemDetailsLookup
import com.electron.electron_image_picker.adapter.MyItemKeyProvider
import com.electron.electron_image_picker.databinding.ActivityElectronImagePickerBinding
import com.electron.electron_image_picker.model.ElectronImage
import com.electron.electron_image_picker.util.*
import com.electron.electron_image_picker.viewmodel.ElectronImagePickerViewModel
import com.google.gson.Gson

private const val TAG = "ElectronImagePicker"

class ElectronImagePickerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityElectronImagePickerBinding
    private val viewModel: ElectronImagePickerViewModel by viewModels { SavedStateViewModelFactory(application, this, intent.extras) }
    private lateinit var contentObserver: ContentObserver
    private var tracker: SelectionTracker<Long>? = null
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElectronImagePickerBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setContentObserver()
        setUpRecyclerView()
        setUpViews()
        observeData()
    }

    private fun setContentObserver() {
        contentObserver = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                if (ContextCompat.checkSelfPermission(
                        this@ElectronImagePickerActivity,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    Log.i(TAG, "onChange: initContentObserver called")
                    viewModel.loadPhotosFromExternalStorage()
                }
            }
        }
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            true,
            contentObserver
        )
    }

    private fun setUpRecyclerView() {
        imagesAdapter = ImagesAdapter()
        binding.imagesRv.adapter = imagesAdapter

        tracker = SelectionTracker.Builder(
            "ImagePickerId",
            binding.imagesRv,
            MyItemKeyProvider(imagesAdapter),
            MyItemDetailsLookup(binding.imagesRv),
            StorageStrategy.createLongStorage()
        ).withSelectionPredicate(
            if (viewModel.selectionType == SelectionType.SINGLE)
                SelectionPredicates.createSelectSingleAnything()
            else
                SelectionPredicates.createSelectAnything()
        ).build()
        imagesAdapter.tracker = tracker
    }

    private fun setUpViews() {
        binding.apply {

            if (viewModel.selectionType == SelectionType.SINGLE)
                submitBtn.setGone()

            updateSelectionInfo()

            cancelBtn.setOnClickListener {
                if (tracker?.selection?.isEmpty == false) {
                    tracker?.clearSelection()
                } else {
                    setResultCancel("User Closed Image Picker.")
                }
            }

            submitBtn.setOnClickListener {
                setResultOk()
            }

            swipeRefreshLayout.setOnRefreshListener {
                try {
                    if (spinner.selectedItem.toString() != GALLERY) {
                        tracker?.clearSelection()
                    }
                } catch (e: Exception) {
                }
                viewModel.loadPhotosFromExternalStorage()
            }
        }
    }

    private fun observeData() {
        tracker?.addObserver(object : SelectionTracker.SelectionObserver<Long>() {
            override fun onSelectionChanged() {
                tracker?.let {
                    binding.apply {
                        spinner.isEnabled = it.selection.isEmpty
                        spinner.alpha = if (spinner.isEnabled) 1f else 0.5f
                    }
                    updateSelectionInfo()
                    if (it.selection.size() >= viewModel.maxMultiSelectionSize.value) {
                        setResultOk()
                    }
                    super.onSelectionChanged()
                }
            }
        })

        imagesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(
                positionStart: Int,
                itemCount: Int
            ) {
                binding.imagesRv.smoothScrollToPosition(0)
            }
        })

        viewModel.images.observe(this) {
            binding.apply {
                when (it) {
                    is Resource.Error -> {
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.setGone()
                        spinnerCv.setGone()
                        messageTxt.setVisible()
                        messageTxt.text = it.message
                    }
                    is Resource.Loading -> {
                        progressBar.setVisible()
                        spinnerCv.setGone()
                        messageTxt.setGone()
                    }
                    is Resource.Success -> {
                        setSpinner()
                        swipeRefreshLayout.isRefreshing = false
                        progressBar.setGone()
                        messageTxt.setGone()
                        spinnerCv.setVisible()

                        if (!it.data.isNullOrEmpty()) {
                            imagesAdapter.submitList(it.data)
                            imagesRv.smoothScrollToPosition(1)
                        } else {
                            spinnerCv.setGone()
                            binding.messageTxt.setVisible()
                            binding.messageTxt.text = getString(R.string.no_image_found)
                        }
                    }
                }
            }
        }
    }

    private fun setSpinner() {
        val spinnerAdapter = ArrayAdapter(this, R.layout.item_spinner_list, viewModel.parentFolderNameSet)
        spinnerAdapter.setDropDownViewResource(R.layout.item_spinner_list)
        binding.spinner.adapter = spinnerAdapter


        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val folderName = viewModel.parentFolderNameSet[p2]
                Log.i(TAG, "setSpinner - onFolderItemSelected: $folderName")

                viewModel.images.value?.data?.let { list ->
                    imagesAdapter.submitList(
                        if (folderName == GALLERY)
                            list
                        else
                            list.filter { it.parentFolderName == folderName }
                    )
                } ?: kotlin.run {
                    binding.messageTxt.setVisible()
                    binding.messageTxt.text = getString(R.string.something_went_wrong)
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                Log.i(TAG, "setSpinner - onNothingSelected")
            }
        }
    }

    private fun updateSelectionInfo() {
        binding.selectionInfoTxt.text = "${tracker?.selection?.size() ?: 0}/${viewModel.maxMultiSelectionSize.value}"
    }

    private fun setResultOk() {
        tracker?.let { selectionTracker ->
            if (selectionTracker.selection.isEmpty) {
                setResultCancel("User did not select any images.")
            } else {
                val electronImages: List<ElectronImage> = imagesAdapter.currentList.filter {
                    selectionTracker.selection.contains(it.id)
                }.map {
                    it.toElectronImage(this)
                }

                val intent = Intent()
                intent.putExtra(MESSAGE, "User selected ${electronImages.size} images.")
                intent.putExtra(ELECTRON_IMAGES_DATA, Gson().toJson(electronImages))
                setResult(RESULT_OK, intent)
                finish()
            }
        } ?: setResultCancel("Image Picker Selection Failed")
    }

    private fun setResultCancel(message: String) {
        val intent = Intent()
        intent.putExtra(MESSAGE, message)
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker?.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        contentResolver.unregisterContentObserver(contentObserver)
        super.onDestroy()
    }
}