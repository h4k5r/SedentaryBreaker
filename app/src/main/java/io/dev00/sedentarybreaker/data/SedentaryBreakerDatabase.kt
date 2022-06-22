package io.dev00.sedentarybreaker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import io.dev00.sedentarybreaker.models.AlarmIsSet
import io.dev00.sedentarybreaker.models.UserActivity

@Database(entities = [UserActivity::class,AlarmIsSet::class], version = 1, exportSchema = false)
abstract class SedentaryBreakerDatabase: RoomDatabase() {
    abstract fun sedentaryBreakerDAO():SedentaryBreakerDAO
}