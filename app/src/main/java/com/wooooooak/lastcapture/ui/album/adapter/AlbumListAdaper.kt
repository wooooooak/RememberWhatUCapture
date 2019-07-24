package com.wooooooak.lastcapture.ui.album.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.wooooooak.lastcapture.R
import com.wooooooak.lastcapture.data.Album
import com.wooooooak.lastcapture.databinding.ItemAlbumBinding

class AlbumListAdapter : ListAdapter<Album, AlbumListAdapter.ViewHolder>(AlbumDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAlbumBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val album = getItem(position)
        holder.bind(album, createOnClickListener(album))
    }

    private fun createOnClickListener(album: Album): View.OnClickListener {
        return View.OnClickListener {
            if (album.isSelected) {
                it.setBackgroundResource(0)
            } else {
                it.setBackgroundResource(R.drawable.border_red)
            }
            album.isSelected = !album.isSelected
        }
    }

    inner class ViewHolder(val binding: ItemAlbumBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(album: Album, listener: View.OnClickListener) {
            binding.apply {
                this.album = album
                onClickListener = listener
            }
        }
    }
}

private class AlbumDiffCallback : DiffUtil.ItemCallback<Album>() {
    override fun areItemsTheSame(oldItem: Album, newItem: Album) = oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: Album, newItem: Album) = oldItem == newItem
}