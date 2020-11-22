package com.wooooooak.lastcapture.data.source.local

import com.wooooooak.lastcapture.data.model.AlbumLocal
import com.wooooooak.lastcapture.data.model.ImageLocal

interface AlbumDataSource {
    suspend fun getSelectedImageList(
        count: Int,
        albumNameList: List<String>,
    ): List<ImageLocal>

    suspend fun getAllAlbum(): List<AlbumLocal>

    suspend fun addSelectedAlbum(album: AlbumLocal): Unit?

    suspend fun removeSelectedAlbum(album: AlbumLocal): Unit?

    suspend fun getSelectedAlbumList(): List<AlbumLocal>
}