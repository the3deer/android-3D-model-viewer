ChangeLog
=========

(f) fixed, (i) improved, (n) new feature

**v5.0.0**  (10/04/2026) 
- **Android SDK 35** upgrade  
- **Engine** improvements
  - OpenGL shaders upgraded to **OpenGL 3**. OpenGL 2 shaders is still supported (for older phones)
  - Engine Multiple **Scene**, **Camera** and **Animation**
- **Internationalization (i18n)**
  - Support for **Spanish** language. **English** being the default application language
- **Brand New User Interface (UI)**
  - UI completely rewritten / re-designed from scratch (assisted by AI)
  - UI is now based on **Android Fragments**
  - UI has now 1 **Android Navigation** Drawer (and 1 Android Toolbar)
  - UI has now 3 **Dialogs** to allow the selection of the Scene, Camera & Animation
  - UI has now 1 **Preference** screen to configure some of the Engine properties (skybox, language, etc)
- DAE & GLTF parsers completely rewritten / re-designed from scratch (assisted by AI)
- FBX: **basic** support added. The FBX parser is possible thanks to ufbx (https://github.com/ufbx/ufbx)
  - FBX Features: **static** models only with diffuse textures (no animation / rigging)

**v4.1.1** (31/12/2024)
  - (i) Android SDK and dependencies updated (API 35)  
  - (f) Fixed Android Content Manager Loader
  - (f) Fixed shader issue

**v4.1.0** (22/10/2024)
  - (n) Gltf Animations
  - (n) Android Preferences for Settings
  - (n) Loader: Integrated Khronos repository
  - (i) Android view using Fragments
  - (i) Android SDK and dependencies updated (API 34)

**v4.0.0** (04/09/2022)
  - (i) rebranding to org.the3deer
  - (i) master branch renamed to main