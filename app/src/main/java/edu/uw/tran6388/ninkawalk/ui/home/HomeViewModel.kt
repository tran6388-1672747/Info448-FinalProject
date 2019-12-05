// Joon Chang, William Fu, Jimmy Tran
// 12/4/2019

package edu.uw.tran6388.ninkawalk.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

// A viewmodel for the main home screen w/ introductory text
class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Walk to buy cool stuff from the shop!"
    }
    val text: LiveData<String> = _text

}