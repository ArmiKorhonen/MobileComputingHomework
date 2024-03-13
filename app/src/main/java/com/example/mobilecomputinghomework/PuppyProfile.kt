package com.example.mobilecomputinghomework

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PuppyProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val breed: String,
    val imageUri: String,
    val bio: String
)
