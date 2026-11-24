package com.pipbeacon.app.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketQuoteDao {

    @Query("SELECT * FROM market_quotes ORDER BY symbol ASC")
    fun getAllQuotes(): Flow<List<MarketQuoteEntity>>

    @Query("SELECT * FROM market_quotes WHERE marketType = :type ORDER BY symbol ASC")
    fun getQuotesByType(type: String): Flow<List<MarketQuoteEntity>>

    @Query("SELECT * FROM market_quotes WHERE symbol = :symbol LIMIT 1")
    suspend fun getQuoteBySymbol(symbol: String): MarketQuoteEntity?

    @Query("SELECT * FROM market_quotes WHERE symbol = :symbol LIMIT 1")
    fun observeQuoteBySymbol(symbol: String): Flow<MarketQuoteEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotes(quotes: List<MarketQuoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuote(quote: MarketQuoteEntity)

    @Query("DELETE FROM market_quotes")
    suspend fun clearAll()
}
