package com.example.myapplicationyyy.ui.gallery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Random.Range()"
    }
    val text: LiveData<String> = _text


     public val _text2 = MutableLiveData<String>().apply {
        value = "This is a prototype function page for Random.Range(). Here we will describe the function and have documentation."
    }
    val text2: LiveData<String> = _text2
}