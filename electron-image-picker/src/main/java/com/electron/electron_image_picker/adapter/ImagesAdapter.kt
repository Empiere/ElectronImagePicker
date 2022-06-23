package com.electron.electron_image_picker.adapter

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.electron.electron_image_picker.databinding.ItemElectronImageBinding
import com.electron.electron_image_picker.model.Image

/**
 * CREATED BY Aagam Koradiya on 22-06-2022
 */

class ImagesAdapter : ListAdapter<Image, ImagesAdapter.ImagesViewHolder>(DiffCallback()) {

    var tracker: SelectionTracker<Long>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding =
            ItemElectronImageBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val currentItem = getItem(position)
        tracker?.let {
            holder.bind(currentItem, it.isSelected(currentItem.id))
        }
    }

    inner class ImagesViewHolder(private val binding: ItemElectronImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(currentItem: Image, selected: Boolean) {
            binding.imageIv.setImageBitmap(currentItem.thumbnail)
            binding.selectedView.isVisible = selected
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> =
            object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition

                override fun getSelectionKey(): Long = getItem(adapterPosition).id

                override fun inSelectionHotspot(e: MotionEvent): Boolean {
                    return true
                }
            }
    }

    class DiffCallback : DiffUtil.ItemCallback<Image>() {
        override fun areItemsTheSame(oldItem: Image, newItem: Image) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Image, newItem: Image) =
            oldItem == newItem
    }
}