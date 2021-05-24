package com.example.testdrawer2.ui.new_reminder

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import androidx.room.Room
import com.example.testdrawer2.reminder_data.Reminder
import com.example.testdrawer2.reminder_data.ReminderDatabase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewReminderViewModel(context: Context) : ViewModel() {
    private val _reminders = MutableLiveData<List<Reminder>>()
    val reminders: LiveData<List<Reminder>>
        get() = _reminders
    private val _reminder = MutableLiveData<Reminder>()
    val reminder: LiveData<Reminder>
        get() = _reminder
    private val _insert = MutableLiveData<Long>()
    val insert : LiveData<Long>
        get() = _insert

    val database = Room.databaseBuilder(context, ReminderDatabase::class.java, "reminders").build()

    init {
        getAllReminders()
    }


    fun getAllReminders() {
        viewModelScope.launch {
            _reminders.postValue(database.reminderDAO().getAll())
        }
    }

    fun getReminder(id: Int) {
        viewModelScope.launch {
            Log.d("ID", id.toString())
            _reminder.postValue(database.reminderDAO().getById(id))
//            val reminderLiveData: LiveData<Reminder>
//            Log.d("reminder", reminder.value?.title.toString())
//            Log.d("reminder", _reminder.value?.title.toString())
//            Log.d("reminder", "reminder")
        }
    }

    fun addReminder(
        Id: Int, Title: String?, YYYY: String?, MM: String?, DD: String?, Description: String
    ): Long? {
        val Year = YYYY?.toIntOrNull()
        val Month = MM?.toIntOrNull()
        val Day = DD?.toIntOrNull()
        if (Title != null && Year != null && Month != null && Day != null) {
            val reminder = Reminder(Id, Title, Year, Month, Day, Description)
            viewModelScope.launch {
//                _insert.postValue(database.reminderDAO().insertReminder(reminder))
                _insert.value = database.reminderDAO().insertReminder(reminder)
//                Log.d("SAVE:", database.reminderDAO().insertReminder(reminder).toString())
                getAllReminders()
            }
        }
        return insert.value
    }

    fun updateReminder(
        Id: Int, Title: String, YYYY: String, MM: String, DD: String, Description: String
    ) {
        val Year = YYYY?.toIntOrNull()
        val Month = MM?.toIntOrNull()
        val Day = DD?.toIntOrNull()
        if (Title != null && Year != null && Month != null && Day != null) {
            val reminder = Reminder(Id, Title, Year, Month, Day, Description)
            viewModelScope.launch {
                database.reminderDAO().updateReminder(reminder)
                getAllReminders()
            }
        }
    }

    fun deleteReminder() {
        reminder.observeForever { reminder ->
            viewModelScope.launch {
                database.reminderDAO().deleteReminder(reminder)
                getAllReminders()
            }
        }

    }
}