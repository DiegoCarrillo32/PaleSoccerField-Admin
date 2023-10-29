package com.kosti.palesoccerfieldadmin.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UserViewModel(application: Application) : AndroidViewModel(application) {
    var userId: String = ""
}