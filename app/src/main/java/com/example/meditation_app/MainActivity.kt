package com.example.meditation_app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

import androidx.fragment.app.Fragment
import com.example.meditation_app.fragments.MeditationsFragment

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // ініціалізація нижньої панелі навігації
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // встановлення стартового фрагмента (екран медитацій)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MeditationsFragment())
                .commit()
        }

        // логіка перемикання фрагментів
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meditations -> {
                    loadFragment(MeditationsFragment())
                    true
                }
                R.id.nav_user -> {
                    // Перехід до UserActivity
                    val intent = Intent(this, UserActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    // метод для завантаження потрібного фрагмента
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}


