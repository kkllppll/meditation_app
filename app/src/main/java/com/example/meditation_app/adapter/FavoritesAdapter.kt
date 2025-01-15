package com.example.meditation_app.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.BaseAdapter
import com.example.meditation_app.MeditationDetailsActivity
import com.example.meditation_app.R
import com.example.meditation_app.model.Meditation

class FavoritesAdapter(
    private val context: Context,
    private val favoriteMeditations: List<Meditation>
) : BaseAdapter() {

    override fun getCount(): Int = favoriteMeditations.size

    override fun getItem(position: Int): Meditation = favoriteMeditations[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // інфлейт елементу списку
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_favorite_meditation, parent, false
        )

        // отримуємо дані медитації
        val meditation = getItem(position)

        // відображення назви медитації
        val meditationTitle = view.findViewById<TextView>(R.id.meditationTitleTextView)
        meditationTitle.text = meditation.title

        // відображення опису медитації
        val meditationDescription = view.findViewById<TextView>(R.id.meditationDescriptionTextView)
        meditationDescription.text = meditation.description

        // клік на елемент списку
        view.setOnClickListener {
            Log.d("FavoritesAdapter", "soundId being passed: ${meditation.sound_id}")

            // передаємо дані через Intent
            val intent = Intent(context, MeditationDetailsActivity::class.java).apply {
                putExtra("SOUND_ID", meditation.sound_id)
                putExtra("MEDITATION_ID", meditation.id)
                putExtra("MEDITATION_TITLE", meditation.title)
                putExtra("MEDITATION_DESCRIPTION", meditation.description)
                putExtra("MEDITATION_CATEGORY", meditation.category)

            }
            context.startActivity(intent)
        }

        return view
    }
}
