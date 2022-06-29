package io.dev00.sedentarybreaker.data

import androidx.room.*
import io.dev00.sedentarybreaker.models.AlarmIsSet
import io.dev00.sedentarybreaker.models.HomeLocation
import io.dev00.sedentarybreaker.models.UserActivity

@Dao
interface SedentaryBreakerDAO {
    @Query("SELECT * FROM userActivity LIMIT 1")
    fun getUserActivity():UserActivity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserActivity(userActivity: UserActivity)

    @Delete
    suspend fun deleteUserActivity(userActivity: UserActivity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserActivity(userActivity: UserActivity)

    @Query("SELECT * FROM alarmIsSet LIMIT 1")
    fun getAlarmIsSet(): AlarmIsSet?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarmIsSet(alarmIsSet: AlarmIsSet)

    @Delete
    suspend fun deleteAlarmIsSet(alarmIsSet: AlarmIsSet)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateAlarmIsSet(alarmIsSet: AlarmIsSet)

    @Query("SELECT * FROM home_location LIMIT 1")
    fun getHomeLocation():HomeLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomeLocation(homeLocation: HomeLocation)

    @Delete
    suspend fun deleteHomeLocation(homeLocation: HomeLocation)

    @Update
    suspend fun  updateHomeLocation(homeLocation: HomeLocation)
}