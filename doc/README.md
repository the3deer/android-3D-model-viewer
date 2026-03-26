# Android 3D Model Viewer - Application Design

## Overview
A modern 3D model viewer for Android built with a modular architecture, separating the User Interface from a hierarchical Rendering Engine.

## Architecture

### 1. Module Structure
- **`:app` (Android Application)**: Handles UI, navigation, user preferences, and application state via Jetpack components.
- **`:engine` (Android Library)**: A standalone 3D rendering engine. It implements a Scene Graph architecture, allowing for complex object hierarchies and transformations.

### 2. Design Principles
- **Scene Graph**: Instead of drawing flat lists of vertices, the engine manages a tree of `Node` objects. This allows for parent-child transformations (moving a group of objects together).
- **Separation of Concerns**: The `:engine` is purely data-driven. It doesn't know about ViewModels or SharedPreferences. It simply renders the `Model` data it is given.
- **MVP (Model-View-Projection)**: The engine uses standard 3D math matrices to handle object positions, camera views, and screen projections.

## 3.  Application / Engine Integration

- **`SharedViewModel`**: Centralizes the app state. It holds a list of the `Model`s being viewed and it's corresponding `ModelEngine`s (aka environment). 
- **`Model`**: The root container for a 3D asset. Holds multiple `Scene`s.
- **`Scene`**: A specific 3D environment containing a `Node` hierarchy and `Camera`s.
- **`ModelEngine`**: The environment holding a list of engine `Component`s
- **`Component`**: super interface to implement different engine Features, like `CameraController` or the `GUI`
- GLSurfaceView: OpenGL surface holding an `GLRendererImpl`
- GLRenderer: OpenGL Renderer. Delegate the rendering to the active `Renderer`
- Renderer: interface with 3 implementations (Default, anaglyph an stereoscopic)
- `Drawer`: specialization of a `Component` with many implementations, like `SceneDrawer` or `SkyBoxDrawer`






