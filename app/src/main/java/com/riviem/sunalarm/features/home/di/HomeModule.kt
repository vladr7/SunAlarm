package com.riviem.sunalarm.features.home.di

import com.riviem.sunalarm.core.data.database.AlarmDatabase
import com.riviem.sunalarm.core.data.local.LocalStorage
import com.riviem.sunalarm.features.home.data.AlarmRepository
import com.riviem.sunalarm.features.home.data.DefaultAlarmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object HomeModule {


    @Singleton
    @Provides
    fun provideAlarmRepository(
        alarmDatabase: AlarmDatabase,
        localStorage: LocalStorage
    ): AlarmRepository {
        return DefaultAlarmRepository(
            alarmDatabase = alarmDatabase,
            localStorage = localStorage
        )
    }

}