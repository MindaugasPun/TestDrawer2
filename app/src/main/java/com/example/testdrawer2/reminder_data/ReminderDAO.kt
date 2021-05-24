package com.example.testdrawer2.reminder_data

import androidx.room.*

@Dao
interface ReminderDAO {
    @Insert
    suspend fun insertReminder(reminder: Reminder): Long

    @Delete
    suspend fun deleteReminder(reminder: Reminder)

    @Update
    suspend fun updateReminder(reminder: Reminder)

    @Query("SELECT * FROM Reminder")
    suspend fun getAll(): List<Reminder>

    @Query("SELECT * FROM Reminder WHERE reminderId= :id LIMIT 1")
    suspend fun getById(id: Int): Reminder

    @Query("SELECT * FROM Reminder WHERE ((year*12*31 + month*31 + day) >= (:yearMin*12*31 + :monthMin*31 + :dayMin) AND (year*12*31 + month*31 + day) <= (:yearMax*12*31 + :monthMax*31 + :dayMax)) ORDER BY (year*12*31 + month*31 + day) DESC")
//    @Query("SELECT * FROM Reminder WHERE (year BETWEEN :yearMin AND :yearMax) AND ((month BETWEEN :monthMin AND :monthMax) OR (month = :monthMax AND (month BETWEEN :monthMin AND :monthMax)))")
    suspend fun getByDateInterval(
        yearMin: Int, monthMin: Int, dayMin: Int, yearMax: Int, monthMax: Int, dayMax: Int
    ): List<Reminder>
}