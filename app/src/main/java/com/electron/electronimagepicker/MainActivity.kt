package com.electron.electronimagepicker

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.electron.electron_image_picker.model.ElectronImage
import com.electron.electron_image_picker.ui.ElectronImagePickerActivity
import com.electron.electron_image_picker.util.*
import com.electron.electronimagepicker.databinding.ActivityMainBinding

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    lateinit var imgAdapter: ImgAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        imgAdapter = ImgAdapter()
        binding.showImagesRv.adapter = imgAdapter

        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let {
                    val data = it.getStringExtra(ELECTRON_IMAGES_DATA)
                    val message = it.getStringExtra(MESSAGE)
                    Log.i(TAG, "RESULT_OK: Message - $message")
                    Log.i(TAG, "RESULT_OK: Data - $data")

                    data?.let { json ->
                        showResultData(getElectronImageListFromData(json))
                    }

                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                result.data?.let {
                    Log.i(TAG, "RESULT_CANCELED: Message - " + it.getStringExtra(MESSAGE))
                }
            }

        }

        binding.selectSingleImage.setOnClickListener {
            val intent = Intent(this, ElectronImagePickerActivity::class.java)
            intent.putExtra(SELECTION_TYPE, SelectionType.SINGLE)
            intent.putExtra(MIN_IMAGE_SIZE_IN_BYTE, getBytesFromMB(0f))
            intent.putExtra(MAX_IMAGE_SIZE_IN_BYTE, getBytesFromMB(5f))
            resultLauncher.launch(intent)
        }

        binding.selectMultipleImage.setOnClickListener {
            val intent = Intent(this, ElectronImagePickerActivity::class.java)
            intent.putExtra(SELECTION_TYPE, SelectionType.MULTIPLE)
            intent.putExtra(MAX_MULTI_SELECTION_SIZE, MaxMultiSelectionSize.FIVE)
            intent.putExtra(MIN_IMAGE_SIZE_IN_BYTE, getBytesFromMB(0f))
            intent.putExtra(MAX_IMAGE_SIZE_IN_BYTE, getBytesFromMB(5f))
            resultLauncher.launch(intent)
        }

    }

    private fun showResultData(electronImageList: List<ElectronImage>) {
        imgAdapter.submitList(electronImageList)
    }
}