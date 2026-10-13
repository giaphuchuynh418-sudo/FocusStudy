package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.sin

enum class AmbientSoundType(val title: String, val subtitle: String, val iconName: String) {
    OFF("Yên Tĩnh", "Không phát âm thanh nền", "volume_off"),
    RAIN("Mưa Dịu Êm", "Tiếng mưa rơi nhẹ nhàng", "water_drop"),
    BINAURAL("Sóng Não 40Hz", "Sóng gamma tăng tập trung sâu", "waves"),
    WHITE_NOISE("Tiếng Ồn Trắng", "Khử tạp âm xung quanh", "graphic_eq"),
    CAFE("Quán Cà Phê", "Âm thanh thư thái ấm áp", "coffee")
}

class AmbientSoundManager {
    private var audioTrack: AudioTrack? = null
    private var playJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)
    private var currentType: AmbientSoundType = AmbientSoundType.OFF

    fun playSound(type: AmbientSoundType) {
        stopSound()
        currentType = type
        if (type == AmbientSoundType.OFF) return

        playJob = scope.launch {
            val sampleRate = 22050
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(sampleRate / 4)

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
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
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack = track
            track.play()

            val buffer = ShortArray(bufferSize / 2)
            val random = Random()
            var phase1 = 0.0
            var phase2 = 0.0
            var lastSample = 0.0

            while (isActive) {
                for (i in buffer.indices) {
                    val sampleVal: Double = when (type) {
                        AmbientSoundType.RAIN -> {
                            // Filtered brown/pink noise with soft low-pass filter
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            lastSample = (lastSample * 0.94) + (white * 0.06)
                            lastSample * 0.35
                        }
                        AmbientSoundType.WHITE_NOISE -> {
                            // Gentle white noise attenuated
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            white * 0.12
                        }
                        AmbientSoundType.BINAURAL -> {
                            // 200 Hz carrier and 240 Hz beat (40 Hz gamma difference for intense focus)
                            phase1 += 2.0 * PI * 200.0 / sampleRate
                            phase2 += 2.0 * PI * 240.0 / sampleRate
                            if (phase1 > 2 * PI) phase1 -= 2 * PI
                            if (phase2 > 2 * PI) phase2 -= 2 * PI
                            val s1 = sin(phase1) * 0.15
                            val s2 = sin(phase2) * 0.15
                            (s1 + s2) * 0.5
                        }
                        AmbientSoundType.CAFE -> {
                            // Low-frequency warm rumble and gentle multi-frequency hum
                            phase1 += 2.0 * PI * 130.81 / sampleRate // C3 note
                            if (phase1 > 2 * PI) phase1 -= 2 * PI
                            val white = (random.nextDouble() * 2.0 - 1.0) * 0.08
                            lastSample = (lastSample * 0.96) + (white * 0.04)
                            (sin(phase1) * 0.1) + lastSample
                        }
                        AmbientSoundType.OFF -> 0.0
                    }
                    val clamped = (sampleVal * Short.MAX_VALUE).coerceIn(
                        Short.MIN_VALUE.toDouble(),
                        Short.MAX_VALUE.toDouble()
                    ).toInt().toShort()
                    buffer[i] = clamped
                }
                track.write(buffer, 0, buffer.size)
            }
        }
    }

    fun stopSound() {
        playJob?.cancel()
        playJob = null
        try {
            audioTrack?.pause()
            audioTrack?.flush()
            audioTrack?.release()
        } catch (_: Exception) {
        }
        audioTrack = null
        currentType = AmbientSoundType.OFF
    }

    fun playCompletionChime() {
        scope.launch {
            val sampleRate = 44100
            val frequencies = listOf(329.63, 415.30, 493.88, 659.25) // E4, G#4, B4, E5 chime chord
            val noteDurationMs = 280
            val totalSamples = (sampleRate * (noteDurationMs * frequencies.size + 400) / 1000)
            val buffer = ShortArray(totalSamples)

            var sampleIdx = 0
            for (freq in frequencies) {
                val samplesForNote = (sampleRate * noteDurationMs / 1000)
                for (n in 0 until samplesForNote) {
                    if (sampleIdx >= buffer.size) break
                    val t = n.toDouble() / sampleRate
                    // Exponential decay envelope
                    val envelope = kotlin.math.exp(-3.5 * t)
                    val value = sin(2.0 * PI * freq * t) * envelope * 0.4
                    buffer[sampleIdx++] = (value * Short.MAX_VALUE).toInt().toShort()
                }
            }

            // Ring out tail
            while (sampleIdx < buffer.size) {
                buffer[sampleIdx++] = 0
            }

            try {
                val chimeTrack = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
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
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                chimeTrack.write(buffer, 0, buffer.size)
                chimeTrack.play()
                // Sleep briefly then release
                SystemClock.sleep(1500)
                chimeTrack.release()
            } catch (_: Exception) {
            }
        }
    }
}
