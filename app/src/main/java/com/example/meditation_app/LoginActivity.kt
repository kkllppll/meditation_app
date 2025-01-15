package com.example.meditation_app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.Timestamp
import android.util.Log

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var statusTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_activity)

        // ініціалізація Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // підключення елементів інтерфейсу
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginButton = findViewById<Button>(R.id.loginButton)
        statusTextView = findViewById(R.id.statusTextView)

        // обробка натискання кнопки реєстрації
        registerButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                registerUser(email, password)
            } else {
                showStatusMessage("Будь ласка, заповніть всі поля", false)
            }
        }

        // обробка натискання кнопки входу
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                showStatusMessage("Будь ласка, заповніть усі поля", false)
            }
        }
    }

    // функція для відображення статусу
    private fun showStatusMessage(message: String, isSuccess: Boolean) {
        statusTextView.text = message
        statusTextView.setTextColor(
            if (isSuccess) getColor(R.color.primary) else getColor(R.color.secondary)
        )
        statusTextView.visibility = TextView.VISIBLE
    }

    // функція реєстрації
    private fun registerUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore() // Додаємо користувача у Firestore
                    showStatusMessage("Реєстрація успішна!", true)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showStatusMessage("Реєстрація не вдалася: ${task.exception?.message}", false)
                }
            }
    }

    // функція входу
    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveUserToFirestore() // Додаємо користувача у Firestore
                    showStatusMessage("Вхід успішний!", true)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showStatusMessage("Вхід не вдався: ${task.exception?.message}", false)
                }
            }
    }

    // функція для збереження користувача у Firestore
    private fun saveUserToFirestore() {
        val db = FirebaseFirestore.getInstance() // Ініціалізація Firestore
        val currentUser = FirebaseAuth.getInstance().currentUser // Отримання поточного користувача

        if (currentUser != null) {
            val user = hashMapOf(
                "user_id" to currentUser.uid, // UID з Firebase Authentication
                "email" to currentUser.email, // Електронна пошта
                "registration_date" to Timestamp.now() // Поточна дата і час
            )

            // збереження користувача у Firestore
            db.collection("Users").document(currentUser.uid)
                .set(user)
                .addOnSuccessListener {
                    Log.d("Firestore", "User successfully added")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error adding user: ${e.message}")
                }
        }
    }
}
