package com.musalatask.weatherapp.framework.room

import androidx.room.TypeConverter

class TypeConverters {

    @TypeConverter
    fun toString(list: List<String>): String =
        list.joinToString { it }

    @TypeConverter
    fun toStringList(str: String): List<String> =
        ArrayList(str.split(", ").map { it })
}