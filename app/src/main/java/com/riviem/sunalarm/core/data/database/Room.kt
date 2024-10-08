package com.riviem.sunalarm.core.data.database

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.riviem.sunalarm.core.data.database.typeconverters.DayTypeConverter
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDao {

    @Query("select * from databasealarm order by createdTimestamp desc")
    fun getAlarms(): Flow<List<DatabaseAlarm>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(alarms: List<DatabaseAlarm>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(alarm: DatabaseAlarm)

    @Query("delete from databasealarm")
    fun deleteAllAlarms()

    @Query("delete from databasealarm where createdTimestamp = :alarmId")
    fun deleteAlarm(alarmId: Int)

    @Query("select * from databasealarm where createdTimestamp = :createdTimestampId")
    fun getAlarmById(createdTimestampId: Int): DatabaseAlarm
}

@Database(entities = [DatabaseAlarm::class], version = 6)
@TypeConverters(DayTypeConverter::class)
abstract class AlarmDatabase : RoomDatabase() {
    abstract val alarmDao: AlarmDao
}

private lateinit var INSTANCE: AlarmDatabase

fun getAlarmDatabase(context: Context): AlarmDatabase {
    synchronized(AlarmDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                AlarmDatabase::class.java,
                "alarm"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}