package com.selcukileri.wakeup.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.selcukileri.wakeup.model.SearchHistory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
@Dao
interface SearchHistoryDao {
    @Query("SELECT * FROM SearchHistory ORDER BY timestamp DESC")
    fun getAllSearchHistory(): Flowable<List<SearchHistory>>

    @Insert
    fun insertSearchHistory(searchHistory: SearchHistory): Completable

    @Delete
    fun deleteSearchHistory(searchHistory: SearchHistory): Completable
}