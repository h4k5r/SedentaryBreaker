package io.dev00.sedentarybreaker.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.dev00.sedentarybreaker.data.SedentaryBreakerDAO
import io.dev00.sedentarybreaker.data.SedentaryBreakerDatabase
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideSedentaryBreakerDao(sedentaryBreakerDatabase: SedentaryBreakerDatabase): SedentaryBreakerDAO {
        return sedentaryBreakerDatabase.sedentaryBreakerDAO()
    }

    @Singleton
    @Provides
    fun provideSedentaryBreakerDatabase(@ApplicationContext context: Context): SedentaryBreakerDatabase {
        return Room.databaseBuilder(
            context,
            SedentaryBreakerDatabase::class.java,
            "sedentary_breaker_db"
        )
            .fallbackToDestructiveMigration().build()
    }
}