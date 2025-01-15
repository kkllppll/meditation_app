package com.example.meditation_app

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth


class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)

        // ініціалізація firebase authentication
        auth = FirebaseAuth.getInstance()

        // затримка для показу Splash-екрану
        Handler(Looper.getMainLooper()).postDelayed({
            val currentUser = auth.currentUser

            if (currentUser != null) {
                // перевірка токена користувача
                currentUser.getIdToken(true).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // токен валідний переходимо до MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                    } else {
                        // токен невалідний переходимо до LoginActivity
                        startActivity(Intent(this, LoginActivity::class.java))
                    }
                    finish()
                }
            } else {
                // користувач не залогінений або був видалений
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }, 2000) // splashекран показується 2 секунди
    }
}
