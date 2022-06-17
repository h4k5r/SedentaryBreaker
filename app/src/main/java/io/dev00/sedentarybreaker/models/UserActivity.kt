package io.dev00.sedentarybreaker.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "userActivity")
class UserActivity(
    @PrimaryKey(autoGenerate = true)
    var id: Int?,
    @ColumnInfo(name = "detected_activity")
    var detectedActivity: String,
    @ColumnInfo(name = "activity_state")
    var activityState: String,
    @ColumnInfo(name = "time")
    var time: String,
) {
    constructor() : this(id = null, detectedActivity = "", activityState = "", time = "")
    constructor(activityState: String, detectedActivity: String, time: String) : this(
        id = null,
        detectedActivity = detectedActivity,
        activityState = activityState,
        time = time
    )

    override fun toString(): String {
        return "${this.id} ${this.detectedActivity} ${this.activityState} ${this.time}"
    }
}