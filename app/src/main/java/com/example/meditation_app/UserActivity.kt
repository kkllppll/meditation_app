package com.example.meditation_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.meditation_app.adapter.FavoritesAdapter
import com.example.meditation_app.repository.MeditationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.text.SimpleDateFormat
import java.util.*

class UserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val repository = MeditationRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_activity)

        // ініціалізація FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // пошук елементів інтерфейсу
        val userNameTextView = findViewById<TextView>(R.id.userNameTextView)
        val registrationDateTextView = findViewById<TextView>(R.id.registrationDateTextView)
        val favoritesListView = findViewById<ListView>(R.id.savedMeditationsListView)
        val logoutButton = findViewById<Button>(R.id.logoutButton)

        // отримуємо поточного користувача
        val user: FirebaseUser? = auth.currentUser

        if (user != null) {
            // відображення email
            userNameTextView.text = "Email: ${user.email ?: "Невідомий"}"

            // відображення дати реєстрації
            val metadata = user.metadata
            val creationDate = metadata?.creationTimestamp?.let {
                val date = Date(it)
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
            } ?: "Невідомий"
            registrationDateTextView.text = "Дата реєстрації: $creationDate"

            // завантаження улюблених медитацій
            loadFavoriteMeditations(user.uid) { favoriteMeditations ->
                if (favoriteMeditations.isEmpty()) {
                    Log.d("UserActivity", "Для користувача не знайдено улюблених медитацій.")
                    favoritesListView.adapter = FavoritesAdapter(this, emptyList()) // Порожній адаптер
                } else {
                    repository.getMeditationsByIds(favoriteMeditations) { meditations ->
                        if (meditations.isNotEmpty()) {
                            val adapter = FavoritesAdapter(this, meditations) // Передаємо List<Meditation>
                            favoritesListView.adapter = adapter
                        } else {
                            Log.d("UserActivity", "Медитації за вказаними ідентифікаторами не знайдені.")
                        }
                    }
                }
            }
        } else {
            userNameTextView.text = "Користувач не увійшов у систему"
            registrationDateTextView.text = ""
        }

        // логіка для кнопки виходу
        logoutButton.setOnClickListener {
            auth.signOut() // вихід з акаунту
            val intent = Intent(this, LoginActivity::class.java) // Перехід на екран входу
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish() // закриття поточної активності
        }
    }

    private fun loadFavoriteMeditations(userId: String, onComplete: (List<String>) -> Unit) {
        repository.getFavorites(userId) { favoriteIds ->
            Log.d("UserActivity", "Завантажено ідентифікатори улюблених: $favoriteIds")
            onComplete(favoriteIds)
        }
    }
}
