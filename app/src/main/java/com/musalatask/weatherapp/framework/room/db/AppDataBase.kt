package com.musalatask.weatherapp.framework.room.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.musalatask.weatherapp.framework.room.dao.CityWeatherDao
import com.musalatask.weatherapp.framework.room.entity.CityWeatherEntity

const val DB_NAME = "WeatherAppDataBase.db"

@Database(entities = [CityWeatherEntity::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun cityWeatherDao(): CityWeatherDao

    companion object {

        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getInstance(context: Context): AppDataBase =
            INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDataBase::class.java, DB_NAME
            )
                .build()
    }

    suspend fun clear() {
        cityWeatherDao().clearCityWeatherEntities()
    }
}