package com.example.testdrawer2.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.testdrawer2.reminder_data.Reminder
import com.example.testdrawer2.reminder_data.ReminderDatabase
import kotlinx.coroutines.launch

class HomeViewModel(context: Context) : ViewModel() {

    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>>
        get() = _reminders

    val database = Room.databaseBuilder(context, ReminderDatabase::class.java, "reminders").build()
    init {
        getAllReminders()
    }

    fun getAllReminders(){
        viewModelScope.launch {
            _reminders.postValue(database.reminderDAO().getAll())
        }
    }

}