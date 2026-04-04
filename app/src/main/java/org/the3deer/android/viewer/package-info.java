/**
 * <h1>Android 3D Model Viewer</h1>
 * <p>
 * A powerful, open-source Android application for viewing and inspecting 3D models.
 * Built with a modular architecture and powered by a custom OpenGL ES 2.0/3.0 engine.
 * </p>
 *
 * <h2>Key Features</h2>
 * <ul>
 *     <li><b>Multi-Format Support</b>: Load OBJ, STL, DAE (Collada), GLTF, and FBX files.</li>
 *     <li><b>Advanced Rendering</b>: Support for skeletal animation, normal mapping, and basic lighting.</li>
 *     <li><b>Modern UI</b>: Built with Android Fragments, Navigation Component, and Material Design.</li>
 *     <li><b>VR Ready</b>: Integrated Anaglyph and Stereoscopic (VR headset) rendering modes.</li>
 *     <li><b>Interactive Tools</b>: Wireframe toggles, skeleton visualization, bounding boxes, and ray-cast selection.</li>
 * </ul>
 *
 * <h2>Project Structure</h2>
 * <p>The application is divided into two main components:</p>
 * <ul>
 *     <li><b>App (this package)</b>: The Android application layer, managing UI, state, and user interaction.</li>
 *     <li><b>Engine</b>: A standalone 3D engine submodule (see <code>org.the3deer.engine</code>).</li>
 * </ul>
 *
 * @see <a href="https://github.com/the3deer/android-3D-model-viewer">GitHub Repository</a>
 */
package org.the3deer.android.viewer;