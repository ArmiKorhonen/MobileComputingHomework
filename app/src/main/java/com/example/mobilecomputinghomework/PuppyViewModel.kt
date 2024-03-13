package com.example.mobilecomputinghomework

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PuppyViewModel(private val dao: PuppyProfileDao) : ViewModel() {

    // LiveData to observe puppy profiles
    val puppies = MutableLiveData<List<PuppyProfile>>()

    fun fetchPuppies() {
        viewModelScope.launch(Dispatchers.IO) {
            puppies.postValue(dao.getAll())
        }
    }

    // Add more functions here for inserting or deleting puppies
}
