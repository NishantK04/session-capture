# OralVis – Session-Based Image Capture App

## 📌 Overview
OralVis is an Android app that allows users to capture images **session-wise**, store metadata (SessionID, Name, Age) in a local **SQLite (Room)** database, and organize captured images in app-specific storage.  
Users can also **search sessions by SessionID** to view the stored metadata and images.

---
## 📸 Screenshots  
<img src="https://github.com/NishantK04/oralVis_app/blob/master/home.png" width="250"> <img src="https://github.com/NishantK04/oralVis_app/blob/master/preview.png" width="250"> <img src="https://github.com/NishantK04/oralVis_app/blob/master/info_add.png" width="250"> <img src="https://github.com/NishantK04/oralVis_app/blob/master/search.png" width="250">

---
##  Features
- 📷 **Start Session** → Capture multiple images using CameraX.  
- 📝 **End Session** → Save session metadata (SessionID, Name, Age).  
- 💾 **Storage**:
  - Metadata → stored in **SQLite (Room)** database.  
  - Images → stored in app-specific storage under:  
    ```
    Android/media/OralVis/Sessions/<SessionID>/IMG_timestamp.jpg
    ```
- 🔎 **Search Functionality** → Search by `SessionID` to display session details and images.  
- 🏗️ **Architecture** → MVVM (Model–View–ViewModel).  

---

##  Tech Stack
- **Language**: Kotlin
- **UI**: XML + Material 3  
- **Camera**: CameraX  
- **Database**: Room (SQLite)  
- **Architecture**: MVVM  
- **Scoped Storage** for saving images  

---

##  Installation & Setup
```sh
# Clone the repository
git clone https://github.com/NishantK04/oralVis_app.git

# Open the project in Android Studio

# Sync Gradle dependencies

# Run on emulator or physical device
