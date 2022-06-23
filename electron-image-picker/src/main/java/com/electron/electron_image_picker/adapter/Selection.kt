package com.electron.electron_image_picker.adapter

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

/**
 * CREATED BY Aagam Koradiya on 21-06-2022
 */


class MyItemDetailsLookup(private val recyclerView: RecyclerView) : ItemDetailsLookup<Long>() {
    override fun getItemDetails(event: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(event.x, event.y)
        if (view != null) {
            return (recyclerView.getChildViewHolder(view) as ImagesAdapter.ImagesViewHolder).getItemDetails()
        }
        return null
    }
}

class MyItemKeyProvider(private val adapter: ImagesAdapter) : ItemKeyProvider<Long>(SCOPE_CACHED) {

    override fun getKey(position: Int): Long = adapter.currentList[position].id

    override fun getPosition(key: Long): Int = adapter.currentList.indexOfFirst { it.id == key }
}
