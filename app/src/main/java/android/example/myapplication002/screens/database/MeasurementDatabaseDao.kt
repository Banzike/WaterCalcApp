package android.example.myapplication002.screens.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface MeasurementDatabaseDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(measurement: Measurement)

    @Update
    suspend fun update(measurement: Measurement)

    @Query("SELECT * FROM measurement_table WHERE reserve_Id=:key")
    suspend fun getMeasurementsOfReserve(key:Long):List<Measurement>

    @Query("DELETE FROM measurement_table WHERE reserve_Id=:key")
    suspend fun deleteMeasurementsOfReserve(key: Long)

    @Query("DELETE FROM measurement_table WHERE owner=:key")
    suspend fun deleteMeasurementsOfUser(key:String)

    @Query("DELETE FROM measurement_table")
    suspend fun clear()

    @Query("SELECT * from measurement_table")
    fun getAllMeasurements():List<Measurement>

    @Query("SELECT * from measurement_table WHERE owner=:key")
    fun getMeasurementsOfUser(key:String):List<Measurement>

}