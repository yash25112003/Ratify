# Project Documentation
**Name:** Yash Shah
**ASU ID:** 1237371241

## Program Description
This health monitoring application helps users track their vital signs and symptoms through a simple three-screen interface. The app starts with a main screen where users can choose to record new health data or delete existing records. When recording data, users measure heart rate by uploading a video file that gets processed to detect pulse patterns, and respiratory rate using movement data from provided CSV files. After obtaining these measurements, users proceed to a symptom tracking screen where they can rate common symptoms on a five-star scale. All information is stored securely on the device using Android's Room database, and users can completely erase their data at any time with a single tap.

## Generative AI Acknowledgment
Portions of the code in this project were generated with assistance from ChatGPT, an AI tool developed by OpenAI.

**Reference:** OpenAI. (2024). ChatGPT [Large language model]. openai.com/chatgpt

**Estimated percentage of code influenced by Generative AI:** 20%

## In-Code Citations

```kotlin
// Generative AI Used: ChatGPT (OpenAI, November 28, 2024)
// Purpose: Needed help implementing video frame extraction from MediaMetadataRetriever
// Prompt: "How do I extract frames from a video file at specific time intervals using MediaMetadataRetriever in Android?"
private fun extractVideoFrames(retriever: MediaMetadataRetriever, intervalMs: Long): List<Bitmap> {
    val frameList = ArrayList<Bitmap>()
    var currentTimeMs = 0L
    while (currentTimeMs < 45000) { // 45-second duration
        val bitmap = retriever.getFrameAtTime(currentTimeMs * 1000, 
                        MediaMetadataRetriever.OPTION_CLOSEST_SYNC)
        bitmap?.let { frameList.add(it) }
        currentTimeMs += intervalMs
    }
    return frameList
}

## Youtube Link
https://youtube.com/shorts/sw5_ALCBwjY
