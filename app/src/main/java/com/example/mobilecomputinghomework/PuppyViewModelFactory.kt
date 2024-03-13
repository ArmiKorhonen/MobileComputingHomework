package com.example.mobilecomputinghomework

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class PuppyViewModelFactory(private val dao: PuppyProfileDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PuppyViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PuppyViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}