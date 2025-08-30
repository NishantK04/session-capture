package com.nishant.oralvisapp.viewmodel

import androidx.lifecycle.*
import com.nishant.oralvisapp.data.ImageWithSession
import com.nishant.oralvisapp.data.OralvisRepository
import com.nishant.oralvisapp.data.SessionEntity
import kotlinx.coroutines.launch

class SessionViewModel(private val repository: OralvisRepository) : ViewModel() {

    val allSessions: LiveData<List<SessionEntity>> = repository.getAllSessions()

    // Now accepts a callback with inserted rowId
    fun insertSession(session: SessionEntity, onInserted: (Long) -> Unit) {
        viewModelScope.launch {
            val rowId = repository.insertSession(session)
            onInserted(rowId)   // return generated auto id
        }
    }

    fun getSessionById(sessionId: String, onResult: (SessionEntity?) -> Unit) {
        viewModelScope.launch {
            val session = repository.getSessionById(sessionId)
            onResult(session)
        }
    }
    fun getAllImagesWithSession(): LiveData<List<ImageWithSession>> {
        return repository.getAllImagesWithSession()
    }

    fun getImagesWithSessionId(sid: String): LiveData<List<ImageWithSession>> {
        return repository.getImagesWithSessionId(sid)
    }

}



class SessionViewModelFactory(private val repository: OralvisRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SessionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SessionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
