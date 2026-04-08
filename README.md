# 📱 App Development Mini Projects

Welcome to the **App-dev-mini-projects** repository! This collection features four distinct Android applications developed as part of an academic assignment. Each project focuses on learning and implementing core concepts of Android development, ranging from basic UI interactions to advanced hardware and storage integration.

## 🚀 Projects Overview

### 1. Currency Converter (`Q1_CurrencyConverter`)
A simple yet effective utility app to convert currencies.
* **Features:** Takes user input, applies mathematical conversion rates, and displays real-time results.
* **Concepts Learned:** UI layout design (LinearLayout, EditText, TextView), basic Event Listeners, and String-to-Double type casting.

### 2. Dual Media Player (`Q2_MediaPlayer`)
An entertainment app capable of handling both local and online media playback.
* **Features:** * Plays a video stream from a live URL.
  * Controls (Play, Pause, Stop, Restart) a local audio file stored in the app.
* **Concepts Learned:** `VideoView`, `MediaPlayer` API, handling Internet permissions, and media state management.

### 3. Hardware Sensor Monitor (`Q3_SensorReader`)
A diagnostic app that interacts directly with the device's physical hardware in real-time.
* **Features:** Live monitoring of the Accelerometer (X, Y, Z motion), Light Sensor (Lux), and Proximity Sensor (Distance in cm).
* **Concepts Learned:** `SensorManager`, implementing `SensorEventListener`, and optimizing battery life by managing sensors efficiently during `onResume` and `onPause` lifecycle events.

### 4. Advanced Photo Gallery (`Q4_PhotoGallery`)
The flagship project of this repository. A fully functional camera and gallery application utilizing modern Android security practices.
* **Features:**
  * **Folder Selection:** Users can choose a specific directory to save their photos using the Storage Access Framework (SAF).
  * **Camera Integration:** Capture high-resolution photos using the device's native camera.
  * **Grid Gallery:** Displays all images from the selected folder in a clean, scrollable GridView.
  * **Detailed View:** Clicking an image opens a details page displaying the image, Name, Path, Size (KB), and Date Taken.
  * **File Management:** Permanently delete images with a secure confirmation dialog.
* **Concepts Learned:** * `FileProvider` and `MediaStore` Intents for secure camera access.
  * `ACTION_OPEN_DOCUMENT_TREE` and `DocumentFile` for reading/writing outside app-specific storage.
  * **Memory Optimization:** Implementing `BitmapFactory.Options` (`inSampleSize`) to compress images on the fly, successfully preventing `Out-of-Memory (OOM)` crashes when loading heavy camera files into the UI.
  * Custom `BaseAdapter` for GridViews and dynamic `AlertDialogs`.

## 🛠️ Tech Stack
* **Language:** Java
* **Framework:** Android SDK (Optimized for API 34 / Android 14)
* **UI:** XML Layouts
* **IDE:** Android Studio

## ⚙️ How to Run
1. Clone this repository to your local machine:
   ```bash
   git clone [https://github.com/your-username/App-dev-mini-projects.git](https://github.com/your-username/App-dev-mini-projects.git)
