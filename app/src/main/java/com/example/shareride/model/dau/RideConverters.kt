package com.example.shareride.model.dau

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RideConverters {

    @TypeConverter
    fun fromListToString(list: List<String>?): String {
        return Gson().toJson(list ?: emptyList<String>())
    }
    @TypeConverter
    fun fromStringToList(value: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, type) ?: emptyList()
    }
}
