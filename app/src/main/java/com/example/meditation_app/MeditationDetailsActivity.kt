package com.example.meditation_app

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.meditation_app.api.FreesoundApiClient
import com.example.meditation_app.api.FavQsApiClient
import com.example.meditation_app.api.MyMemoryApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MeditationDetailsActivity : AppCompatActivity() {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false
    private var isRunning = true
    private var isPaused = false
    private lateinit var playPauseButton: Button
    private lateinit var stopButton: Button
    private lateinit var seekBar: SeekBar
    private lateinit var currentTimeTextView: TextView
    private lateinit var durationTextView: TextView
    private lateinit var breathTextView: TextView
    private lateinit var timer: Timer
    private lateinit var breathingCycleTimer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meditation_details)

        // ініціалізація елементів ui
        val titleTextView: TextView = findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = findViewById(R.id.descriptionTextView)
        val quoteTextView: TextView = findViewById(R.id.quoteTextView)
        playPauseButton = findViewById(R.id.playPauseButton)
        stopButton = findViewById(R.id.stopButton)
        seekBar = findViewById(R.id.seekBar)
        currentTimeTextView = findViewById(R.id.currentTimeTextView)
        durationTextView = findViewById(R.id.durationTextView)
        breathTextView = findViewById(R.id.breathTextView)

        // отримання даних з Intent
        val title = intent.getStringExtra("MEDITATION_TITLE") ?: "Untitled"
        val description = intent.getStringExtra("MEDITATION_DESCRIPTION") ?: "No description available."
        val soundId = intent.getIntExtra("SOUND_ID", -1)
        val category = intent.getStringExtra("MEDITATION_CATEGORY") ?: "inspiration"

        // відображення отриманих даних
        titleTextView.text = title
        descriptionTextView.text = description

        // завантаження цитати через API
        loadQuote(category, quoteTextView)

        // завантаження звуку через Freesound API
        if (soundId != -1) {
            loadSound(soundId)
        } else {
            Toast.makeText(this, "Invalid Sound ID!", Toast.LENGTH_SHORT).show()
        }

        // запуск циклу дихання
        startBreathingCycle()

        // обробка кнопки Stop/Resume
        stopButton.setOnClickListener {
            toggleBreathingCycle()
        }
    }

    private fun loadQuote(category: String, quoteTextView: TextView) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Отримання цитати
                val response = FavQsApiClient.api.getQuotesByCategory(category)
                if (response.quotes.isNotEmpty()) {
                    val randomQuote = response.quotes.random()

                    // Переклад цитати через MyMemory API
                    val translatedText = MyMemoryApiClient.translate(randomQuote.body)

                    // Відображення перекладеної цитати
                    quoteTextView.text = "\"$translatedText\"\n- ${randomQuote.author}"
                } else {
                    quoteTextView.text = "Цитати для цієї категорії не знайдено."
                }
            } catch (e: Exception) {
                quoteTextView.text = "Не вдалося завантажити цитату: ${e.message}"
            }
        }
    }






    private fun loadSound(soundId: Int) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val soundDetails = FreesoundApiClient.api.getSoundDetails(
                    soundId,
                    "YulStpnj765Qip7JOmQFWA9MaGlSIr02hGFKKfnn"
                )
                val soundUrl = soundDetails.previews["preview-lq-mp3"]
                if (!soundUrl.isNullOrEmpty()) {
                    initializeMediaPlayer(soundUrl)
                } else {
                    Toast.makeText(this@MeditationDetailsActivity, "URL звуку не знайдено!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MeditationDetailsActivity, "Не вдалося завантажити звук: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun initializeMediaPlayer(url: String) {
        mediaPlayer = MediaPlayer().apply {
            setDataSource(url)
            prepare()
        }

        seekBar.max = mediaPlayer!!.duration
        durationTextView.text = formatTime(mediaPlayer!!.duration)

        timer = Timer()
        timer.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (mediaPlayer != null && isPlaying) {
                    runOnUiThread {
                        seekBar.progress = mediaPlayer!!.currentPosition
                        currentTimeTextView.text = formatTime(mediaPlayer!!.currentPosition)
                    }
                }
            }
        }, 0, 1000)

        playPauseButton.setOnClickListener {
            togglePlayPause()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                    currentTimeTextView.text = formatTime(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun startBreathingCycle() {
        val stages = listOf(
            "Вдихніть" to 5000L,
            "Затримайте дихання" to 4000L,
            "Видихніть" to 5000L,
            "Затримайте дихання" to 4000L
        )
        var stageIndex = 0

        fun startStage() {
            if (!isRunning) return
            if (stageIndex >= stages.size) stageIndex = 0

            val (instruction, duration) = stages[stageIndex]
            stageIndex++

            breathTextView.text = instruction
            breathingCycleTimer = object : CountDownTimer(duration, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    breathTextView.text = "$instruction (${millisUntilFinished / 1000}s)"
                }

                override fun onFinish() {
                    if (!isPaused) startStage()
                }
            }
            breathingCycleTimer.start()
        }

        startStage()
    }

    private fun toggleBreathingCycle() {
        if (isRunning) {
            isRunning = false
            isPaused = true
            if (::breathingCycleTimer.isInitialized) {
                breathingCycleTimer.cancel()
            }
            breathTextView.text = "Цикл дихання зупинено"
            stopButton.text = "Відтворити дихання"
        } else {
            isRunning = true
            isPaused = false
            startBreathingCycle()
            stopButton.text = "Зупинити дихання"
        }
    }

    private fun togglePlayPause() {
        if (mediaPlayer == null) return

        if (isPlaying) {
            mediaPlayer?.pause()
            playPauseButton.text = "Відтворити звук"
        } else {
            mediaPlayer?.start()
            playPauseButton.text = "Зупинити звук"
        }
        isPlaying = !isPlaying
    }

    private fun formatTime(milliseconds: Int): String {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        return dateFormat.format(milliseconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        mediaPlayer?.release()
        timer.cancel()
        if (::breathingCycleTimer.isInitialized) {
            breathingCycleTimer.cancel()
        }
    }
}
