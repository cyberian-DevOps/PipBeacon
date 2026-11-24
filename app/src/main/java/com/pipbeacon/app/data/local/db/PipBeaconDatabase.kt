package com.pipbeacon.app.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        MarketQuoteEntity::class,
        SignalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class PipBeaconDatabase : RoomDatabase() {

    abstract fun marketQuoteDao(): MarketQuoteDao
    abstract fun signalDao(): SignalDao

    companion object {
        @Volatile
        private var INSTANCE: PipBeaconDatabase? = null

        fun getInstance(context: Context): PipBeaconDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PipBeaconDatabase::class.java,
                    "pipbeacon_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
