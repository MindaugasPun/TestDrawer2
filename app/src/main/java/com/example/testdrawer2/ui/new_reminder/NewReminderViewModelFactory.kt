package com.example.testdrawer2.ui.new_reminder

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class NewReminderViewModelFactory(val context: Context):ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewReminderViewModel::class.java)){
            return NewReminderViewModel(context) as T
        }
        throw IllegalArgumentException()
    }
}