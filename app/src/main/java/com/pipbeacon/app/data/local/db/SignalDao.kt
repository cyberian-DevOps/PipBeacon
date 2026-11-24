package com.pipbeacon.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SignalDao {

    @Query("SELECT * FROM signals ORDER BY creationTimestamp DESC")
    fun getAllSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE isCompleted = 0 ORDER BY creationTimestamp DESC")
    fun getActiveSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE isCompleted = 1 ORDER BY creationTimestamp DESC")
    fun getCompletedSignals(): Flow<List<SignalEntity>>

    @Query("SELECT * FROM signals WHERE signalId = :id LIMIT 1")
    suspend fun getSignalById(id: String): SignalEntity?

    @Query("SELECT * FROM signals WHERE signalId = :id LIMIT 1")
    fun observeSignalById(id: String): Flow<SignalEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignals(signals: List<SignalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSignal(signal: SignalEntity)

    @Update
    suspend fun updateSignal(signal: SignalEntity)

    @Query("UPDATE signals SET outcomeStatus = :outcome, resolvedTimestamp = :timestamp, resolvedPrice = :price, isCompleted = :completed WHERE signalId = :id")
    suspend fun updateOutcome(id: String, outcome: String, timestamp: Long, price: Double?, completed: Boolean)

    @Query("DELETE FROM signals")
    suspend fun clearAll()
}
