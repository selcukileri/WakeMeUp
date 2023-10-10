package com.selcukileri.wakeup.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.selcukileri.wakeup.model.Place
import com.selcukileri.wakeup.model.SearchHistory

@Database(entities = [SearchHistory::class], version = 1)
abstract class HistoryDatabase : RoomDatabase() {
    abstract fun searchHistoryDao(): SearchHistoryDao
}