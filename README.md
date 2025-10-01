# Project Documentation
**Name:** Yash Shah
**ASU ID:** 1237371241

## Program Description
This health monitoring application helps users track their vital signs and symptoms through a simple three-screen interface. The app starts with a main screen where users can choose to record new health data or delete existing records. When recording data, users measure heart rate by uploading a video file that gets processed to detect pulse patterns, and respiratory rate using movement data from provided CSV files. After obtaining these measurements, users proceed to a symptom tracking screen where they can rate common symptoms on a five-star scale. All information is stored securely on the device using Android's Room database, and users can completely erase their data at any time with a single tap.

## Short Answers

**Imagine you are new to the programming world and not proficient enough in coding. But, you have a brilliant idea where you want to develop a context-sensing application like Project 1.  You come across the Heath-Dev paper and want it to build your application. Specify what Specifications you should provide to the Health-Dev framework to develop the code ideally.**

If I have a smart idea for an app that senses things around me and helps people with health but I am new to coding, I would want Health-Dev to make this app for me by giving it all the simple information it needs. First, I would clearly say which sensors I want to use, like a heart sensor or a temperature sensor, and what each sensor should check for. I would mention how often the sensors should take readings, like every second for heart rate or every minute for temperature. I would tell what kind of device the sensors are on, such as Arduino or another board, so it fits my setup. For each sensor, I would explain what I want to happen with the data, like if I need it to find sudden changes, show the latest number, or compare it to something else. I would write down how I want the data to be sent maybe using Bluetooth or ZigBee—and whether it should go right to my phone or to another sensor first. I would also give a simple name or number for each sensor, so they do not get mixed up. If the sensors need to talk to each other, I would draw or describe how they are connected so it is clear who talks to whom. On the phone side, I would write out what I want to see and use, like buttons to start or stop the sensors, or whether I want to see numbers, graphs, or lists. I would describe what should be on the phone screen in plain words and also say if I want to change how the sensors work using the phone. By giving Health-Dev all these details in simple language, it can understand exactly what I want, make the code for me, and build the app to match my idea even if I do not know how to do the coding myself.

**In Project 1 you have stored the user’s symptoms data in the local server. Using the bHealthy application suite how can you provide feedback to the user and develop a novel application to improve context sensing and use that to generate the model of the user?** 

After finishing Project 1 and reading both papers, my views about mobile computing have definitely changed. Before, I thought mobile computing was only about making phone apps, like games or social media, because that is what people talk about the most. But I learned that mobile computing is really much bigger and deeper than just apps. It is about how phones and mobile devices use sensors, collect data, and understand what is happening around the user. For example, in Project 1, I saw how we can use a phone’s sensors and link it to health data to help people manage their symptoms. The papers showed me that phones can help sense our real-life situations, analyze patterns, and even give advice or feedback using this data. Mobile computing is about connecting all these things—devices, sensors, networks, and smart thinking to make life better or easier, not just for fun or for chatting. I now think mobile computing is more about understanding and improving people’s lives using the smart tools in our phones instead of only making regular apps. Examples like health apps that read our heart rate and give tips, or context apps that learn what we do and suggest better habits, prove that mobile computing is a wide field that mixes hardware, software, and real-world context, not just programs or games.

**A common assumption is mobile computing is mostly about app development. After completing Project 1 and reading both papers, have your views changed? If yes, what do you think mobile computing is about and why? If no, please explain why you still think mobile computing is mostly about app development, providing examples to support your viewpoint** 

Before starting Project 1 and reading the two papers, I used to think mobile computing was just about developing apps for phones. This idea was based on seeing so many apps for games, messaging, or shopping, and feeling like app stores are the center of the mobile world. But after doing the project and reading deeply, my views have changed a lot. Now, I understand that mobile computing is about much more than just writing and launching apps. It is about how mobile devices can sense, collect, and use information from the world and the user, going beyond traditional app screens. For example, in Project 1, I learned that mobile computing can involve gathering health data like heart rate and breathing, sometimes even without the actual sensors present by making use of datasets and emulators. The research papers helped me see that mobile computing includes how devices connect to other sensors, analyze data, protect privacy, adapt to the context, and help people in real-time. It is about creating systems that understand a person’s habits, routines, or health needs, and then adjust or provide feedback automatically. Instead of just coding buttons and features, mobile computing means designing solutions that merge software, hardware, real-world data, and user experience together. This perspective changed the way I look at mobile technology: it’s about solving real problems and supporting people anywhere and anytime, not just launching another new app. So now, I think of mobile computing as a whole field of smart, connected solutions that can work in many areas like health, safety, learning, and personal wellbeing.

## Youtube Link
https://youtu.be/0VtaYzfVeS0
https://youtube.com/shorts/sw5_ALCBwjY

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


