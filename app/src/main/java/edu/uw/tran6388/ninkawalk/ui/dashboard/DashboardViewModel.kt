package edu.uw.tran6388.ninkawalk.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Welcome!!!"
        //value = "This is test"
    }
    val text: LiveData<String> = _text
}