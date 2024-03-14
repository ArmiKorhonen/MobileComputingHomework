package com.example.mobilecomputinghomework

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PuppyProfileDao {
    @Query("SELECT * FROM PuppyProfile")
    fun getAll(): LiveData<List<PuppyProfile>>

    @Insert
    fun insertAll(vararg profiles: PuppyProfile)

    @Delete
    fun delete(profile: PuppyProfile)
}
