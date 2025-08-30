package com.nishant.oralvisapp.data

import androidx.lifecycle.LiveData

class OralvisRepository(
    private val sessionDao: SessionDao,
    private val imageDao: ImageDao
) {

    suspend fun insertSession(session: SessionEntity): Long = sessionDao.insertSession(session)
    fun getAllSessions(): LiveData<List<SessionEntity>> = sessionDao.getAllSessions()
    suspend fun getSessionById(sessionId: String): SessionEntity? = sessionDao.getSessionBySessionId(sessionId)


    suspend fun insertImage(image: ImageEntity): Long = imageDao.insertImage(image)
    fun getImagesForSession(sessionId: Int): LiveData<List<ImageEntity>> {
        return imageDao.getImagesForSession(sessionId)
    }

    fun getAllImages(): LiveData<List<ImageEntity>> = imageDao.getAllImages()

    fun getImagesForSessionId(sid: String): LiveData<List<ImageEntity>> {
        return imageDao.getImagesForSessionId(sid)
    }

    fun getAllImagesWithSession(): LiveData<List<ImageWithSession>> =
        imageDao.getAllImagesWithSession()

    fun getImagesWithSessionId(sid: String): LiveData<List<ImageWithSession>> =
        imageDao.getImagesWithSessionId(sid)

    fun searchImagesBySessionPrefix(query: String): LiveData<List<ImageWithSession>> {
        return imageDao.getImagesWithSessionPrefix(query)
    }


    suspend fun deleteImage(image: ImageEntity) = imageDao.deleteImage(image)
}

