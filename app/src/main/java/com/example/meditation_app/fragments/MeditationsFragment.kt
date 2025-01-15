package com.example.meditation_app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.meditation_app.MeditationDetailsActivity
import com.example.meditation_app.R
import com.example.meditation_app.adapter.MeditationsAdapter
import com.example.meditation_app.viewmodel.MeditationsViewModel
import com.google.firebase.auth.FirebaseAuth

class MeditationsFragment : Fragment() {

    private lateinit var viewModel: MeditationsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MeditationsAdapter
    private val auth = FirebaseAuth.getInstance()
    private val favoriteMeditationIds = mutableSetOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_meditations, container, false)

        viewModel = ViewModelProvider(this).get(MeditationsViewModel::class.java)
        // Показати медитації у списку, отримати медитації з Firebase Firestore
        recyclerView = view.findViewById(R.id.meditations_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // відповідає за вигляд кожного елемента списку
        adapter = MeditationsAdapter(
            meditations = emptyList(),
            favoriteMeditationIds = favoriteMeditationIds,
            onFavoriteToggle = { meditationId, isFavorite ->
                updateFavorites(meditationId, isFavorite)
            },
            onMeditationClick = { meditation ->
                val intent = Intent(requireContext(), MeditationDetailsActivity::class.java)
                intent.putExtra("MEDITATION_TITLE", meditation.title)
                intent.putExtra("MEDITATION_DESCRIPTION", meditation.description)
                intent.putExtra("SOUND_ID", meditation.sound_id)
                intent.putExtra("MEDITATION_CATEGORY", meditation.category)
                startActivity(intent)
            }
        )
        recyclerView.adapter = adapter

        val userId = auth.currentUser?.uid ?: return view
        viewModel.loadFavorites(userId)

        viewModel.favorites.observe(viewLifecycleOwner) { favorites ->
            favoriteMeditationIds.clear()
            favoriteMeditationIds.addAll(favorites)
            adapter.notifyDataSetChanged()
        }

        viewModel.meditations.observe(viewLifecycleOwner) { meditations ->
            adapter.submitList(meditations)
        }

        viewModel.loadMeditations()

        return view
    }

    private fun updateFavorites(meditationId: String, isFavorite: Boolean) {
        val userId = auth.currentUser?.uid ?: return
        viewModel.toggleFavorite(userId, meditationId)
    }
}
