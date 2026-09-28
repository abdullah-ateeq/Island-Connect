package com.example.islandconnect.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default)
    var isEnabled: Boolean = true

    private val sampleRate = 44100

    private fun playTone(
        frequencies: List<Pair<Double, Int>>, // (frequency in Hz, duration in ms)
        baseVolume: Float = 0.5f,
        type: ToneType = ToneType.SINE
    ) {
        if (!isEnabled) return

        scope.launch {
            try {
                val totalMs = frequencies.sumOf { it.second }
                val totalSamples = (sampleRate * totalMs) / 1000
                val pcmData = ShortArray(totalSamples)

                var sampleIndex = 0
                for ((freq, durationMs) in frequencies) {
                    val segSamples = (sampleRate * durationMs) / 1000
                    for (i in 0 until segSamples) {
                        if (sampleIndex >= totalSamples) break
                        // Attack-decay envelope to prevent clicking
                        val attackLength = minOf(150, segSamples / 4)
                        val decayLength = minOf(300, segSamples / 3)
                        val envelope = when {
                            i < attackLength -> i.toFloat() / attackLength
                            i > segSamples - decayLength -> (segSamples - i).toFloat() / decayLength
                            else -> 1f
                        }

                        val time = i.toDouble() / sampleRate
                        val rawSample = when (type) {
                            ToneType.SINE -> sin(2.0 * PI * freq * time)
                            ToneType.WARM -> 0.7 * sin(2.0 * PI * freq * time) + 0.3 * sin(4.0 * PI * freq * time)
                        }

                        val sampleVal = (rawSample * envelope * baseVolume * Short.MAX_VALUE).toInt()
                        pcmData[sampleIndex++] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }

                val bufferSize = pcmData.size * 2
                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(pcmData, 0, pcmData.size)
                audioTrack.play()

                // Release after playback
                kotlinx.coroutines.delay(totalMs + 50L)
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {
                // Ignore audio generation failures gracefully
            }
        }
    }

    enum class ToneType {
        SINE,
        WARM
    }

    fun playSelect() {
        playTone(listOf(820.0 to 35), baseVolume = 0.35f, type = ToneType.SINE)
    }

    fun playRotate() {
        playTone(listOf(620.0 to 25, 880.0 to 35), baseVolume = 0.4f, type = ToneType.WARM)
    }

    fun playPlace() {
        playTone(listOf(340.0 to 50, 260.0 to 40), baseVolume = 0.5f, type = ToneType.WARM)
    }

    fun playIslandConnected() {
        playTone(
            listOf(587.33 to 90, 880.0 to 180),
            baseVolume = 0.6f,
            type = ToneType.WARM
        )
    }

    fun playNetworkCompleted() {
        playTone(
            listOf(
                523.25 to 100, // C5
                659.25 to 100, // E5
                783.99 to 120, // G5
                1046.50 to 280 // C6
            ),
            baseVolume = 0.65f,
            type = ToneType.WARM
        )
    }

    fun playFailure() {
        playTone(
            listOf(330.0 to 120, 261.63 to 180),
            baseVolume = 0.5f,
            type = ToneType.WARM
        )
    }
}
