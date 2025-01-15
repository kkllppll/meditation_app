package com.example.meditation_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.meditation_app.model.Meditation
import com.example.meditation_app.repository.MeditationRepository

class MeditationsViewModel : ViewModel() {

    private val repository = MeditationRepository()

    private val _meditations = MutableLiveData<List<Meditation>>()
    val meditations: LiveData<List<Meditation>> get() = _meditations

    private val _favorites = MutableLiveData<Set<String>>()
    val favorites: LiveData<Set<String>> get() = _favorites

    fun loadMeditations() {
        repository.getMeditations { loadedMeditations ->
            _meditations.value = loadedMeditations
        }
    }

    fun toggleFavorite(userId: String, meditationId: String) {
        val currentFavorites = _favorites.value ?: emptySet()
        val isFavorite = currentFavorites.contains(meditationId)

        repository.toggleFavorite(userId, meditationId, isFavorite) { success ->
            if (success) {
                val updatedFavorites = if (isFavorite) {
                    currentFavorites - meditationId
                } else {
                    currentFavorites + meditationId
                }
                _favorites.value = updatedFavorites
            }
        }
    }

    fun loadFavorites(userId: String) {
        repository.getFavorites(userId) { loadedFavorites ->
            _favorites.value = loadedFavorites.toSet()
        }
    }

    fun loadMeditationsByIds(ids: List<String>, onComplete: (List<Meditation>) -> Unit) {
        repository.getMeditationsByIds(ids) { meditations ->
            onComplete(meditations)
        }
    }
}
