package com.riviem.sunalarm.core

import android.content.Context
import com.riviem.sunalarm.core.data.database.getAlarmDatabase
import com.riviem.sunalarm.core.data.local.DefaultDataStore
import com.riviem.sunalarm.core.data.local.LocalStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Singleton
    @Provides
    fun provideAlarmDatabase(
        @ApplicationContext context: Context
    ) = getAlarmDatabase(context)

    @Singleton
    @Provides
    fun provideLocalStorage(
        @ApplicationContext context: Context
    ): LocalStorage = DefaultDataStore(
        context = context
    )

}