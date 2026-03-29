# Android Model Viewer Application Documentation

This document explains how the Viewer application is built on top of the 3D Engine.

## Application Architecture

The app follows an MVVM-inspired architecture using Android Jetpack components.

- **MainActivity**: The single entry point, hosting the Navigation Drawer and Fragment container.
- **SharedViewModel**: Scoped to the Activity, it holds the "Single Source of Truth" for the app state:
    - Currently loaded `Model`.
    - Active UI state (Immersive mode, colors).
    - Camera and Animation selections.
- **Fragments**: Each screen (Home, Load, Settings) observes the `SharedViewModel`.

## Data Flow & Integration

The app integrates with the Engine's `SceneRenderer` via a reactive flow:
1. **Selection**: User picks a model in the `LoadContentDialog`.
2. **State**: The `SharedViewModel` updates the `activeModel` LiveData.
3. **Observation**: `HomeFragment` observes the change and calls `renderer.updateModel(newModel)`.
4. **Drawing**: The Fragment triggers `glSurfaceView.requestRender()`, and the Engine draws the new state.

## User Interface

### Home (The 3D Canvas)
The `HomeFragment` displays the OpenGL content. To allow standard Android UI to appear over the 3D scene:
- `glSurfaceView.setZOrderMediaOverlay(true)` is used to place the surface correctly in the window hierarchy.
- UI elements (like the selection buttons) are placed in a layout with higher elevation.

### Modal Navigation
- **Load Dialog**: A `DialogFragment` that allows browsing and switching models without leaving the 3D context.
- **Settings Dialog**: Hosted via `PreferenceFragmentCompat`, allowing real-time adjustments to rendering properties (like skybox or language).

## Preferences & Settings System

The app leverages the Engine's `BeanFactory` to automatically generate the Settings UI.

### Annotation Mapping
The UI is dynamically built by scanning for `@Bean` and `@BeanProperty` annotations:
- **Keys**: Constructed as `<className>.<propertyName>`.
- **Master Toggles**: If a property is named `enabled`, it acts as a toggle for all other properties in that bean.
- **Dynamic Values**: Lists of options (like Colors or Renderers) are fetched via delegated methods in the beans.

### Persistence
Settings are stored in `SharedPreferences`. The `SharedViewModel` initializes by reading these values and injecting them into the Engine's configuration beans on startup.
