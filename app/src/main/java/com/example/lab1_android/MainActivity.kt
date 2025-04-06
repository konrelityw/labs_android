package com.example.lab1_android

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.mediaplayer.R

class MainActivity : AppCompatActivity() {

    private lateinit var radioAudio: RadioButton
    private lateinit var radioVideo: RadioButton
    private lateinit var btnSelectFile: Button
    private lateinit var btnPlayLocal: Button
    private lateinit var btnPlayPause: Button
    private lateinit var btnStop: Button
    private lateinit var btnLoadFromUrl: Button
    private lateinit var urlInput: EditText
    private lateinit var playerView: PlayerView
    private lateinit var audioOverlay: TextView

    private var player: ExoPlayer? = null
    private var isPlaying = false
    private var isAudioMode = true

    companion object {
        private const val REQUEST_PERMISSIONS = 1
        private const val REQUEST_FILE = 2
        private const val TAG = "MediaPlayerApp"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        requestPermissions()
        initializePlayer()
        setupEventListeners()
    }

    private fun initializeViews() {
        radioAudio = findViewById(R.id.radioAudio)
        radioVideo = findViewById(R.id.radioVideo)
        btnSelectFile = findViewById(R.id.btnSelectFile)
        btnPlayLocal = findViewById(R.id.btnPlayLocal)
        btnPlayPause = findViewById(R.id.btnPlayPause)
        btnStop = findViewById(R.id.btnStop)
        btnLoadFromUrl = findViewById(R.id.btnLoadFromUrl)
        urlInput = findViewById(R.id.urlInput)
        playerView = findViewById(R.id.playerView)
        audioOverlay = findViewById(R.id.audioOverlay)
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toTypedArray(), REQUEST_PERMISSIONS)
        }
    }

    private fun initializePlayer() {
        Log.d(TAG, "Initializing player")
        player = ExoPlayer.Builder(this).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_READY -> Log.d(TAG, "Player ready")
                        Player.STATE_BUFFERING -> Log.d(TAG, "Buffering...")
                        Player.STATE_ENDED -> {
                            this@MainActivity.isPlaying = false
                            btnPlayPause.text = "Відтворити"
                        }
                        Player.STATE_IDLE -> Log.d(TAG, "Player idle")
                        else -> {}
                    }
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    Log.e(TAG, "Playback error: ${error.message}")
                    Toast.makeText(this@MainActivity, "Помилка відтворення: ${error.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
        playerView.player = player
        updatePlayerViewVisibility()
    }

    @OptIn(UnstableApi::class) private fun updatePlayerViewVisibility() {
        playerView.visibility = android.view.View.VISIBLE
        playerView.useController = true
        playerView.controllerAutoShow = true

        if (isAudioMode) {
            audioOverlay.visibility = android.view.View.VISIBLE
            Toast.makeText(this, "Аудіо режим увімкнено", Toast.LENGTH_SHORT).show()
        } else {
            audioOverlay.visibility = android.view.View.GONE
            Toast.makeText(this, "Відео режим увімкнено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupEventListeners() {
        radioAudio.setOnClickListener {
            isAudioMode = true
            updatePlayerViewVisibility()
        }
        radioVideo.setOnClickListener {
            isAudioMode = false
            updatePlayerViewVisibility()
        }
        btnSelectFile.setOnClickListener { selectFile() }
        btnPlayLocal.setOnClickListener { playLocalFile() }
        btnPlayPause.setOnClickListener { togglePlayPause() }
        btnStop.setOnClickListener { stopPlayback() }
        btnLoadFromUrl.setOnClickListener { loadFromUrl() }
    }

    private fun selectFile() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = if (isAudioMode) "audio/*" else "video/*"
        }
        startActivityForResult(intent, REQUEST_FILE)
    }

    private fun playLocalFile() {
        val resourceId = if (isAudioMode) {
            R.raw.sample_audio
        } else {
            R.raw.sample_video
        }
        val uri = Uri.parse("android.resource://${packageName}/$resourceId")
        Log.d(TAG, "Playing local file: $uri")
        playMedia(uri)
    }

    private fun togglePlayPause() {
        player?.let {
            if (isPlaying) {
                it.pause()
                btnPlayPause.text = "Відтворити"
            } else {
                it.play()
                btnPlayPause.text = "Пауза"
            }
            isPlaying = !isPlaying
        }
    }

    private fun stopPlayback() {
        player?.let {
            it.stop()
            it.clearMediaItems()
            isPlaying = false
            btnPlayPause.text = "Відтворити"
        }
    }

    private fun loadFromUrl() {
        val url = urlInput.text.toString().trim()
        if (url.isNotEmpty()) {
            if (url.contains("youtube.com") || url.contains("youtu.be")) {
                Toast.makeText(this, "YouTube відео не підтримуються. Використовуйте прямий URL до медіафайлу.", Toast.LENGTH_LONG).show()
                return
            }
            Log.d(TAG, "Loading from URL: $url")
            playMedia(Uri.parse(url), isFromWeb = true)
        } else {
            Toast.makeText(this, "Будь ласка, введіть URL", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playMedia(uri: Uri, isFromWeb: Boolean = false) {
        Log.d(TAG, "Playing media: $uri")
        if (!isFromWeb && uri.scheme == "content") {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            try {
                contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: SecurityException) {
                Log.e(TAG, "Failed to take persistable permission: ${e.message}")
            }
        }

        val mediaItem = MediaItem.fromUri(uri)
        player?.let {
            it.clearMediaItems()
            it.setMediaItem(mediaItem)
            it.prepare()
            it.play()
            isPlaying = true
            btnPlayPause.text = "Пауза"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                Log.d(TAG, "File selected: $uri")
                try {
                    playMedia(uri)
                } catch (e: Exception) {
                    Log.e(TAG, "Error playing file: ${e.message}")
                    Toast.makeText(this, "Помилка відтворення файлу: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(this, "Дозволи отримано", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Дозволи відхилено, деякі функції можуть бути недоступні", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (player == null) initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        if (player == null) initializePlayer()
    }

    override fun onPause() {
        super.onPause()
        player?.pause()
        isPlaying = false
        btnPlayPause.text = "Відтворити"
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    private fun releasePlayer() {
        Log.d(TAG, "Releasing player resources")
        player?.let {
            it.stop()
            it.clearMediaItems()
            it.release()
            player = null
        }
    }
}