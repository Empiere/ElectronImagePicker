package com.electron.electronimagepicker

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.electron.electron_image_picker.model.ElectronImage
import com.electron.electronimagepicker.databinding.ItemImageBinding
import java.io.File

/**
 * CREATED BY Aagam Koradiya on 23-06-2022
 */

class ImgAdapter : ListAdapter<ElectronImage, ImgAdapter.ImgViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgViewHolder {
        val binding =
            ItemImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ImgViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImgViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ImgViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(image: ElectronImage) {
            binding.apply {
//                Glide.with(binding.root.context).load(File(image.filePath)).into(imageView) // Working
                imageView.setImageURI(Uri.parse(image.contentUri))
                idTxt.text = image.id.toString()
                nameTxt.text = image.name
                sizeInBytesTxt.text = image.sizeInBytes.toString()
                formattedSizeTxt.text = image.formattedSize
                contentUriTxt.text = image.contentUri
                validPathTxt.text = image.filePath
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ElectronImage>() {
        override fun areItemsTheSame(oldItem: ElectronImage, newItem: ElectronImage) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: ElectronImage, newItem: ElectronImage) =
            oldItem == newItem
    }
}