package edu.uw.tran6388.ninkawalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Walk to buy cool stuff from the shop!"
    }
    val text: LiveData<String> = _text
}