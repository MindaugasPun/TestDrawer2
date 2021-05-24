package com.example.testdrawer2.reminder_data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Reminder(
    @PrimaryKey(autoGenerate = true) var reminderId: Int,
    var title: String,
    var year: Int, //Simplest way would probably be to use [dd mm yyyy] as Int
    var month: Int,
    var day: Int,
    var description: String
)