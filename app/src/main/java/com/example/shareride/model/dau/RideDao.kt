package com.example.shareride.model.dau


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.shareride.model.Ride

@Dao
interface RideDao {

    @Query("SELECT * FROM Ride")
    fun getAllRides(): List<Ride>

    @Query("SELECT * FROM Ride WHERE id = :id")
    fun getRideById(id: String): Ride

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRides(vararg rides: Ride)

    @Delete
    fun delete(ride: Ride)
}
