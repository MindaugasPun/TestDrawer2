package com.example.testdrawer2.ui.calendar

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.testdrawer2.ui.new_reminder.NewReminderViewModel
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class CalendarViewModelFactory(val context: Context): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CalendarViewModel::class.java)){
            return CalendarViewModel(context) as T
        }
        throw IllegalArgumentException()
    }
}