package com.example.mobilecomputinghomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PuppyViewModel(private val dao: PuppyProfileDao) : ViewModel() {

    // LiveData to observe puppy profiles
    val puppies: LiveData<List<PuppyProfile>> = dao.getAll()


    /*fun fetchPuppies() {
        viewModelScope.launch(Dispatchers.IO) {
            puppies.postValue(dao.getAll())
        }
    }*/

    fun insertPuppyProfile(name: String, breed: String, imageUri: String, bio: String) {
        val newPuppy = PuppyProfile(name = name, breed = breed, imageUri = imageUri, bio = bio)
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertAll(newPuppy)
        }
    }


}
