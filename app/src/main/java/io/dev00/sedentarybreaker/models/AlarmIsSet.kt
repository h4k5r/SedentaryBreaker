package io.dev00.sedentarybreaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarmIsSet")
class AlarmIsSet(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "is_set")
    var isSet: Boolean = false
) {
    constructor(): this(id= null,isSet = false)
    constructor(isSet: Boolean): this(id = null,isSet = isSet)
}