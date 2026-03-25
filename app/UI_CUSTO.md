# UI Customisation

This document tracks the changes made to the user interface of the Android Model Viewer application compared to the default project template.

## Navigation & Branding

- **Home Fragment**: The `TransformFragment` has been promoted to be the application's **Home** fragment.
- **Labeling**: Updated `mobile_navigation.xml` and `strings.xml` to rename the primary destination from "Transform" to "Home".
- **Default Start**: The application now starts directly on the Home fragment.
- **Top-Level Configuration**: Updated `AppBarConfiguration` in `MainActivity` to treat both **Home** and **Slideshow** as top-level destinations, ensuring the drawer icon (burger menu) is visible on both.

## Modal Dialogs & Navigation Logic

- **DialogFragment Refactor**: 
    - **Load Fragment**: Converted to a `DialogFragment` to allow model switching without leaving the current context.
    - **Settings Fragment**: Hosted within a new `SettingsDialogFragment` to provide a consistent modal experience for application preferences.
- **Navigation Fix**: 
    - Customised the navigation listener in `MainActivity` to prevent the "pop to Home" behaviour when opening **Load** or **Settings** from the drawer/bottom navigation.
    - This allows users to open these tools while remaining on the **Slideshow** fragment.
    - Implemented a `DestinationChangedListener` to maintain correct menu selection highlights when dialogs are dismissed.

## Immersive Mode

- **FAB Toggle**: Refactored the floating action button (FAB) in `MainActivity` to toggle between normal and immersive modes.
- **Visual Changes**:
    - Hides system bars (status and navigation), the Action Bar (Toolbar), and the Bottom Navigation View.
    - FAB icon toggles between `ic_menu_add` (to enter) and `ic_menu_revert` (to exit).
- **Persistence & Sync**: 
    - Added logic to `MainActivity` to check `SharedPreferences` on startup and automatically apply immersive mode if the "Immersive View" setting is enabled.
    - **Bi-directional Sync**: When the FAB is clicked to toggle the mode, it immediately updates the `immersive_mode` value in `SharedPreferences`.
- **Implementation**: Uses `WindowInsetsControllerCompat` for modern Android compatibility.

## OpenGL Integration

- **GLSurfaceView**: Integrated a `GLSurfaceView` into multiple fragments (`SlideshowFragment`, `HomeFragment`).
- **Custom Renderer**: Implemented a `ModelRenderer` using OpenGL ES 2.0 to draw 2D/3D shapes.
- **Layering**: Verified that the existing `ConstraintLayout` correctly supports the OpenGL surface as a background layer, even behind interactive components like `RecyclerView`.
- **Lifecycle Management**: Ensuring the OpenGL thread is properly paused and resumed with fragment lifecycle events (`onPause`, `onResume`).

## Shared Data Architecture

- **SharedViewModel**: Implemented a shared `AndroidViewModel` scoped to the Activity.
- **Data State**:
    - **Model Geometry**: Holds the active model's coordinate data (`FloatArray`). Supports switching between **Triangle** and **Cube** vertices.
    - **Style State**: Holds the current model color (`FloatArray`).
- **Persistence**: On initialization, the ViewModel reads the saved "Default Model Color" from `SharedPreferences`.
- **Reactive UI**: All rendering fragments observe the shared `modelCoords` and `modelColor`, ensuring instantaneous updates across the app when data changes.

## Load Feature

- **Model Selection**: Refactored the "Reflow" placeholder into a **Load** fragment.
- **Dynamic Loading**: Provides UI buttons to switch the active model in the `SharedViewModel` between a basic Triangle and a 3D Cube.
- **Contextual Awareness**: As a `DialogFragment`, it allows loading new geometry while previewing it immediately in the background.

## Settings & Preferences

- **Refactored SettingsFragment**: Converted the default fragment to a `PreferenceFragmentCompat`.
- **Preference Definition (`preferences.xml`)**:
    - **Immersive View**: A `SwitchPreferenceCompat` that persists the user's fullscreen preference.
    - **Default Model Color**: A `ListPreference` allowing users to choose between Red, Green, and Blue.
- **Live Updates**:
    - `SettingsFragment` implements `OnSharedPreferenceChangeListener` to trigger immediate re-renders when preferences change.
