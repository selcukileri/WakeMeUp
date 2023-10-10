package com.selcukileri.wakeup.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity
class SearchHistory (
    @ColumnInfo(name = "query")
    var query: String,
    @ColumnInfo(name = "timestamp")
    var timestamp: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}