# main goal

stabilization, performance, robustness, resiliency and bug fixing. is that too much?

# current issues

- the 3d viewer app does not allow to open the top system bar
- pixel 8: the nav drawer, at least in the simulator, and with the mouse, I don't know how to open it.
- loading may crash with an out of memory error
- remove legacy parsers (collada and gltf)
- the skybox size is static - the size should be proportional maybe

# current warnings

- I didn't do any profiling yet. This is to be done. I don't want memory leaks nor cpu hotspots.
- The 3D UI FontFactory is not finished. Not all the characters are implemented. This is a tedious job, because it should match the font used.
- The camera handler may be improved. Maybe it is more intuitive to rotate the model, rather than orbiting around it.  Not sure about this.


# edit support ?

- allow texture selection :  add a menu in the nav drawer "Textures"
  the Menu should list all the textures for the specified model,

# nice to have

- finish the support for GLTF, for example Morphing. Or recursive skeletons? 
- support animation for fbx. current implementation only processes vertices
- collision controller: i would like to instead of drawing 1 point, i would like to paint the triangle hit by the ray
- camera background: i would like to allow the user to select the "Camera" as a background
- eye tracking: i would like to add eye tracking feature
- camera with AR: i would like to add AR feature.
- "Exploration". This would mean that I can navigate like a person inside the 3d world. Obviously this would require a collision detection system. The user would "walk" using the finger (forward, backward, turn left, turn right).


