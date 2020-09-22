package org.andresoviedo.util.android;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import org.andresoviedo.android_3d_model_engine.model.CubeMap;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_NEGATIVE_Z;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_X;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Y;
import static android.opengl.GLES20.GL_TEXTURE_CUBE_MAP_POSITIVE_Z;

public final class GLUtil {

    private static final String TAG = "GLUtil";

    private GLUtil() {

    }

    /**
     * Helper function to compile and link a program.
     *
     * @param vertexShaderHandle   An OpenGL handle to an already-compiled vertex shader.
     * @param fragmentShaderHandle An OpenGL handle to an already-compiled fragment shader.
     * @param attributes           Attributes that need to be bound to the program.
     * @return An OpenGL handle to the program.
     */
    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle,
                                           final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();

        if (programHandle != 0) {
            // Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);

            // Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);

            // Bind attributes
            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }

            // Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);

            // Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);

            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                programHandle = 0;
            }
        }

        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }

        return programHandle;
    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p>
     * <strong>Note:</strong> When developing shaders, use the checkGlError() method to debug shader coding errors.
     * </p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        int[] compiled = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
        Log.d("GLUtil", "Shader compilation info: " + GLES20.glGetShaderInfoLog(shader));
        if (compiled[0] == 0) {
            Log.e("GLUtil", "Shader error: " + GLES20.glGetShaderInfoLog(shader) + "\n" + shaderCode);
            GLES20.glDeleteShader(shader);
        }

        return shader;
    }

    public static int loadTexture(final byte[] textureData) {
        ByteArrayInputStream textureIs = new ByteArrayInputStream(textureData);
        return loadTexture(textureIs);
    }

    public static int loadTexture(final InputStream is) {
        Log.v("GLUtil", "Loading texture from stream...");

        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);
        GLUtil.checkGlError("glGenTextures");
        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        Log.v("GLUtil", "Handler: " + textureHandle[0]);

        final Bitmap bitmap = loadBitmap(is);

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
        GLUtil.checkGlError("glBindTexture");
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
        GLUtil.checkGlError("texImage2D");
        bitmap.recycle();
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

        Log.v("GLUtil", "Loaded texture ok");
        return textureHandle[0];
    }

    private static Bitmap loadBitmap(byte[] is) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and which
        // resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be sure,
        // we set inScaled to false.
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeByteArray(is, 0, is.length, options);
        if (bitmap == null) {
            throw new RuntimeException("couldn't load bitmap");
        }
        return bitmap;
    }

    private static Bitmap loadBitmap(InputStream is) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        // By default, Android applies pre-scaling to bitmaps depending on the resolution of your device and which
        // resource folder you placed the image in. We don’t want Android to scale our bitmap at all, so to be sure,
        // we set inScaled to false.
        options.inScaled = false;

        // Read in the resource
        final Bitmap bitmap = BitmapFactory.decodeStream(is, null, options);
        if (bitmap == null) {
            throw new RuntimeException("couldn't load bitmap");
        }
        return bitmap;
    }

    public static int loadCubeMap(final CubeMap cubeMapTexture) {

        final Bitmap bitmapPosX = loadBitmap(cubeMapTexture.getPoxx());
        final Bitmap bitmapNegX = loadBitmap(cubeMapTexture.getNegx());
        final Bitmap bitmapPosY = loadBitmap(cubeMapTexture.getPoxy());
        final Bitmap bitmapNegY = loadBitmap(cubeMapTexture.getNegy());
        final Bitmap bitmapPosZ = loadBitmap(cubeMapTexture.getPoxz());
        final Bitmap bitmapNegZ = loadBitmap(cubeMapTexture.getNegz());

        final int[] texturesIds = new int[1];
        GLES20.glGenTextures(1, texturesIds, 0);
        GLUtil.checkGlError("glGenTextures");

        if (texturesIds[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        final int textureId = texturesIds[0];

        // Bind to the texture in OpenGL
        GLES20.glBindTexture(GLES20.GL_TEXTURE_CUBE_MAP, textureId);
        GLUtil.checkGlError("glBindTexture");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_X, 0, bitmapPosX, 0);
        GLUtil.checkGlError("texImage2D");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_X, 0, bitmapNegX, 0);
        GLUtil.checkGlError("texImage2D");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Y, 0, bitmapPosY, 0);
        GLUtil.checkGlError("texImage2D");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Y, 0, bitmapNegY, 0);
        GLUtil.checkGlError("texImage2D");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_POSITIVE_Z, 0, bitmapPosZ, 0);
        GLUtil.checkGlError("texImage2D");
        GLUtils.texImage2D(GL_TEXTURE_CUBE_MAP_NEGATIVE_Z, 0, bitmapNegZ, 0);
        GLUtil.checkGlError("texImage2D");
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_CUBE_MAP, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        // free bitmap
        bitmapPosX.recycle();
        bitmapNegX.recycle();
        bitmapPosY.recycle();
        bitmapNegY.recycle();
        bitmapPosZ.recycle();
        bitmapNegZ.recycle();

        // update model
        cubeMapTexture.setTextureId(textureId);

        return textureId;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, &quot;vColor&quot;);
     * MyGLRenderer.checkGlError(&quot;glGetUniformLocation&quot;);
     * </pre>
     * <p>
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static boolean checkGlError(String glOperation) {
        int glError;
        boolean error = false;
        while ((glError = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + glError);
            error = true;
            Log.e(TAG, Thread.currentThread().getStackTrace()[3].toString());
            Log.e(TAG, Thread.currentThread().getStackTrace()[4].toString());
            Log.e(TAG, Thread.currentThread().getStackTrace()[5].toString());
            Log.e(TAG, Thread.currentThread().getStackTrace()[6].toString());

            // throw new RuntimeException(glOperation + ": glError " + error);
        }
        return error;
    }
}
