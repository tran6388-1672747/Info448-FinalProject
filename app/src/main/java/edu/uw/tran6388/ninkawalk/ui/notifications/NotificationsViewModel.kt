package edu.uw.tran6388.ninkawalk.ui.notifications

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NotificationsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "My Private Collection"
    }
    val text: LiveData<String> = _text
}