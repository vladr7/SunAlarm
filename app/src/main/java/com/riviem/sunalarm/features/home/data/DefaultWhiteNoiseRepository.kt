package com.riviem.sunalarm.features.home.data

import android.content.Context
import android.media.MediaPlayer
import com.riviem.sunalarm.R

class DefaultWhiteNoiseRepository(
    private val context: Context,
) : WhiteNoiseRepository {
    private var mediaPlayer: MediaPlayer? = null
    private var isPlaying = false

    override fun playOrStopWhiteNoise(volume: Int) {
        if (isPlaying) {
            stopWhiteNoise()
        } else {
            playWhiteNoise(volume)
        }
        isPlaying = !isPlaying
    }

    private fun playWhiteNoise(volume: Int) {
        val volumeLevel = volume.toFloat() / 100f
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.jet_4h30mincut)
            mediaPlayer?.setVolume(volumeLevel, volumeLevel)
            mediaPlayer?.isLooping = true
        }
        mediaPlayer?.start()
    }

    private fun stopWhiteNoise() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}
