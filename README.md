Android 3D Model Viewer
=======================

This is a demo of OpenGL ES 2.0.

It is basically an android application with a 3D renderer (OpenGL ES 2.0) that can load 3D models.

The purpose of this application is to learn and share how to draw using OpenGL language


News (27/02/2017)
=================

* Fixed loading of external resources
* Project moved to gradle


Android Market
==============

https://play.google.com/store/apps/details?id=org.andresoviedo.dddmodel


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

  1. Play Store:  https://play.google.com/store/apps/details?id=org.andresoviedo.dddmodel
  1. APK: [app-debug.apk](app/build/outputs/apk/app-debug.apk)
  1. Gradle: clone the repository, compile with gradle and install with adb
  2. Open the application. You should see a menu. From there you can load some demos or load your own model
  3. Once the scenario is loaded, pinch and rotate to see the 3D scene from another perspective.


Screenshot
==========

![Screenshot1](screenshots/screenshot1.png)
![Screenshot2](screenshots/screenshot2.png)
![Screenshot3](screenshots/screenshot3.png)
![Screenshot4](screenshots/screenshot4.png)
![Screenshot5](screenshots/screenshot5.png)
![Screenshot6](screenshots/screenshot6.png)
![Screenshot7](screenshots/screenshot7.png)


Tests
=====

  - Tested on Alcatel Flash Plus 2 (android 6)
  - Tested on Nexus 7 3G 2012 (android 4.4.2)
  - Tested on BQ Aquaris 5 (android 4.2.1)
  - Tested on Samsung GT-S5280 (android 4.1.2)


Build
=====

    export ANDROID_HOME=/home/$USER/Android/Sdk
    ./gradlew assembleDebug
    adb install -r app/build/outputs/apk/app-debug.apk
    adb shell am start -n org.andresoviedo.dddmodel2/org.andresoviedo.app.model3D.MainActivity


Android Market
==============

Sign the app:

    keytool -genkey -v -keystore android-3D-model-viewer.jks \
    -keyalg RSA -keysize 2048 -validity 10000 -alias android-3D-model-viewer


Final Notes
===========

You are free to use this program while you keep this file and the authoring comments in the code.
Any comments and suggestions are welcome.


ChangeLog
=========

(f) fixed, (i) improved, (n) new feature

- 2.0.0 (27/02/2017)
 - (f) Fixed loading external files issue #6
 - (i) Project moved to gradle

- 1.2.0 (06/04/2016)
 - (n) Implemented selection of objects

- 1.1.0 (30/03/2016)
 - (n) Implemented lighting & toggle textures & lights
 - (i) Refactoring of 3DObjectImpl

- 1.0.0 (27/03/2016)
 - (n) First release in Google Play Android Market
