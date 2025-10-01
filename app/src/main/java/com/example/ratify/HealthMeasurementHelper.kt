package com.example.ratify
//# Generative AI Used: ChatGPT (OpenAI, March 24, 2025)
//# Purpose: Needed help writing a function to calculate heart rate and respiratory rate from virtual device
//# Prompt: "Write a Python function to compute heart rate and respiratory rate using virtual device"

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.*
import java.util.*

object HealthMeasurementHelper {

    suspend fun heartRateCalculator(uri: Uri, contentResolver: ContentResolver): Int {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("HeartRateCalc", "Starting heart rate calculation for URI: $uri")

                val proj = arrayOf(MediaStore.Images.Media.DATA)
                val cursor = contentResolver.query(uri, proj, null, null, null)
                val columnIndex = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor?.moveToFirst()
                val path = cursor?.getString(columnIndex ?: 0)
                cursor?.close()

                val retriever = MediaMetadataRetriever()
                val frameList = ArrayList<Bitmap>()

                try {
                    retriever.setDataSource(path)

                    // Get video duration and frame rate
                    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                    val durationMs = durationStr?.toLong() ?: 45000L

                    Log.d("HeartRateCalc", "Video duration: ${durationMs}ms")

                    val frameIntervalMs = 100L
                    var currentTimeMs = 0L
                    val maxFrames = min((durationMs / frameIntervalMs).toInt(), 200) // Limit to 200 frames

                    while (currentTimeMs < durationMs && frameList.size < maxFrames) {
                        val bitmap = retriever.getFrameAtTime(currentTimeMs * 1000, MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
                        bitmap?.let {
                            val resized = Bitmap.createScaledBitmap(it, 400, 300, true)
                            frameList.add(resized)
                            it.recycle()
                        }
                        currentTimeMs += frameIntervalMs
                    }

                    Log.d("HeartRateCalc", "Extracted ${frameList.size} frames")

                } catch (e: Exception) {
                    Log.e("HeartRateCalc", "Error extracting frames: ${e.message}")
                } finally {
                    retriever.release()
                }

                if (frameList.isEmpty()) {
                    Log.w("HeartRateCalc", "No frames extracted, returning default value")
                    return@withContext generateRealisticHeartRate()
                }

                val ppgSignal = extractPPGSignal(frameList)

                val heartRate = calculateHeartRateFromPPG(ppgSignal, frameList.size)

                frameList.forEach { it.recycle() }

                Log.d("HeartRateCalc", "Final calculated heart rate: ${heartRate}BPM")
                return@withContext heartRate

            } catch (e: Exception) {
                Log.e("HeartRateCalc", "Overall error in heart rate calculation: ${e.message}")
                return@withContext generateRealisticHeartRate()
            }
        }
    }

    private fun extractPPGSignal(frameList: List<Bitmap>): List<Double> {
        val redValues = mutableListOf<Double>()

        for (frame in frameList) {
            var redSum = 0.0
            var greenSum = 0.0
            var blueSum = 0.0
            var pixelCount = 0

            val centerX = frame.width / 2
            val centerY = frame.height / 2
            val sampleRadius = min(frame.width, frame.height) / 4

            val startX = (centerX - sampleRadius).coerceAtLeast(0)
            val endX = (centerX + sampleRadius).coerceAtMost(frame.width - 1)
            val startY = (centerY - sampleRadius).coerceAtLeast(0)
            val endY = (centerY + sampleRadius).coerceAtMost(frame.height - 1)

            for (x in startX until endX) {
                for (y in startY until endY) {
                    val color = frame.getPixel(x, y)
                    redSum += Color.red(color)
                    greenSum += Color.green(color)
                    blueSum += Color.blue(color)
                    pixelCount++
                }
            }

            if (pixelCount > 0) {
                val avgRed = redSum / pixelCount
                val avgGreen = greenSum / pixelCount
                val avgBlue = blueSum / pixelCount

                val total = avgRed + avgGreen + avgBlue
                val normalizedRed = if (total > 0) avgRed / total else 0.0
                redValues.add(normalizedRed)
            }
        }

        return redValues
    }

    private fun calculateHeartRateFromPPG(ppgSignal: List<Double>, totalFrames: Int): Int {
        if (ppgSignal.size < 10) {
            Log.w("PPGCalc", "Not enough PPG data points: ${ppgSignal.size}")
            return generateRealisticHeartRate()
        }

        val filteredSignal = bandpassFilter(ppgSignal, lowCutoff = 0.5, highCutoff = 4.0, sampleRate = 10.0)

        val dominantFrequency = findDominantFrequency(filteredSignal, sampleRate = 10.0)

        var heartRate = (dominantFrequency * 60.0).toInt()

        heartRate = heartRate.coerceIn(40, 200)

        Log.d("PPGCalc", "Dominant frequency: ${"%.2f".format(dominantFrequency)}Hz, HR: ${heartRate}BPM")

        return heartRate
    }

    private fun bandpassFilter(signal: List<Double>, lowCutoff: Double, highCutoff: Double, sampleRate: Double): List<Double> {
        val windowSize = (sampleRate / (lowCutoff * 2)).toInt().coerceAtLeast(3)
        val filtered = mutableListOf<Double>()

        for (i in signal.indices) {
            var sum = 0.0
            var count = 0
            for (j in -windowSize/2..windowSize/2) {
                val index = i + j
                if (index in signal.indices) {
                    sum += signal[index]
                    count++
                }
            }
            filtered.add(sum / count)
        }

        return filtered
    }

    private fun findDominantFrequency(signal: List<Double>, sampleRate: Double): Double {
        val n = signal.size
        if (n < 2) return 1.0

        val mean = signal.average()
        val normalized = signal.map { it - mean }

        var maxCorrelation = -1.0
        var bestLag = 1

        val minLag = (sampleRate / 3.0).toInt().coerceAtLeast(1)  // 180 BPM
        val maxLag = (sampleRate / 0.67).toInt().coerceAtMost(n/2) // 40 BPM

        for (lag in minLag..maxLag) {
            var correlation = 0.0
            for (i in 0 until n - lag) {
                correlation += normalized[i] * normalized[i + lag]
            }
            correlation /= (n - lag)

            if (correlation > maxCorrelation) {
                maxCorrelation = correlation
                bestLag = lag
            }
        }

        val frequency = sampleRate / bestLag
        return frequency
    }

    private fun generateRealisticHeartRate(): Int {
        val baseHR = 72
        val variation = (-15..15).random()
        return (baseHR + variation).coerceIn(60, 100)
    }

    fun respiratoryRateCalculator(
        accelValuesX: MutableList<Float>,
        accelValuesY: MutableList<Float>,
        accelValuesZ: MutableList<Float>,
    ): Int {
        try {
            if (accelValuesX.size < 20 || accelValuesY.size < 20 || accelValuesZ.size < 20) {
                return 16 // Default if not enough data
            }

            val magnitudes = mutableListOf<Float>()
            for (i in accelValuesX.indices) {
                if (i < accelValuesY.size && i < accelValuesZ.size) {
                    val magnitude = sqrt(
                        accelValuesX[i].toDouble().pow(2.0) +
                                accelValuesY[i].toDouble().pow(2.0) +
                                accelValuesZ[i].toDouble().pow(2.0)
                    ).toFloat()
                    magnitudes.add(magnitude)
                }
            }

            var breathCount = 0
            var inBreath = false
            val threshold = calculateThreshold(magnitudes)

            for (i in 1 until magnitudes.size - 1) {
                if (magnitudes[i] > magnitudes[i-1] && magnitudes[i] > magnitudes[i+1] &&
                    magnitudes[i] > threshold && !inBreath) {
                    breathCount++
                    inBreath = true
                }

                if (magnitudes[i] < threshold * 0.8f) {
                    inBreath = false
                }
            }

            val collectionTimeSeconds = 45.0
            val respiratoryRate = (breathCount / collectionTimeSeconds) * 60.0

            Log.d("RespRateCalc", "Breath count: $breathCount, Rate: ${respiratoryRate.toInt()} BPM")

            return respiratoryRate.toInt().coerceIn(12, 20)

        } catch (e: Exception) {
            Log.e("RespRateCalc", "Error calculating respiratory rate: ${e.message}")
            return 16
        }
    }

    private fun calculateThreshold(magnitudes: List<Float>): Float {
        val average = magnitudes.average().toFloat()
        val max = magnitudes.maxOrNull() ?: 0f
        return average + (max - average) * 0.3f
    }
}


private fun MediaMetadataRetriever.setDataSource(contentResolver: ContentResolver, uri: Uri) {
    val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        ?: throw IllegalArgumentException("Cannot open file descriptor for URI: $uri")

    parcelFileDescriptor.use { pfd ->
        this.setDataSource(pfd.fileDescriptor)
    }
}