Android 3D Model Viewer
=======================

### App Module (`:app`)
- **`SharedViewModel`**: Centralizes the app state. It holds the current `Model` being viewed and its properties (like color).
- **UI Fragments (`HomeFragment`, `SlideshowFragment`)**:
    - Act as the "Controller" in the MVC/MVVM sense.
    - They observe `LiveData` from the `SharedViewModel`.
    - When data changes, they "push" the new state into the `SceneRenderer`.

## Data Flow --> to review
1. **User Action**: User changes a setting (e.g., selects "Cube").
2. **State Update**: `SharedViewModel` updates its `activeModel` LiveData.
3. **Observation**: The Fragment's observer triggers.
4. **Injection**: The Fragment calls `renderer.updateModel(newModel)`.
5. **Request Render**: The Fragment calls `glSurfaceView.requestRender()`.
6. **Traversal**: `SceneRenderer` traverses the Scene Graph, calculates world matrices, and draws the meshes.


# User Interface

[UI Design](UI_DESIGN.md)

# Android Customisation

[UI Customisation](UI_CUSTO.md)