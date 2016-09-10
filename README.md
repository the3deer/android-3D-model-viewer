Android 3DModel Viewer
======================

This is a demo of OpenGL ES 2.0. 

It is basically an android application with a 3D renderer (OpenGL ES 2.0) that can load 3D models.

The purpose of this application is to learn and share how to draw using OpenGL language


News (06/04/2016)
=================

* Implemented selection of objects (click on Demo and touch the different objects)
* Light rendering implemented
* Toggle textures & lights
* Released app on android market :) https://play.google.com/store/apps/details?id=org.andresoviedo.dddmodel


About
=====

Load 3D models and see how to do it with this open source code application.

The main purpose of this app is to show how to draw in android using the OpenGL 2.0 by sharing the source code. 
So please, don't expect this application to be much richer or nicer than the ones already published in the app store, 
but at least it's unique in the sense it's opened to anyone who wants to contribute or don't want to start a similar 
project from scratch.

As this is my first android app and Im still learning the OpenGL 2.0 language, it is highly probable that there are bugs; 
but I will try to continue improving the app and adding more features. So please send me your comments, suggestions or 
complains to andresoviedo@gmail.com.

The app comes with some included 3D models that were taken for free from Internet (http://www.turbosquid.com).


Whats next
==========

* Choose different texture
* Many more... 


Future
======

* Integrate app with assimp library to load more file formats (http://www.assimp.org/) 


Features
========

  - OpenGL ES 2.0 API
  - obj format supported (wavefront)
  - textures
  - colors
  - lighting
  - display of normals
  - display of bounding box 
  - scaling, rotation
  - object selection
  - touch support!
    - tap to select object 
    - drag to move camera
    - rotate to rotate camera
    - pinch & spread to zoom in/out the camera
  - moving of objects (not yet!)
  - primitive collision detection (not yet!) 
  - animation of sprites (not yet!)


Try it
======

  1. Install app from google play https://play.google.com/store/apps/details?id=org.andresoviedo.dddmodel
  1. Or download the apk to your device and install it https://github.com/andresoviedo/android-3DModel/raw/master/android-3DModel.apk
  1. Or clone source, compile project from your android IDE, connect your android device and run application 
  2. Open the application. You should see a menu. From there you can load some demos or load your own model
  3. Once the scenario is loaded, pinch and rotate to see the 3D scene from another perspective. 


Screenshot
==========

![Screenshot1](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot1.png)
![Screenshot2](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot2.png)
![Screenshot3](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot3.png)
![Screenshot4](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot4.png)
![Screenshot5](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot5.png)
![Screenshot6](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot6.png)
![Screenshot7](https://raw.github.com/andresoviedo/android-3D-model-viewer/master/screenshots/screenshot7.png)


Tests
=====

  - Tested on Nexus 7 3G 2012 (android 4.4.2)
  - Tested on BQ Aquaris 5 (android 4.2.1)
  - Tested on Samsung GT-S5280 (android 4.1.2)


Final Notes
===========

You are free to use this program while you keep this file and the authoring comments in the code.
Any comments and suggestions are welcome.


ChangeLog
=========

(f) fixed, (i) improved, (n) new feature

- 1.2.0 (06/04/2016)
 - (n) Implemented selection of objects

- 1.1.0 (30/03/2016)
 - (n) Implemented lighting & toggle textures & lights
 - (i) Refactoring of 3DObjectImpl

- 1.0.0 (27/03/2016)
 - (n) First release in Google Play Android Market
