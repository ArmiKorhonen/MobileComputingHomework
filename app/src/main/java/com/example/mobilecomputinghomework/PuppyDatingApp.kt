package com.example.mobilecomputinghomework

import android.app.Application

class PuppyDatingApp : Application(){
    val database by lazy { AppDatabase.getDatabase(this) }
}


