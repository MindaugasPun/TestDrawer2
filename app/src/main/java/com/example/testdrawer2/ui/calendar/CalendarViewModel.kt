package com.example.testdrawer2.ui.calendar

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.testdrawer2.reminder_data.Reminder
import com.example.testdrawer2.reminder_data.ReminderDatabase
import kotlinx.coroutines.launch

class CalendarViewModel(context: Context) : ViewModel() {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>>
        get() = _reminders

    val database = Room.databaseBuilder(context, ReminderDatabase::class.java, "reminders").build()

    init {
        filter(null, null, null, null, null, null)
    }

    fun filter(Year1: Int?, Month1: Int?, Day1: Int?, Year2: Int?, Month2: Int?, Day2: Int?) {
        val y1: Int = Year1 ?: 0
        val m1: Int = Month1 ?: 0
        val d1: Int = Day1 ?: 0
        val y2: Int = Year2 ?: 9999
        val m2: Int = Month2 ?: 12
        val d2: Int = Day2 ?: 32
        viewModelScope.launch {
            _reminders.postValue(database.reminderDAO().getByDateInterval(y1, m1, d1, y2, m2, d2))
        }
    }
}