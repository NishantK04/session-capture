package com.nishant.oralvisapp.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// ImageDao.kt
@Dao
interface ImageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageEntity): Long

    @Query("SELECT * FROM images WHERE sessionOwnerId = :sessionId")
    fun getImagesForSession(sessionId: Int): LiveData<List<ImageEntity>>

    @androidx.room.Delete
    suspend fun deleteImage(image: ImageEntity)

    @Query("SELECT * FROM images")
    fun getAllImages(): LiveData<List<ImageEntity>>

    @Query("SELECT images.* FROM images INNER JOIN sessions ON images.sessionOwnerId = sessions.id WHERE sessions.sessionId = :sid")
    fun getImagesForSessionId(sid: String): LiveData<List<ImageEntity>>

    @Query("""
    SELECT images.imagePath, sessions.sessionId, sessions.name, sessions.age
    FROM images 
    INNER JOIN sessions ON images.sessionOwnerId = sessions.id
""")
    fun getAllImagesWithSession(): LiveData<List<ImageWithSession>>

    @Query("""
    SELECT images.imagePath, sessions.sessionId, sessions.name, sessions.age
    FROM images 
    INNER JOIN sessions ON images.sessionOwnerId = sessions.id
    WHERE sessions.sessionId = :sid
""")
    fun getImagesWithSessionId(sid: String): LiveData<List<ImageWithSession>>

    @Query("""
    SELECT images.imagePath, sessions.sessionId, sessions.name, sessions.age
    FROM images
    INNER JOIN sessions ON images.sessionOwnerId = sessions.id
    WHERE sessions.sessionId LIKE :query || '%' OR sessions.name LIKE :query || '%'
""")
    fun getImagesWithSessionPrefix(query: String): LiveData<List<ImageWithSession>>



}
