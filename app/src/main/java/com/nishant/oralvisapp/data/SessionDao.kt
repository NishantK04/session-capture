package com.nishant.oralvisapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// SessionDao.kt
@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertSession(session: SessionEntity): Long

    @Query("SELECT * FROM sessions WHERE sessionId = :sid LIMIT 1")
    suspend fun getSessionBySessionId(sid: String): SessionEntity?

    @Query("SELECT * FROM sessions")
    fun getAllSessions(): LiveData<List<SessionEntity>>
}