ChangeLog
=========

(f) fixed, (i) improved, (n) new feature

- 4.1.1 (31/12/2024)
    - (i) Android SDK and dependencies updated (API 35)  
    - (f) Fixed Android Content Manager Loader
    - (f) Fixed shader issue
- 4.1.0 (22/10/2024)
    - (n) Gltf Animations
    - (n) Android Preferences for Settings
    - (n) Loader: Integrated Khronos repository
    - (i) Android view using Fragments
    - (i) Android SDK and dependencies updated (API 34)
- 4.0.0 (04/09/2022)
    - (i) rebranding to org.the3deer
    - (i) master branch renamed to main
- 3.5.0 (04/09/2022)
    - (n) merged shadow + geometry demo into main branch
- 3.4.1 (23/09/2022)
    - (f) bug fixing: lighting, textures, etc #176
    - (i) texture loading refactoring #61
    - (i) shader refactoring & deduplication #61
- 3.4.0 (17/09/2022)
    - (n) GLTF basic support #176
- 3.3.1 (12/09/2022)
    - (f) fixed texture issue + color issue + blending issue. fixed #214
    - (f) fixed texture issue #204
    - (f) fixed STL binary fallback issue #208
- 3.3.0 (23/06/2022)
    - (n) interactive object orientation
    - (n) isometric, orthographic and free camera view
    - (n) New gui axis + gui info
    - (f) fixed FPS counter
    - (i) some user options are being saved (camera settings)
- 3.2.0 (02/02/2022)
    - (i) repository explorer improved - multiple index files
    - (f) smoothing fixed
    - (f) fixed renderer memory leak
- 3.1.1 (28/10/2021)
    - (f) google play required library upgrades
    - (f) spanish menu fixed
- 3.1.0 (10/10/2020)
    - (n) skybox
    - (f) deleted unlicensed assets
- 3.0.4 (05/10/2020)
    - (f) support for multiple skin controllers
    - (f) skeleton fixed to use invert of inverse bind matrix
- 3.0.3 (05/09/2020)
    - (f) smooth faces are now toggle featured - not all models should be smoothed
    - (f) fixed normal calculation - using high precision numbers
- 3.0.2 (03/09/2020)
    - (f) setUniform4fv function had length zero - issue detected with Xiaomi Redmi 8
    - (f) gl_MaxVertexUniformVectors is apparently not working - "too many uniforms" - detected in Xiaomi Redmi 8
- 3.0.1 (15/07/2020)
    - (f) Forgot to add requestLegacyExternalStorage option - Android 10 requirement
- 3.0.0 (15/07/2020)
    - (n) Support for Object Groups  (wavefront: o,g, dae: <geometry>)
    - (n) Support for Smoothing Groups (wavefront)
    - (n) migrated project to androidx compat libraries
    - (n) GUI - fps counter - experimental framework
    - (n) Collada support for polygon with holes (<ph>)
    - (i) Complete re-engineering and refactoring of the code
    - (i) Wavefront + Collada Loader reimplemented
    - (f) Fixed overall bugs
- 2.7.0 (13/11/2019)
    - (n) new blending force mode to 50%
    - (f) fixed light rendering issues on renderers #125 (diffuse + specular)
    - (f) fixed bugs when DAE had multiple geometries #125
    - (f) fixed textures not being linked issue
    - (f) fixed performance issues: now rendering below 5% cpu & no ram allocation
- 2.6.0 (20/10/2019)
    - (n) #81 Support for collada files with multiple geometries
    - (f) #94 fixed setVisible(boolean)
    - (f) #92 fixed multiple color rendering for non-triangulated file.obj
    - (i) overall engine improved
- 2.5.1 (20/05/2019)
    - (f) wavefront loader fixed for meshObject point to negative indices
- 2.5.0 (19/05/2019)
    - (n) new blending toggle
    - (n) new color toggle
    - (i) engine refactoring: externalized renderers
    - (i) engine improved: fixed bugs and removed classes
- 2.4.0 (16/05/2019)
    - (n) stereoscopic rendering: anaglyph + cardboard
- 2.3.0 (27/09/2018)
    - (n) Externalized 3d engine into android library module
    - (n) Wiki initial documentation
