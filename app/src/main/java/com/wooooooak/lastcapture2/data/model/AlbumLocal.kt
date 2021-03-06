package com.wooooooak.lastcapture2.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumLocal(
    @PrimaryKey val name: String,
    val imageResource: String,
    val selected: Boolean = false
)