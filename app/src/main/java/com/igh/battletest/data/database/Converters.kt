package com.igh.battletest.data.database

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromStringList(value: String?): List<String> {
        if (value.isNullOrEmpty()) return emptyList()
        return value.split("|||").filter { it.isNotEmpty() }
    }

    @TypeConverter
    fun stringListToString(list: List<String>?): String {
        return list?.joinToString("|||") ?: ""
    }

    @TypeConverter
    fun fromStringSet(value: String?): Set<String> {
        if (value.isNullOrEmpty()) return emptySet()
        return value.split("|||").filter { it.isNotEmpty() }.toSet()
    }

    @TypeConverter
    fun stringSetToString(set: Set<String>?): String {
        return set?.joinToString("|||") ?: ""
    }
}