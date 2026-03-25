# User Interface Design

## Structure

The application follows a standard Android Navigation Drawer and Bottom Navigation pattern, with the following core destinations:

1.  **Home (Welcome)**: The primary landing page. It displays a responsive list of content overlaid on a live 3D background. 
2.  **Load (Model Management)**: A core utility implemented as a **DialogFragment**. It allows the user to switch the active 3D model (e.g., between a Triangle and a Cube) without losing the current viewing context.
3.  **Slideshow (Showcase)**: A secondary viewing mode that provides a distraction-free environment for inspecting models.
4.  **Settings (Preferences)**: Implemented as a **DialogFragment**. It hosts application-wide settings like Immersive Mode and default model colors.

## Design Principles

- **Contextual Modals**: Tools like "Load" and "Settings" are designed as dialogs rather than full-screen fragments. This keeps the 3D model visible and provides immediate visual feedback for user actions.
- **Immersive Viewing**: The application provides an "Immersive Mode" that hides all system and app UI, maximising the screen real estate for 3D rendering.
- **Shared State**: All fragments are synchronised through a `SharedViewModel`. This ensures that changing a model in the "Load" dialog or a color in "Settings" updates the UI globally and instantly.
- **Visual Layering**: OpenGL surfaces (`GLSurfaceView`) are used as a background layer in fragments, demonstrating how 3D content can be integrated behind standard Android UI components like `RecyclerView` and `TextView`.
