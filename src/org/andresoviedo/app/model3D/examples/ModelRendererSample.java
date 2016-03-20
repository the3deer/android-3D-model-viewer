package org.andresoviedo.app.model3D.examples;
//package org.andresoviedo.app.model3D.model;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.opengl.GLSurfaceView;
//import android.opengl.GLSurfaceView.Renderer;
//import android.opengl.GLU;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnTouchListener;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.FloatBuffer;
//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.List;
//import javax.microedition.khronos.egl.EGLConfig;
//import javax.microedition.khronos.opengles.GL10;
//
//public class GLEnvironment extends GLSurfaceView implements
//		GLSurfaceView.Renderer, View.OnTouchListener {
//	public static final int HORIZONTAL_ROTATION = 0;
//	public static final int HORIZONTAL_X_AXIS = 2131296341;
//	public static final int HORIZONTAL_Y_AXIS = 2131296342;
//	public static final int HORIZONTAL_Z_AXIS = 2131296343;
//	public static final int VERTICAL_ROTATION = 1;
//	public static final int VERTICAL_X_AXIS = 2131296348;
//	public static final int VERTICAL_Y_AXIS = 2131296349;
//	public static final int VERTICAL_Z_AXIS = 2131296350;
//	private final float LIGHT_MOVEMENT_SCALE = 10.0F;
//	private final float TOUCH_SCALE = 0.2F;
//	private List<LoadingUpdateEventListener> _listeners = new ArrayList();
//	private float[] center;
//	private float dx;
//	private float dy;
//	private SharedPreferences.Editor editor;
//	private float extrema;
//	private float[] lightAmbient = new float[4];
//	private FloatBuffer lightAmbientBuffer;
//	private float lightColorBlue;
//	private float lightColorGreen;
//	private float lightColorRed;
//	private float[] lightDiffuse = new float[4];
//	private FloatBuffer lightDiffuseBuffer;
//	private boolean lightEditModeEnabled;
//	private float lightMagnitude;
//	private float[] lightPosition = new float[4];
//	private FloatBuffer lightPositionBuffer;
//	private float[] lightSpecular = new float[4];
//	private FloatBuffer lightSpecularBuffer;
//	private float lightType;
//	private float lightZoom = 2.0F;
//	private boolean lightingEnabled;
//	private SharedPreferences mPreferences;
//	private String modelName;
//	private ArrayList<Model3D> models = new ArrayList();
//	private int oldPointerCount;
//	private float oldX;
//	private float oldX1;
//	private float oldX2;
//	private float oldY;
//	private float oldY1;
//	private float oldY2;
//	private int screenHeight = 1;
//	private int screenWidth = 1;
//	private boolean singleLightRenderRequested;
//	int status = 8;
//	private String textureName;
//	private boolean texturesEnabled;
//	private Model3D theModel;
//	private boolean toggleLight;
//	private boolean toggleTexture;
//	private boolean wireframesEnabled;
//	private float xrot;
//	private float xrotX;
//	private float xrotY;
//	private float xrotZ;
//	private float yrot;
//	private float yrotX;
//	private float yrotY;
//	private float yrotZ;
//	private float zoom = 1.0F;
//
//	public GLEnvironment(Context paramContext) {
//		super(paramContext);
//		this.mPreferences = paramContext.getSharedPreferences("Preferences", 0);
//		this.editor = this.mPreferences.edit();
//		setOnTouchListener(this);
//	}
//
//	private void drawLightSphere(GL10 paramGL10) {
//		paramGL10.glLoadIdentity();
//		paramGL10.glTranslatef(0.0F, 0.0F, -4.0F);
//		if (this.extrema != 0.0F)
//			paramGL10.glScalef(1.0F / this.extrema, 1.0F / this.extrema,
//					1.0F / this.extrema);
//		paramGL10.glTranslatef(-this.center[0], -this.center[1],
//				-this.center[2]);
//		paramGL10.glTranslatef(this.lightPosition[0], this.lightPosition[1],
//				this.lightPosition[2]);
//		paramGL10.glScalef(this.extrema, this.extrema, this.extrema);
//		DrawSphere(paramGL10, 0.2F, 12, 12);
//	}
//
//	private void fireEvent(String paramString, int paramInt) {
//		try {
//			LoadingUpdateEvent localLoadingUpdateEvent = new LoadingUpdateEvent(
//					paramString, paramInt);
//			Iterator localIterator = this._listeners.iterator();
//			while (true) {
//				boolean bool = localIterator.hasNext();
//				if (!bool)
//					return;
//				((LoadingUpdateEventListener) localIterator.next())
//						.onLoadingUpdate(localLoadingUpdateEvent);
//			}
//		} finally {
//		}
//	}
//
//	private void handleLighting(GL10 paramGL10) {
//		if ((this.lightEditModeEnabled) || (this.singleLightRenderRequested)) {
//			paramGL10.glLightfv(16384, 4611, this.lightPositionBuffer);
//			this.singleLightRenderRequested = false;
//		}
//		this.lightAmbient[0] = (this.lightColorRed * this.lightMagnitude * (1.0F - this.lightType));
//		this.lightAmbient[1] = (this.lightColorGreen * this.lightMagnitude * (1.0F - this.lightType));
//		this.lightAmbient[2] = (this.lightColorBlue * this.lightMagnitude * (1.0F - this.lightType));
//		this.lightAmbient[3] = 1.0F;
//		this.lightAmbientBuffer.put(this.lightAmbient);
//		this.lightAmbientBuffer.position(0);
//		this.lightDiffuse[0] = (this.lightColorRed * this.lightMagnitude * this.lightType);
//		this.lightDiffuse[1] = (this.lightColorGreen * this.lightMagnitude * this.lightType);
//		this.lightDiffuse[2] = (this.lightColorBlue * this.lightMagnitude * this.lightType);
//		this.lightDiffuse[3] = 1.0F;
//		this.lightDiffuseBuffer.put(this.lightDiffuse);
//		this.lightDiffuseBuffer.position(0);
//		this.lightSpecular[0] = 1.0F;
//		this.lightSpecular[1] = 1.0F;
//		this.lightSpecular[2] = 1.0F;
//		this.lightSpecular[3] = 1.0F;
//		this.lightSpecularBuffer.put(this.lightSpecular);
//		this.lightSpecularBuffer.position(0);
//		paramGL10.glLightfv(16384, 4608, this.lightAmbientBuffer);
//		paramGL10.glLightfv(16384, 4609, this.lightDiffuseBuffer);
//		paramGL10.glLightfv(16384, 4610, this.lightSpecularBuffer);
//	}
//
//	private void handleTouchVelocity() {
//		if (this.dx > 0.7F) {
//			this.dx -= 0.5F;
//			this.xrot += 0.2F * this.dx;
//		}
//		if (this.dy > 0.7F) {
//			this.dy -= 0.5F;
//			this.yrot += 0.2F * this.dy;
//		}
//		if (this.dx < -0.7F) {
//			this.dx = (0.5F + this.dx);
//			this.xrot += 0.2F * this.dx;
//		} else {
//			this.dx = 0.0F;
//		}
//
//		if (this.dy < -0.7F) {
//			this.dy = (0.5F + this.dy);
//			this.yrot += 0.2F * this.dy;
//			return;
//		}
//		this.dy = 0.0F;
//	}
//
//	private void initializeLighting() {
//		ByteBuffer localByteBuffer1 = ByteBuffer
//				.allocateDirect(4 * this.lightAmbient.length);
//		localByteBuffer1.order(ByteOrder.nativeOrder());
//		this.lightAmbientBuffer = localByteBuffer1.asFloatBuffer();
//		this.lightAmbientBuffer.put(this.lightAmbient);
//		this.lightAmbientBuffer.position(0);
//		ByteBuffer localByteBuffer2 = ByteBuffer
//				.allocateDirect(4 * this.lightDiffuse.length);
//		localByteBuffer2.order(ByteOrder.nativeOrder());
//		this.lightDiffuseBuffer = localByteBuffer2.asFloatBuffer();
//		this.lightDiffuseBuffer.put(this.lightDiffuse);
//		this.lightDiffuseBuffer.position(0);
//		ByteBuffer localByteBuffer3 = ByteBuffer
//				.allocateDirect(4 * this.lightSpecular.length);
//		localByteBuffer3.order(ByteOrder.nativeOrder());
//		this.lightSpecularBuffer = localByteBuffer3.asFloatBuffer();
//		this.lightSpecularBuffer.put(this.lightSpecular);
//		this.lightSpecularBuffer.position(0);
//		ByteBuffer localByteBuffer4 = ByteBuffer
//				.allocateDirect(4 * this.lightPosition.length);
//		localByteBuffer4.order(ByteOrder.nativeOrder());
//		this.lightPositionBuffer = localByteBuffer4.asFloatBuffer();
//		this.lightPositionBuffer.put(this.lightPosition);
//		this.lightPositionBuffer.position(0);
//	}
//
//	private void invertAxisRotation(int paramInt, boolean paramBoolean) {
//		switch (paramInt) {
//		default:
//			return;
//		case 0:
//			if (paramBoolean) {
//				this.xrotX = (-this.xrotX);
//				this.xrotY = (-this.xrotY);
//				this.xrotZ = (-this.xrotZ);
//				return;
//			}
//			this.xrotX = Math.abs(this.xrotX);
//			this.xrotY = Math.abs(this.xrotY);
//			this.xrotZ = Math.abs(this.xrotZ);
//			return;
//		case 1:
//		}
//		if (paramBoolean) {
//			this.yrotX = (-this.yrotX);
//			this.yrotY = (-this.yrotY);
//			this.yrotZ = (-this.yrotZ);
//			return;
//		}
//		this.yrotX = Math.abs(this.yrotX);
//		this.yrotY = Math.abs(this.yrotY);
//		this.yrotZ = Math.abs(this.yrotZ);
//	}
//
//	private void loadModelTextures(GL10 paramGL10) {
//		int i = this.theModel.loadGLTexture(paramGL10, this.textureName);
//		if (this.textureName.contains("Don't Use a Texture"))
//			i = 0;
//		switch (i) {
//		case 2:
//		case 3:
//		default:
//			return;
//		case 5:
//			fireEvent("Error loading texture file.\nDisabling textures.", 1);
//			return;
//		case 6:
//			fireEvent("Error loading texture file.\nDisabling textures.", 1);
//			return;
//		case 1:
//			fireEvent(
//					"Error loading texture file, out of memory.\nDisabling textures.",
//					1);
//			return;
//		case 4:
//		}
//		fireEvent("Invalid texture file.\nDisabling textures.", 1);
//	}
//
//	private int loadModels() {
////		while (true) {
////			int i;
////			try {
////				this.theModel = ObjLoader.generateObjModel(this.modelName);
////				this.extrema = ObjLoader.getExtrema();
////				this.center = ObjLoader.getCenter();
////				ObjLoader.clearPositionValues();
////				ObjLoader.clearMemory();
////				this.models.add(this.theModel);
////				i = 0;
////				if (i >= this.models.size())
////					return 0;
////			} catch (OutOfMemoryError localOutOfMemoryError) {
////				ObjLoader.clearMemory();
////				return 1;
////			} catch (TooManyIndicesException localTooManyIndicesException) {
////				ObjLoader.clearMemory();
////				return 2;
////			} catch (InvalidFileException localInvalidFileException) {
////				ObjLoader.clearMemory();
////				return 3;
////			}
////			((Model3D) this.models.get(i))
////					.setWireFrame(this.wireframesEnabled);
////			i++;
////		}
//		return 0;
//	}
//
//	private void toggleLight(GL10 paramGL10) {
//		if (this.lightingEnabled) {
//			paramGL10.glDisable(2896);
//			if (!this.lightingEnabled)
//				return;
//		}
//		 for (boolean bool = false;; bool = true) {
//			this.lightingEnabled = bool;
//			this.editor.putBoolean("Lighting", this.lightingEnabled);
//			this.editor.commit();
//			paramGL10.glEnable(2896);
//			break;
//		}
//	}
//
//	private void toggleTexture(GL10 paramGL10) {
//		if (this.texturesEnabled) {
//			paramGL10.glDisable(3553);
//			if (!this.texturesEnabled)
//				return;
//		}
//		for (boolean bool = false;; bool = true) {
//			this.texturesEnabled = bool;
//			this.editor.putBoolean("Textures", this.texturesEnabled);
//			this.editor.commit();
//			paramGL10.glEnable(3553);
//			break;
//		}
//	}
//
//	void DrawSphere(GL10 paramGL10, float paramFloat, int paramInt1,
//			int paramInt2) {
//		int i = 0;
//		if (i > paramInt1)
//			return;
//		double d1 = 3.14159265D * (-0.5D + (i - 1) / paramInt1);
//		double d2 = Math.sin(d1);
//		double d3 = Math.cos(d1);
//		double d4 = 3.14159265D * (-0.5D + i / paramInt1);
//		double d5 = Math.sin(d4);
//		double d6 = Math.cos(d4);
//		paramGL10.glColor4f(0.3F, 0.3F, 0.3F, 1.0F);
//		float[] arrayOfFloat1 = new float[6 * (paramInt2 + 1)];
//		float[] arrayOfFloat2 = new float[6 * (paramInt2 + 1)];
//		for (int j = 0;; j++) {
//			if (j > paramInt2) {
//				ByteBuffer localByteBuffer1 = ByteBuffer
//						.allocateDirect(4 * arrayOfFloat1.length);
//				localByteBuffer1.order(ByteOrder.nativeOrder());
//				FloatBuffer localFloatBuffer1 = localByteBuffer1
//						.asFloatBuffer();
//				localFloatBuffer1.put(arrayOfFloat1);
//				localFloatBuffer1.position(0);
//				ByteBuffer localByteBuffer2 = ByteBuffer
//						.allocateDirect(4 * arrayOfFloat2.length);
//				localByteBuffer2.order(ByteOrder.nativeOrder());
//				FloatBuffer localFloatBuffer2 = localByteBuffer2
//						.asFloatBuffer();
//				localFloatBuffer2.put(arrayOfFloat2);
//				localFloatBuffer2.position(0);
//				if (this.texturesEnabled)
//					paramGL10.glDisable(3553);
//				paramGL10.glEnableClientState(32884);
//				paramGL10.glEnableClientState(32885);
//				paramGL10.glVertexPointer(3, 5126, 0, localFloatBuffer1);
//				paramGL10.glNormalPointer(5126, 0, localFloatBuffer2);
//				if (paramFloat == 0.0F)
//					paramFloat = 1.0F;
//				paramGL10.glScalef(paramFloat, paramFloat, paramFloat);
//				paramGL10.glDrawArrays(5, 0, 6 * (paramInt2 + 1) / 3);
//				paramGL10.glScalef(1.0F / paramFloat, 1.0F / paramFloat,
//						1.0F / paramFloat);
//				paramGL10.glDisableClientState(32884);
//				paramGL10.glDisableClientState(32885);
//				if (this.texturesEnabled)
//					paramGL10.glEnable(3553);
//				paramGL10.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
//				i++;
//				break;
//			}
//			double d7 = 6.2831853D * (j - 1) / paramInt2;
//			double d8 = Math.cos(d7);
//			double d9 = Math.sin(d7);
//			arrayOfFloat1[(0 + j * 6)] = ((float) (d8 * d3));
//			arrayOfFloat1[(1 + j * 6)] = ((float) (d9 * d3));
//			arrayOfFloat1[(2 + j * 6)] = ((float) d2);
//			arrayOfFloat1[(3 + j * 6)] = ((float) (d8 * d6));
//			arrayOfFloat1[(4 + j * 6)] = ((float) (d9 * d6));
//			arrayOfFloat1[(5 + j * 6)] = ((float) d5);
//			arrayOfFloat2[(0 + j * 6)] = ((float) (d8 * d3));
//			arrayOfFloat2[(1 + j * 6)] = ((float) (d9 * d3));
//			arrayOfFloat2[(2 + j * 6)] = ((float) d2);
//			arrayOfFloat2[(3 + j * 6)] = ((float) (d8 * d6));
//			arrayOfFloat2[(4 + j * 6)] = ((float) (d9 * d6));
//			arrayOfFloat2[(5 + j * 6)] = ((float) d5);
//		}
//	}
//
//	public void addEventListener(LoadingUpdateEventListener paramLoadingUpdateEventListener)
//  {
//    try
//    {
//      this._listeners.add(paramLoadingUpdateEventListener);
//      return;
//    }
//    finally
//    {
////      localObject = finally;
////      throw localObject;
//    }
//  }
//
//	public void destroyModel() {
//		this.theModel.destroy();
//		this.theModel = null;
//	}
//
//	public boolean getLightEnabled() {
//		return this.lightingEnabled;
//	}
//
//	public int load(String paramString1, String paramString2) {
//		this.modelName = paramString1;
//		this.textureName = paramString2;
//		loadPreferences();
//		this.status = loadModels();
//		if (this.status == 0)
//			setInitialLightPosition(false);
//		initializeLighting();
//		return this.status;
//	}
//
//	public void loadPreferences() {
//		this.toggleTexture = false;
//		this.toggleLight = false;
//		this.texturesEnabled = this.mPreferences.getBoolean("Textures", true);
//		this.lightingEnabled = this.mPreferences.getBoolean("Lighting", true);
//		this.wireframesEnabled = this.mPreferences.getBoolean("Wireframe",
//				false);
//		this.lightEditModeEnabled = this.mPreferences.getBoolean(
//				"Light Edit Mode", false);
//	}
//
//	public void onDrawFrame(GL10 paramGL10) {
//		if (this.status == 8)
//			;
//		do {
//			paramGL10.glClear(16640);
//			paramGL10.glLoadIdentity();
//			if (this.toggleTexture) {
//				toggleTexture(paramGL10);
//				this.toggleTexture = false;
//			}
//			if (this.toggleLight) {
//				toggleLight(paramGL10);
//				this.toggleLight = false;
//			}
//			handleLighting(paramGL10);
//			handleTouchVelocity();
//			paramGL10.glTranslatef(0.0F, 0.0F, -4.0F);
//			if (this.extrema != 0.0F)
//				paramGL10.glScalef(1.0F / this.extrema, 1.0F / this.extrema,
//						1.0F / this.extrema);
//			paramGL10.glRotatef(this.yrot, this.yrotX, this.yrotY, this.yrotZ);
//			paramGL10.glRotatef(this.xrot, this.xrotX, this.xrotY, this.xrotZ);
//			paramGL10.glScalef(this.zoom, this.zoom, this.zoom);
//			paramGL10.glTranslatef(-this.center[0], -this.center[1],
//					-this.center[2]);
//			this.theModel.draw(paramGL10);
//		} while (!this.lightEditModeEnabled);
//		drawLightSphere(paramGL10);
//	}
//
//	public void onSurfaceChanged(GL10 paramGL10, int paramInt1, int paramInt2) {
//		if (paramInt2 == 0)
//			paramInt2 = 1;
//		this.screenWidth = paramInt1;
//		this.screenHeight = paramInt2;
//		paramGL10.glViewport(0, 0, paramInt1, paramInt2);
//		paramGL10.glMatrixMode(5889);
//		paramGL10.glLoadIdentity();
//		GLU.gluPerspective(paramGL10, 45.0F, paramInt1 / paramInt2, 0.1F,
//				100.0F);
//		paramGL10.glMatrixMode(5888);
//		paramGL10.glLoadIdentity();
//	}
//
//	public void onSurfaceCreated(GL10 paramGL10, EGLConfig paramEGLConfig) {
//		if (this.status == 8)
//			return;
//		loadModelTextures(paramGL10);
//		paramGL10.glLightfv(16384, 4608, this.lightAmbientBuffer);
//		paramGL10.glLightfv(16384, 4609, this.lightDiffuseBuffer);
//		paramGL10.glLightfv(16384, 4610, this.lightSpecularBuffer);
//		paramGL10.glLightfv(16384, 4611, this.lightPositionBuffer);
//		paramGL10.glLightf(16384, 4615, 1.0F);
//		paramGL10.glEnable(16384);
//		if (this.texturesEnabled)
//			paramGL10.glEnable(3553);
//		if (this.lightingEnabled)
//			paramGL10.glEnable(2896);
//		paramGL10.glDisable(3024);
//		paramGL10.glShadeModel(7425);
//		paramGL10.glClearColor(0.0F, 0.0F, 0.0F, 0.5F);
//		paramGL10.glClearDepthf(1.0F);
//		paramGL10.glEnable(2929);
//		paramGL10.glDepthFunc(515);
//		paramGL10.glEnable(32826);
//		paramGL10.glHint(3152, 4354);
//	}
//
//	public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
//		if ((paramMotionEvent.getAction() == 0)
//				&& (paramMotionEvent.getPointerCount() == 1)) {
//			this.oldX = paramMotionEvent.getX();
//			this.oldY = paramMotionEvent.getY();
//			this.dy = 0.0F;
//			this.dx = 0.0F;
//		}
//		float f7;
//		float f8;
//		if (paramMotionEvent.getAction() == 2) {
//			if (paramMotionEvent.getPointerCount() != 1)
//				break label295;
//			f7 = paramMotionEvent.getX();
//			f8 = paramMotionEvent.getY();
//			if (this.oldPointerCount == 2) {
//				this.oldX = f7;
//				this.oldY = f8;
//			}
//			if ((!this.lightEditModeEnabled) || (this.oldPointerCount == 2))
//				break label270;
//			this.lightPosition[0] = (10.0F * this.extrema
//					* (f7 / this.screenWidth - 0.5F) + this.center[0]);
//			this.lightPosition[1] = (10.0F * this.extrema
//					* (0.5F + -f8 / this.screenHeight) + this.center[1]);
//			this.lightPosition[2] = (this.lightZoom * this.extrema + this.center[2]);
//			this.lightPosition[3] = 0.0F;
//			this.lightPositionBuffer.put(this.lightPosition);
//			this.lightPositionBuffer.position(0);
//			this.xrot += 0.2F * this.dx;
//			this.yrot += 0.2F * this.dy;
//			this.oldX = f7;
//			this.oldY = f8;
//			this.oldPointerCount = 1;
//		}
//		while (true) {
//			this.dx = (f7 - this.oldX);
//			this.dy = (f8 - this.oldY);
//			break;
//			if (paramMotionEvent.getPointerCount() != 2)
//				continue;
//			float f1 = 0.0F;
//			float f2 = 0.0F;
//			float f3 = 0.0F;
//			try {
//				f1 = paramMotionEvent.getX(paramMotionEvent.getPointerId(0));
//				f2 = paramMotionEvent.getY(paramMotionEvent.getPointerId(0));
//				f3 = paramMotionEvent.getX(paramMotionEvent.getPointerId(1));
//				float f6 = paramMotionEvent.getY(paramMotionEvent
//						.getPointerId(1));
//				float f4 = f6;
//				int i = 1;
//				if (i == 0)
//					continue;
//				float f5 = (float) Math.sqrt(Math.pow(f3 - f1, 2.0D)
//						+ Math.pow(f4 - f2, 2.0D))
//						- (float) Math
//								.sqrt(Math.pow(this.oldX2 - this.oldX1, 2.0D)
//										+ Math.pow(this.oldY2 - this.oldY1,
//												2.0D));
//				if (Math.abs(f5) > 100.0F)
//					f5 = 0.0F;
//				if (this.lightEditModeEnabled) {
//					if ((this.lightZoom > -5.0F) && (this.lightZoom < 5.0F))
//						this.lightZoom += 0.01F * f5;
//					if (this.lightZoom >= 5.0F) {
//						this.lightZoom = 5.0F;
//						if (f5 < 0.0F)
//							this.lightZoom += 0.01F * f5;
//						label533: this.lightPosition[2] = (this.lightZoom
//								* this.extrema + this.center[2]);
//						this.lightPosition[3] = 0.0F;
//						this.lightPositionBuffer.put(this.lightPosition);
//						this.lightPositionBuffer.position(0);
//						f5 = 0.0F;
//					}
//				} else {
//					if ((this.zoom > 0.25F) && (this.zoom < 7.0F))
//						this.zoom += 0.01F * f5;
//					if (this.zoom < 7.0F)
//						break label737;
//					this.zoom = 7.0F;
//					if (f5 < 0.0F)
//						this.zoom += 0.01F * f5;
//				}
//				while (true) {
//					this.oldX1 = f1;
//					this.oldY1 = f2;
//					this.oldX2 = f3;
//					this.oldY2 = f4;
//					this.oldPointerCount = 2;
//					break;
//					if (this.lightZoom > -5.0F)
//						break label533;
//					this.lightZoom = -5.0F;
//					if (f5 <= 0.0F)
//						break label533;
//					this.lightZoom += 0.01F * f5;
//					break label533;
//					label737: if (this.zoom <= 0.25F) {
//						this.zoom = 0.25F;
//						if (f5 > 0.0F)
//							this.zoom += 0.01F * f5;
//					}
//				}
//			} catch (IllegalArgumentException localIllegalArgumentException) {
//				while (true) {
//					int i = 0;
//					float f4 = 0.0F;
//				}
//			}
//		}
//	}
//
//	public void removeEventListener(LoadingUpdateEventListener paramLoadingUpdateEventListener)
//  {
//    try
//    {
//      this._listeners.remove(paramLoadingUpdateEventListener);
//      return;
//    }
//    finally
//    {
//      localObject = finally;
//      throw localObject;
//    }
//  }
//
//	public void setAxisRotation(int paramInt) {
//		switch (paramInt) {
//		default:
//			return;
//		case 2131296319:
//		case 2131296341:
//			this.xrotX = 1.0F;
//			this.xrotY = 0.0F;
//			this.xrotZ = 0.0F;
//			return;
//		case 2131296320:
//		case 2131296342:
//			this.xrotX = 0.0F;
//			this.xrotY = 1.0F;
//			this.xrotZ = 0.0F;
//			return;
//		case 2131296321:
//		case 2131296343:
//			this.xrotX = 0.0F;
//			this.xrotY = 0.0F;
//			this.xrotZ = 1.0F;
//			return;
//		case 2131296326:
//		case 2131296348:
//			this.yrotX = 1.0F;
//			this.yrotY = 0.0F;
//			this.yrotZ = 0.0F;
//			return;
//		case 2131296327:
//		case 2131296349:
//			this.yrotX = 0.0F;
//			this.yrotY = 1.0F;
//			this.yrotZ = 0.0F;
//			return;
//		case 2131296328:
//		case 2131296350:
//		}
//		this.yrotX = 0.0F;
//		this.yrotY = 0.0F;
//		this.yrotZ = 1.0F;
//	}
//
//	public void setInitialLightPosition(boolean paramBoolean) {
//		this.lightPosition[0] = 0.0F;
//		this.lightPosition[1] = (1.0F * this.extrema);
//		this.lightPosition[2] = (2.0F * this.extrema);
//		this.lightPosition[3] = 0.0F;
//		if (paramBoolean) {
//			initializeLighting();
//			this.singleLightRenderRequested = true;
//		}
//	}
//
//	public void setInitialRotationAxes(boolean paramBoolean) {
//		int i = this.mPreferences
//				.getInt("Horizontal Rotation GLES20Axis", 2131296342);
//		int j = this.mPreferences.getInt("Vertical Rotation GLES20Axis", 2131296348);
//		boolean bool1 = this.mPreferences.getBoolean(
//				"Horizontal Rotation GLES20Axis Inversion", false);
//		boolean bool2 = this.mPreferences.getBoolean(
//				"Vertical Rotation GLES20Axis Inversion", false);
//		if (paramBoolean) {
//			this.xrot = 0.0F;
//			this.yrot = 0.0F;
//		}
//		setAxisRotation(i);
//		setAxisRotation(j);
//		invertAxisRotation(0, bool1);
//		invertAxisRotation(1, bool2);
//	}
//
//	public void toggleLight() {
//		this.toggleLight = true;
//	}
//
//	public void toggleLightEditMode() {
//		if (this.lightEditModeEnabled)
//			;
//		for (boolean bool = false;; bool = true) {
//			this.lightEditModeEnabled = bool;
//			this.editor
//					.putBoolean("Light Edit Mode", this.lightEditModeEnabled);
//			this.editor.commit();
//			return;
//		}
//	}
//
//	public void toggleTexture() {
//		this.toggleTexture = true;
//	}
//
//	public void toggleWireframe() {
//		int i = 0;
//		if (i >= this.models.size())
//			if (!this.wireframesEnabled)
//				break label75;
//		label75: for (boolean bool = false;; bool = true) {
//			this.wireframesEnabled = bool;
//			this.editor.putBoolean("Wireframe", this.wireframesEnabled);
//			this.editor.commit();
//			return;
//			((Model3D) this.models.get(i)).toggleWireframe();
//			i++;
//			break;
//		}
//	}
//
//	public void updateLighting(int paramInt1, int paramInt2) {
//		switch (paramInt2) {
//		default:
//			return;
//		case 0:
//			this.lightColorRed = (paramInt1 / 100.0F);
//			return;
//		case 1:
//			this.lightColorGreen = (paramInt1 / 100.0F);
//			return;
//		case 2:
//			this.lightColorBlue = (paramInt1 / 100.0F);
//			return;
//		case 3:
//			this.lightMagnitude = (paramInt1 / 25.0F);
//			return;
//		case 4:
//		}
//		this.lightType = (paramInt1 / 100.0F);
//	}
//}
//
///*
