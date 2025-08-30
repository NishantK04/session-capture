package com.nishant.oralvisapp.viewmodel

import androidx.lifecycle.*
import com.nishant.oralvisapp.data.ImageEntity
import com.nishant.oralvisapp.data.ImageWithSession
import com.nishant.oralvisapp.data.OralvisRepository
import kotlinx.coroutines.launch

class ImageViewModel(private val repository: OralvisRepository) : ViewModel() {

    // 🔹 Insert single image
    fun insertImage(image: ImageEntity) {
        viewModelScope.launch {
            repository.insertImage(image)
        }
    }

    // 🔹 Delete single image
    fun deleteImage(image: ImageEntity) {
        viewModelScope.launch {
            repository.deleteImage(image)
        }
    }

    // -------------------------------
    //  Basic ImageEntity queries
    // -------------------------------

    fun getAllImages(): LiveData<List<ImageEntity>> =
        repository.getAllImages()

    fun getImagesForSessionOwnerId(ownerId: Int): LiveData<List<ImageEntity>> =
        repository.getImagesForSession(ownerId)

    fun getImagesForSessionId(sessionId: String): LiveData<List<ImageEntity>> =
        repository.getImagesForSessionId(sessionId)

    // -------------------------------
    //  Joined queries (Image + Session)
    // -------------------------------

    fun getAllImagesWithSession(): LiveData<List<ImageWithSession>> =
        repository.getAllImagesWithSession()

    fun getImagesWithSessionId(sid: String): LiveData<List<ImageWithSession>> =
        repository.getImagesWithSessionId(sid)

    fun getImagesWithSessionPrefix(query: String): LiveData<List<ImageWithSession>> {
        return repository.searchImagesBySessionPrefix(query)
    }


}

class ImageViewModelFactory(private val repository: OralvisRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ImageViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
