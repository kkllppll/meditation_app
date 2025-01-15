package com.example.meditation_app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation_app.R
import com.example.meditation_app.model.Meditation

class MeditationsAdapter(
    private var meditations: List<Meditation>,
    private val favoriteMeditationIds: Set<String>, // Список улюблених медитацій
    private val onFavoriteToggle: (String, Boolean) -> Unit, // Обробник улюблених
    private val onMeditationClick: (Meditation) -> Unit // Обробник натискання на медитацію
) : RecyclerView.Adapter<MeditationsAdapter.MeditationViewHolder>() {

    // viewHolder відповідає за відображення одного елемента списку
    inner class MeditationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        private val descriptionTextView: TextView = view.findViewById(R.id.descriptionTextView)
        private val favoriteButton: ImageView = view.findViewById(R.id.favoriteButton)

        fun bind(meditation: Meditation) {
            titleTextView.text = meditation.title
            descriptionTextView.text = meditation.description

            // зміна кольору іконки залежно від статусу улюбленого
            val isFavorite = favoriteMeditationIds.contains(meditation.id)
            favoriteButton.setImageResource(
                if (isFavorite) R.drawable.ic_favorite else R.drawable.ic_favorite_border
            )

            // обробка натискання на іконку улюбленого
            favoriteButton.setOnClickListener {
                onFavoriteToggle(meditation.id, isFavorite)
            }

            // обробка натискання на елемент
            itemView.setOnClickListener {
                onMeditationClick(meditation)
            }
        }
    }

    // створення нового ViewHolder для кожного елемента списку
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meditation, parent, false) // Підключення XML для елемента
        return MeditationViewHolder(view)
    }

    // прив’язка даних до ViewHolder
    override fun onBindViewHolder(holder: MeditationViewHolder, position: Int) {
        holder.bind(meditations[position])
    }

    // повертаємо кількість елементів у списку
    override fun getItemCount(): Int = meditations.size

    // оновлення списку даних
    fun submitList(newMeditations: List<Meditation>) {
        meditations = newMeditations
        notifyDataSetChanged() // Оновлення RecyclerView
    }
}
