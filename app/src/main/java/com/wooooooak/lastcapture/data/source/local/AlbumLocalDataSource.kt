package com.wooooooak.lastcapture.data.source.local

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.wooooooak.lastcapture.data.model.AlbumLocal
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

//private val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
private const val INDEX_MEDIA_ID = MediaStore.Images.Media._ID
private const val INDEX_ALBUM_NAME = MediaStore.Images.Media.BUCKET_DISPLAY_NAME
private const val INDEX_IMAGE_NAME = MediaStore.Images.Media.DISPLAY_NAME
private const val INDEX_DATE_TAKEN = MediaStore.Images.Media.DATE_TAKEN
private const val orderOption = "$INDEX_DATE_TAKEN DESC"

class AlbumLocalDataSource(private val context: Context) {
    suspend fun getAllAlbum(): List<AlbumLocal> = suspendCoroutine { continuation ->
        var albumList: List<AlbumLocal> = listOf()
        val projection = arrayOf(
            INDEX_MEDIA_ID,
            INDEX_ALBUM_NAME,
            INDEX_IMAGE_NAME,
            INDEX_DATE_TAKEN
        )
        val qurey = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            orderOption
        )
        qurey?.use {
            val idColumn = it.getColumnIndexOrThrow(INDEX_MEDIA_ID)
            val albumNameColumn = it.getColumnIndexOrThrow(INDEX_ALBUM_NAME)

            albumList = generateSequence { if (it.moveToNext()) it else null }
                .map { cursor ->
                    val id = cursor.getLong(idColumn)
                    val albumName = cursor.getString(albumNameColumn)
                    val uri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id.toString()
                    )
                    AlbumLocal(albumName, uri)
                }
                .groupBy { it.name }
                .map { it.value[0] }
        }

        continuation.resume(albumList)
    }
}
