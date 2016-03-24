Android 3DModel Viewer
======================

This is a demo of OpenGL ES 2.0. 

It is basically an android application with a 3D renderer (OpenGL ES 2.0) that can load 3D models.

The purpose of this application is to learn how to code in OpenGL language


News (24/03/2016)
=================

* Fixed shader to show correctly colors
* Implemented loader so now we can show any obj having quads :)
* Implemented showing the face normals


Whats next
==========

* Implement a menu so the user can choose the model (.obj) and the texture file
* Implement a menu so the user can toogle lights, texture & draw mode (wireframe or solid)


Features
========
  - OpenGL ES 2.0 API
  - obj format supported (wavefront)
  - display of normals (not yet!)
  - display of bounding box 
  - textures
  - colors
  - scaling, rotation
  - object picking
  - primitive collision detection (not yet!) 
  - animation of sprites (not yet!)
  - touch support!
    * rotation gesture to rotate
    * pinch gesture to zoom
  - moving of objects (not yet!)


Try it
======

  1. Compile project from your android IDE
  2. Connect your android device and run application 
  3. You should see a menu. From there you can load the demo scenario (3D arrow and some object in the middle).
  4. If you want, you can load your own model
  4. Once the scenario is loaded, pinch and rotate to see the 3D scene from another perspective. 


Screenshot
==========

![Screenshot1](https://github.com/andresoviedo/android-3DModel/blob/master/screenshots/screenshot1.png)
![Screenshot2](https://github.com/andresoviedo/android-3DModel/blob/master/screenshots/screenshot2.png)
![Screenshot3](https://github.com/andresoviedo/android-3DModel/blob/master/screenshots/screenshot3.png)
![Screenshot4](https://github.com/andresoviedo/android-3DModel/blob/master/screenshots/screenshot4.png)


Tests
=====

  - Tested on Nexus 7 3G (2012)


Final Notes
===========

You are free to use this program while you keep this file and the authoring comments in the code.
Any comments and suggestions are welcome.