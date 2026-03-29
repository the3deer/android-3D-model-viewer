# Android 3D Model Viewer

**[Source Code on GitHub](https://github.com/the3deer/android-3D-model-viewer)**

A feature-rich Android application to load, inspect, and visualize 3D models, built on top of the [Android Model Engine](android-model-engine.html).

This open-source project serves as both a powerful utility for viewing 3D assets and a comprehensive demonstration of modern Android development practices with OpenGL.

## News (10/04/2026)

**Version 5.0.0**
* **Engine**: Upgraded to **OpenGL 3** (OpenGL 2 still supported). Support for multiple **Scenes**, **Cameras**, and **Animations**.
* **UI**: Completely rewritten using **Android Fragments** and **Navigation Component**.
* **Internationalization**: Added **Spanish** language support.
* **Parsers**: Rewritten DAE & GLTF loaders. Basic **FBX** support added via `ufbx`.

## Features

- [x] **Wide Format Support**:
    - [x] GLTF (`.gltf`, `.glb`)
    - [x] FBX (`.fbx`) - Basic support (static models)
    - [x] DAE (Collada)
    - [x] OBJ (Wavefront)
    - [x] STL (Stereolithography)
- [x] **Advanced Rendering**:
    - [x] OpenGL ES 2.0 & 3.0 support.
    - [x] Skeletal animation (skinning).
    - [x] Normal mapping and basic lighting.
    - [x] Skybox backgrounds.
- [x] **Virtual Reality (VR)**:
    - [x] Anaglyph (red/cyan glasses).
    - [x] Stereoscopic (side-by-side for VR headsets).
- [x] **Interaction & Analysis**:
    - [x] Touch controls for rotation, panning, and zooming.
    - [x] Wireframe and skeleton decorators.
    - [x] Bounding box visualization.
    - [x] Ray casting for object selection.

## Getting Started

### Prerequisites
- Java 17
- Android SDK
- Git

### Build and Run from Source
```bash
# 1. Clone the repository and its submodules (the engine)
git clone --recursive https://github.com/the3deer/android-3D-model-viewer.git
cd android-3D-model-viewer

# 2. Build the debug APK
./gradlew clean assembleDebug

# 3. Install on a connected device or emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk

# 4. Launch the application
adb shell am start -n org.andresoviedo.dddmodel2/org.the3deer.android.viewer.MainActivity
```

## Architecture

The application is built with a clean, modular architecture that separates UI, state, and rendering logic.

### UI Design
The UI follows a modern Android pattern with a Navigation Drawer and is designed around a few key principles:
- **Shared State**: A central `SharedViewModel` synchronizes state across all fragments, ensuring that changes (like loading a new model) are reflected instantly everywhere.
- **Contextual Modals**: Features like model loading, camera selection, and settings are presented as `DialogFragment`s, allowing users to make changes while keeping the 3D scene visible for immediate feedback.
- **Immersive Viewing**: A dedicated mode hides all system and app UI to maximize screen space for the 3D content.

### Data Flow
A typical user interaction follows this reactive pattern:
1.  **User Action**: The user selects a new model from the "Load" dialog.
2.  **State Update**: The `SharedViewModel` updates its `activeModel` LiveData.
3.  **Observation**: The main `HomeFragment` observes the `LiveData` change.
4.  **Injection**: The fragment pushes the new model data into the `SceneRenderer`.
5.  **Render**: The fragment requests a new frame, and the renderer draws the updated scene.

## Community & Resources

### Download
Get the app from your favorite store:

<a href="https://f-droid.org/en/packages/org.andresoviedo.dddmodel2/"><img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png" width="215" alt="Get in on F-Droid"></a>
<a href="https://play.google.com/store/apps/details?id=org.andresoviedo.dddmodel2"><img src="https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png" width="215" alt="Get it on Google Play"></a>

### Demo Video
See an overview of the application's features in action (note: video is from a previous version).
[Watch on YouTube](https://www.youtube.com/watch?v=PV92DKohXXk)

### Screenshots

<img alt="Menu" src="./screenshots/menu.png" width="400"/> <img alt="Screenshot2" src="./screenshots/screenshot2.png" width="200"/> <img alt="Screenshot3" src="./screenshots/screenshot3.png" width="200"/>
<img alt="cowboy.gif" src="./screenshots/cowboy.gif" width="200"/> <img alt="stormtrooper.gif" src="./screenshots/stormtrooper.gif" width="200"/> <img alt="vc.gif" src="./screenshots/vc.gif" width="200"/>

## Credits & Acknowledgements

This project is made possible by the hard work of many individuals and open-source projects.

#### Core Libraries
- **The3Deer**: MIT License
- **STL Parser**: Copyright (c) 2001, 2002 Dipl. Ing. P. Szawlowski (j3d.org)
- **GLTF Parser**: MIT License (javagl/JglTF)
- **FBX Parser**: MIT License (ufbx)
- **EarCut**: ISC License (Mapbox)

#### Assets
- **cowboy**: Unlicense License - TheThinMatrix
- **fox**: CC0 by PixelMannen, Rigging/Animation CC-BY 4.0 by @tomkranis
- **stormtrooper**: MIT License - hujiulong

Special thanks to the **AI (Gemini)** for assistance in refactoring and documenting large parts of the codebase.

## Disclaimer
*   This is a work in progress. Some features may be incomplete.
*   While many models work, some complex files may not load correctly.
*   If you encounter an issue, please open an issue and attach the model file if possible.