- 2.2.0 (11/09/2018)
    - (n) Load models from app repository
    - (i) Reduced app size to only 1 Megabyte
- 2.1.0 (07/09/2018)
    - (n) Skeleton Animation
    - (n) File chooser to load files from any where
    - (f) Collada Animator fixed (INV_BIND_MATRIX, bind_shape_matrix)
    - (f) Collada Animator Performance improved
    - (f) Application refactoring (ContentUtils, Loaders, etc)
    - (f) Several bugs fixed
- 2.0.4 (22/12/2017)
    - (n) Implemented face collision detection algorithm: ray-triangle + octree
- 2.0.3 (21/12/2017)
    - (i) Improved collision detection algorithm (ray-aabb) for selecting objects
    - (i) BoundingBox code cleanup
- 2.0.2 (17/12/2017)
    - (f) Collada XML parser is now android's XmlPullParser
    - (f) Animation engine frame times improved
    - (n) Camera now moves smoothly
- 2.0.1 (08/12/2017)
    - (f) Multiple Collada parser fixes
    - (f) Camera now can look inside objects
- 2.0.0 (24/11/2017)
    - (n) Support for collada files with skeletal animations :)
- 1.4.1 (21/11/2017)
    - (f) #29: Crash loading obj with only vertex info
- 1.4.0 (19/11/2017)
    - (f) #28: Load texture available for any model having texture coordinates
- 1.3.1 (23/04/2017)
    - (f) #18: Removed asReadOnlyBuffer() because it is causing IndexOutOfBounds on Android 7
- 1.3.0 (17/04/2017)
    - (n) #17: Added support for STL files
    - (n) #17: Asynchronous building of model so the build rendering is previewed
    - (f) #17: Added Toasts to buttons to show current state
- 1.2.10 (16/04/2017)
    - (f) #16: Immersive mode is now configurable in the ModelActivity Intent: b.putString("immersiveMode", "false");
    - (f) #16: Background color configurable in the ModelActivity Intent: b.putString("backgroundColor", "0 0 0 1");
    - (f) #16: Fixed vertex normals generation (vertices were missing)
    - (f) #16: Scaling is now implemented in the ModelView Matrix with Object3DData.setScale(float[])
    - (f) #16: Wireframe generation is now using the source data
    - (n) #16: Implemented Point Drawing, like wireframe mode but only the points are drawn
    - (f) #16: Removed trailing slash from parameter "assetDir"
    - (f) #16: Access to ByteBuffers made absolute so there are thread safe (future fixes need this)
- 1.2.9 (11/04/2017)
    - (f) #15: Toggle rotating light
    - (f) #15: Wireframe with textures and colors
- 1.2.8 (10/04/2017)
    - (f) Fixed #14: Camera movement improved. Only 1 rotation vector is used + space bounds set
- 1.2.8 (04/04/2017)
    - (f) Fixed #13: parsing of vertices with multiple spaces
    - (i) Improved error handling on loading task
    - (i) Vertices are defaulted to (0,0,0) if parsing fails
- 1.2.7 (03/04/2017)
    - (i) Removed commons-lang3 dependency
- 1.2.6 (02/04/2017)
    - (f) Fixed #12. Drawing the wireframe using GL_LINES and the index buffer (drawElements)
- 1.2.5 (01/04/2017)
    - (f) Fixed #10. Map meshObject to texture only when using the only loaded texture
    - (f) Fixed #11. Generation of missing vertex normals
- 1.2.4 (30/03/2017)
    - (f) Fixed #5. Memory performance optimization
- 1.2.3 (27/03/2017)
    - (f) Fixed #1. Cpu performance optimization
- 1.2.2 (25/03/2017)
    - (f) Fixed #9. IOOBE loading face normals when meshObject had no texture or normals
- 1.2.1 (27/02/2017)
    - (f) Fixed loading external files issue #6
    - (i) Project moved to gradle
- 1.2.0 (06/04/2016)
    - (n) Implemented selection of objects
- 1.1.0 (30/03/2016)
    - (n) Implemented lighting & toggle textures & lights
    - (i) Refactoring of 3DObjectImpl
- 1.0.0 (27/03/2016)
    - (n) First release in Google Play Android Market
