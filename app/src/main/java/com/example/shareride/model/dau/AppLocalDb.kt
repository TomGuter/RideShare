package com.example.shareride.model.dau

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.shareride.base.MyApplication
import com.example.shareride.model.Ride


@Database(entities = [Ride::class], version = 1) // Update version if schema changes
abstract class AppLocalDbRepository : RoomDatabase() {
    abstract fun rideDao(): RideDao
}

object AppLocalDb {

    val database: AppLocalDbRepository by lazy {

        val context = MyApplication.Globals.context ?: throw IllegalStateException("Application context is missing")

        Room.databaseBuilder(
            context = context,
            klass = AppLocalDbRepository::class.java,
            name = "rides.db" // Replace with your database name
        )
            .fallbackToDestructiveMigration()
            .build()
    }
}
