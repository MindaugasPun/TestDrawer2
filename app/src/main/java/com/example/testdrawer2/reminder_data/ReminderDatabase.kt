package com.example.testdrawer2.reminder_data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Reminder::class), version = 1)
abstract class ReminderDatabase : RoomDatabase(){

    abstract fun reminderDAO(): ReminderDAO
}