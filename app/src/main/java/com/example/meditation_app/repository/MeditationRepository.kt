package com.example.meditation_app.repository

import android.util.Log
import com.example.meditation_app.model.Meditation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MeditationRepository {
    //"посередник" між ViewModel і Firebase Firestore

    private val db = FirebaseFirestore.getInstance()

    fun getMeditations(onResult: (List<Meditation>) -> Unit) {
        db.collection("Meditations")
            .get()
            .addOnSuccessListener { result ->
                val meditations = result.map { document ->
                    document.toObject(Meditation::class.java)
                }
                onResult(meditations)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error loading meditations", e)
                onResult(emptyList())
            }
    }

    fun toggleFavorite(userId: String, meditationId: String, isFavorite: Boolean, onComplete: (Boolean) -> Unit) {
        val favoritesRef = db.collection("favorites")

        if (isFavorite) {
            favoritesRef
                .whereEqualTo("user_id", userId)
                .whereEqualTo("id", meditationId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        onComplete(false)
                        return@addOnSuccessListener
                    }
                    querySnapshot.documents.forEach { document ->
                        document.reference.delete()
                    }
                    onComplete(true)
                }
                .addOnFailureListener {
                    onComplete(false)
                }
        } else {
            val favorite = hashMapOf(
                "user_id" to userId,
                "id" to meditationId
            )
            favoritesRef.add(favorite)
                .addOnSuccessListener { onComplete(true) }
                .addOnFailureListener { onComplete(false) }
        }
    }

    fun getFavorites(userId: String, onComplete: (List<String>) -> Unit) {
        db.collection("favorites")
            .whereEqualTo("user_id", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meditationIds = querySnapshot.documents.map { it.getString("id") ?: "" }
                onComplete(meditationIds)
            }
            .addOnFailureListener { e ->
                onComplete(emptyList())
            }
    }

    fun getMeditationsByIds(ids: List<String>, onComplete: (List<Meditation>) -> Unit) {
        if (ids.isEmpty()) {
            onComplete(emptyList())
            return
        }

        db.collection("Meditations")
            .whereIn("id", ids)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val meditations = querySnapshot.toObjects(Meditation::class.java)
                onComplete(meditations)
            }
            .addOnFailureListener {
                onComplete(emptyList())
            }
    }
}
