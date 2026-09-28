package com.example.islandconnect.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticsManager(context: Context) {
    var isEnabled: Boolean = true

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private fun vibrate(durationMs: Long, amplitude: Int = -1) {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = if (amplitude > 0 && vibrator.hasAmplitudeControl()) {
                    VibrationEffect.createOneShot(durationMs, amplitude.coerceIn(1, 255))
                } else {
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Ignore haptic failures
        }
    }

    fun onTileSelect() {
        vibrate(10, amplitude = 40)
    }

    fun onTileRotate() {
        vibrate(15, amplitude = 60)
    }

    fun onTilePlaced() {
        vibrate(25, amplitude = 120)
    }

    fun onIslandConnected() {
        vibrate(45, amplitude = 180)
    }

    fun onPuzzleComplete() {
        if (!isEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 40, 60, 80)
                val amplitudes = intArrayOf(0, 160, 0, 240)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(120)
            }
        } catch (_: Exception) {
            vibrate(100, 200)
        }
    }
}
