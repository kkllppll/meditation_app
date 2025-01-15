package com.example.meditation_app.model



data class Meditation(
    val id: String = "",            // Унікальний ідентифікатор медитації
    val title: String = "",         // Назва медитації
    val description: String = "",   // Опис медитації
    val category: String = "",      // Категорія
    val duration: Int = 0,          // Тривалість у хвилинах
    val type: String = "",          // Тип (sound)
    val isFavorite: Boolean = false,// Чи додано в улюблене
    var quote: String = "",         // Цитата динамічно завантажується через API
    var sound_id: Int = 0,         // динамічно завантажується через API
    var imageUrl: String = ""
)