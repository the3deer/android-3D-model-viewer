package org.andresoviedo.app.model3D.examples;
//package org.andresoviedo.app.model3D.model;
//
//import android.app.Activity;
//import android.app.AlertDialog;
//import android.app.AlertDialog.Builder;
//import android.app.Dialog;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.os.Environment;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//import android.widget.ArrayAdapter;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.CompoundButton.OnCheckedChangeListener;
//import android.widget.ListView;
//import android.widget.RadioGroup;
//import android.widget.SeekBar;
//import android.widget.SeekBar.OnSeekBarChangeListener;
//import android.widget.TextView;
//import android.widget.Toast;
//import android.widget.ToggleButton;
//import java.io.File;
//
//public class GLActivity extends ModelViewerActivity
//  implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, RadioGroup.OnCheckedChangeListener, CompoundButton.OnCheckedChangeListener, LoadingUpdateEventListener
//{
//  public static final String NO_TEXTURE_CHOSEN = "Don't Use a Texture";
//  private static boolean loading = false;
//  public static String loadingMessage;
//  public static boolean noModelNormals;
//  public static boolean noModelTextureCoords;
//  private SeekBar blueSeekBar;
//  private SharedPreferences.Editor editor;
//  private GLEnvironment glSurface;
//  private SeekBar greenSeekBar;
//  private RadioGroup horizontalGroup;
//  private ToggleButton horizontalInversionToggleButton;
//  private ToggleButton lightEditModeToggleButton;
//  private Button lightResetButton;
//  private TextView loadingTextView;
//  private SharedPreferences mPreferences;
//  private SeekBar magnitudeSeekBar;
//  private File modelDirectory;
//  private TextView modelEmptyDirListView;
//  private String[] modelListStrings;
//  private ListView modelListView;
//  private String modelName;
//  private SeekBar redSeekBar;
//  private Button rotationResetButton;
//  private File sdCardDirectory;
//  private File textureDirectory;
//  private TextView textureEmptyDirListView;
//  private String[] textureListStrings;
//  private String[] textureListStringsAdditional;
//  private ListView textureListView;
//  private String textureName;
//  private SeekBar typeSeekBar;
//  private RadioGroup verticalGroup;
//  private ToggleButton verticalInversionToggleButton;
//  private boolean writeAccess;
//
//  private String[] filterByExtension(String[] paramArrayOfString1, String[] paramArrayOfString2)
//  {
////    int i = 0;
////    int j = 0;
////    String[] arrayOfString;
////    int m;
////    int n;
////    if (j >= paramArrayOfString1.length)
////    {
////      arrayOfString = new String[i];
////      m = 0;
////      n = 0;
////      if (m >= paramArrayOfString1.length)
////        return arrayOfString;
////    }
////    else
////    {
////      label98: for (int k = 0; ; k++)
////      {
////        if (k >= paramArrayOfString2.length);
////        while (true)
////        {
////          j++;
////          break;
////          if ((!paramArrayOfString1[j].contains(paramArrayOfString2[k])) || (!paramArrayOfString1[j].substring(paramArrayOfString1[j].indexOf(paramArrayOfString2[k])).equals(paramArrayOfString2[k])))
////            break label98;
////          i++;
////        }
////      }
////    }
////    label177: for (int i1 = 0; ; i1++)
////    {
////      if (i1 >= paramArrayOfString2.length);
////      while (true)
////      {
////        m++;
////        break;
////        if ((!paramArrayOfString1[m].contains(paramArrayOfString2[i1])) || (!paramArrayOfString1[m].substring(paramArrayOfString1[m].indexOf(paramArrayOfString2[i1])).equals(paramArrayOfString2[i1])))
////          break label177;
////        arrayOfString[n] = paramArrayOfString1[m];
////        n++;
////      }
////    }
//	  return null;
//  }
//
//  private void loadEnvironment()
//  {
//    setContentView(2130903044);
//    this.loadingTextView = ((TextView)findViewById(2131296291));
//    Loader localLoader = new Loader(this);
//    localLoader.setName("Model Loader Thread");
//    localLoader.start();
//  }
//
//  private void setModelListView()
//  {
////    setContentView(2130903046);
////    String str1 = Environment.getExternalStorageState();
////    if ("mounted".equals(str1))
////      this.writeAccess = true;
////    while (true)
////    {
////      this.sdCardDirectory = Environment.getExternalStorageDirectory();
////      this.modelDirectory = new File(this.sdCardDirectory.getAbsolutePath() + "/3DModelViewer/models/");
////      if ((this.modelDirectory.exists()) && (this.modelDirectory.isDirectory()))
////        break label151;
////      if (this.writeAccess)
////        break;
////      Toast.makeText(this, "Error: Models folder not found.\nPlease refer to the instructions screen for help.", 1).show();
////      finish();
////      return;
////      if ("mounted_ro".equals(str1))
////      {
////        this.writeAccess = false;
////      }
////      else
////      {
////        Toast.makeText(this, "Error: No SD Card / Access Denied", 1).show();
////        finish();
////      }
////    }
////    this.modelDirectory.mkdirs();
////    label151: this.modelListStrings = this.modelDirectory.list();
////    if (this.modelListStrings.length == 0)
////    {
////      this.modelEmptyDirListView = ((TextView)findViewById(2131296294));
////      this.modelEmptyDirListView.setText(2131034133);
////      this.modelEmptyDirListView.setVisibility(0);
////      this.modelListView = ((ListView)findViewById(2131296295));
////      this.modelListView.setAdapter(new ArrayAdapter(this, 17367043, this.modelListStrings));
////      this.modelListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
////      {
////        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
////        {
////          GLActivity.this.modelName = GLActivity.this.modelListStrings[paramAnonymousInt];
////          GLActivity.this.setTextureListView();
////        }
////      });
////      return;
////    }
////    String[] arrayOfString = { ".obj" };
////    this.modelListStrings = filterByExtension(this.modelListStrings, arrayOfString);
////    int i = 1;
////    while (i != 0)
////    {
////      i = 0;
////      for (int j = 0; j < -1 + this.modelListStrings.length; j++)
////        if (this.modelListStrings[j].compareToIgnoreCase(this.modelListStrings[(j + 1)]) > 0)
////        {
////          String str2 = this.modelListStrings[j];
////          this.modelListStrings[j] = this.modelListStrings[(j + 1)];
////          this.modelListStrings[(j + 1)] = str2;
////          i = 1;
////        }
////    }
//  }
//
//  private void setTextureListView()
//  {
////    setContentView(2130903050);
////    this.textureDirectory = new File(this.sdCardDirectory.getAbsolutePath() + "/3DModelViewer/textures/");
////    if ((!this.textureDirectory.exists()) || (!this.textureDirectory.isDirectory()))
////    {
////      if (!this.writeAccess)
////      {
////        Toast.makeText(this, "Error: Textures folder not found.\nPlease refer to the instructions screen for help.", 1).show();
////        finish();
////        return;
////      }
////      this.textureDirectory.mkdirs();
////    }
////    this.textureListStrings = this.textureDirectory.list();
////    if (this.textureListStrings.length == 0)
////    {
////      this.textureEmptyDirListView = ((TextView)findViewById(2131296355));
////      this.textureEmptyDirListView.setText(2131034135);
////      this.textureEmptyDirListView.setVisibility(0);
////      this.textureListStringsAdditional = new String[0];
////      this.textureListView = ((ListView)findViewById(2131296356));
////      this.textureListView.setAdapter(new ArrayAdapter(this, 17367043, this.textureListStringsAdditional));
////      this.textureListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
////      {
////        public void onItemClick(AdapterView<?> paramAnonymousAdapterView, View paramAnonymousView, int paramAnonymousInt, long paramAnonymousLong)
////        {
////          GLActivity.this.textureName = GLActivity.this.textureListStringsAdditional[paramAnonymousInt];
////          GLActivity.this.loadEnvironment();
////        }
////      });
////      return;
////    }
////    String[] arrayOfString = { ".jpg", ".png" };
////    this.textureListStrings = filterByExtension(this.textureListStrings, arrayOfString);
////    int i = 1;
////    while (true)
////    {
////      if (i == 0)
////      {
////        this.textureListStringsAdditional = new String[1 + this.textureListStrings.length];
////        this.textureListStringsAdditional[0] = "Don't Use a Texture";
////        for (int k = 0; k < this.textureListStrings.length; k++)
////          this.textureListStringsAdditional[(k + 1)] = this.textureListStrings[k];
////        break;
////      }
////      i = 0;
////      for (int j = 0; j < -1 + this.textureListStrings.length; j++)
////        if (this.textureListStrings[j].compareToIgnoreCase(this.textureListStrings[(j + 1)]) > 0)
////        {
////          String str = this.textureListStrings[j];
////          this.textureListStrings[j] = this.textureListStrings[(j + 1)];
////          this.textureListStrings[(j + 1)] = str;
////          i = 1;
////        }
////    }
//  }
//
//  public void onBackPressed()
//  {
////    if ((findViewById(2131296295) != null) || (findViewById(2131296356) != null) || (findViewById(2131296291) != null))
////      setContentView(this.glSurface);
////    super.onBackPressed();
//  }
//
//  public void onCheckedChanged(CompoundButton paramCompoundButton, boolean paramBoolean)
//  {
////    switch (paramCompoundButton.getId())
////    {
////    default:
////    case 2131296345:
////    case 2131296352:
////    }
////    while (true)
////    {
////      this.editor.commit();
////      this.glSurface.setInitialRotationAxes(false);
////      return;
////      this.editor.putBoolean("Horizontal Rotation GLES20Axis Inversion", paramBoolean);
////      continue;
////      this.editor.putBoolean("Vertical Rotation GLES20Axis Inversion", paramBoolean);
////    }
//  }
//
//  public void onCheckedChanged(RadioGroup paramRadioGroup, int paramInt)
//  {
////    switch (paramInt)
////    {
////    case 2131296344:
////    case 2131296345:
////    case 2131296346:
////    case 2131296347:
////    default:
////    case 2131296341:
////    case 2131296342:
////    case 2131296343:
////    case 2131296348:
////    case 2131296349:
////    case 2131296350:
////    }
////    while (true)
////    {
////      this.editor.commit();
////      return;
////      this.glSurface.setAxisRotation(2131296341);
////      this.editor.putInt("Horizontal Rotation GLES20Axis", paramInt);
////      continue;
////      this.glSurface.setAxisRotation(2131296342);
////      this.editor.putInt("Horizontal Rotation GLES20Axis", paramInt);
////      continue;
////      this.glSurface.setAxisRotation(2131296343);
////      this.editor.putInt("Horizontal Rotation GLES20Axis", paramInt);
////      continue;
////      this.glSurface.setAxisRotation(2131296348);
////      this.editor.putInt("Vertical Rotation GLES20Axis", paramInt);
////      continue;
////      this.glSurface.setAxisRotation(2131296349);
////      this.editor.putInt("Vertical Rotation GLES20Axis", paramInt);
////      continue;
////      this.glSurface.setAxisRotation(2131296350);
////      this.editor.putInt("Vertical Rotation GLES20Axis", paramInt);
////    }
//  }
//
//  public void onClick(View paramView)
//  {
//    if (paramView.getId() == 5)
//      this.glSurface.toggleLightEditMode();
//    if (paramView.getId() == 2131296289)
//    {
//      this.redSeekBar.setProgress(100);
//      this.greenSeekBar.setProgress(100);
//      this.blueSeekBar.setProgress(100);
//      this.magnitudeSeekBar.setProgress(50);
//      this.typeSeekBar.setProgress(50);
//      this.lightEditModeToggleButton.setChecked(false);
//      this.editor.putInt("Light Red Value", this.redSeekBar.getProgress());
//      this.editor.putInt("Light Green Value", this.greenSeekBar.getProgress());
//      this.editor.putInt("Light Blue Value", this.blueSeekBar.getProgress());
//      this.editor.putInt("Light Magnitude Value", this.magnitudeSeekBar.getProgress());
//      this.editor.putInt("Light Type Value", this.typeSeekBar.getProgress());
//      this.editor.putBoolean("Light Edit Mode", this.lightEditModeToggleButton.isChecked());
//      this.editor.commit();
//      this.glSurface.setInitialLightPosition(true);
//      this.glSurface.loadPreferences();
//    }
//    if (paramView.getId() == 2131296353)
//    {
//      this.horizontalGroup.check(2131296342);
//      this.verticalGroup.check(2131296348);
//      this.horizontalInversionToggleButton.setChecked(false);
//      this.verticalInversionToggleButton.setChecked(false);
//      this.editor.putInt("Horizontal Rotation GLES20Axis", this.horizontalGroup.getCheckedRadioButtonId());
//      this.editor.putInt("Vertical Rotation GLES20Axis", this.verticalGroup.getCheckedRadioButtonId());
//      this.editor.putBoolean("Horizontal Rotation GLES20Axis Inversion", this.horizontalInversionToggleButton.isChecked());
//      this.editor.putBoolean("Vertical Rotation GLES20Axis Inversion", this.verticalInversionToggleButton.isChecked());
//      this.editor.commit();
//      this.glSurface.setInitialRotationAxes(true);
//    }
//  }
//
//  public void onConfigurationChanged(Configuration paramConfiguration)
//  {
//    super.onConfigurationChanged(paramConfiguration);
//    this.glSurface.requestLayout();
//  }
//
//  protected void onCreate(Bundle paramBundle)
//  {
//    super.onCreate(paramBundle);
//    loadingMessage = "Loading...";
//    this.glSurface = new GLEnvironment(this);
//    this.glSurface.setRenderer(this.glSurface);
//    this.glSurface.addEventListener(this);
//    this.mPreferences = getSharedPreferences("Preferences", 0);
//    this.editor = this.mPreferences.edit();
//    setModelListView();
//  }
//
//  protected Dialog onCreateDialog(int paramInt)
//  {
//    switch (paramInt)
//    {
//    default:
//      return null;
//    case 0:
//      View localView2 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903043, (ViewGroup)findViewById(2131296275));
//      this.redSeekBar = ((SeekBar)localView2.findViewById(2131296279));
//      this.greenSeekBar = ((SeekBar)localView2.findViewById(2131296281));
//      this.blueSeekBar = ((SeekBar)localView2.findViewById(2131296283));
//      this.magnitudeSeekBar = ((SeekBar)localView2.findViewById(2131296285));
//      this.typeSeekBar = ((SeekBar)localView2.findViewById(2131296287));
//      this.lightEditModeToggleButton = ((ToggleButton)localView2.findViewById(2131296288));
//      this.lightResetButton = ((Button)localView2.findViewById(2131296289));
//      this.redSeekBar.setOnSeekBarChangeListener(this);
//      this.greenSeekBar.setOnSeekBarChangeListener(this);
//      this.blueSeekBar.setOnSeekBarChangeListener(this);
//      this.magnitudeSeekBar.setOnSeekBarChangeListener(this);
//      this.typeSeekBar.setOnSeekBarChangeListener(this);
//      this.lightEditModeToggleButton.setOnClickListener(this);
//      this.lightResetButton.setOnClickListener(this);
//      this.redSeekBar.setId(0);
//      this.greenSeekBar.setId(1);
//      this.blueSeekBar.setId(2);
//      this.magnitudeSeekBar.setId(3);
//      this.typeSeekBar.setId(4);
//      this.lightEditModeToggleButton.setId(5);
//      this.redSeekBar.setProgress(this.mPreferences.getInt("Light Red Value", 100));
//      this.greenSeekBar.setProgress(this.mPreferences.getInt("Light Green Value", 100));
//      this.blueSeekBar.setProgress(this.mPreferences.getInt("Light Blue Value", 100));
//      this.magnitudeSeekBar.setProgress(this.mPreferences.getInt("Light Magnitude Value", 50));
//      this.typeSeekBar.setProgress(this.mPreferences.getInt("Light Type Value", 50));
//      this.lightEditModeToggleButton.setChecked(this.mPreferences.getBoolean("Light Edit Mode", false));
//      AlertDialog.Builder localBuilder2 = new AlertDialog.Builder(this);
//      localBuilder2.setView(localView2);
//      localBuilder2.setTitle(2131034120);
//      return localBuilder2.create();
//    case 1:
//    }
//    View localView1 = ((LayoutInflater)getSystemService("layout_inflater")).inflate(2130903049, (ViewGroup)findViewById(2131296336));
//    this.horizontalGroup = ((RadioGroup)localView1.findViewById(2131296340));
//    this.verticalGroup = ((RadioGroup)localView1.findViewById(2131296347));
//    this.horizontalInversionToggleButton = ((ToggleButton)localView1.findViewById(2131296345));
//    this.verticalInversionToggleButton = ((ToggleButton)localView1.findViewById(2131296352));
//    this.rotationResetButton = ((Button)localView1.findViewById(2131296353));
//    this.horizontalGroup.setOnCheckedChangeListener(this);
//    this.verticalGroup.setOnCheckedChangeListener(this);
//    this.horizontalInversionToggleButton.setOnCheckedChangeListener(this);
//    this.verticalInversionToggleButton.setOnCheckedChangeListener(this);
//    this.rotationResetButton.setOnClickListener(this);
//    int i = this.mPreferences.getInt("Horizontal Rotation GLES20Axis", 2131296342);
//    int j;
////    if (i == 2131296319)
////    {
////      i = 2131296341;
////      j = this.mPreferences.getInt("Vertical Rotation GLES20Axis", 2131296348);
////      if (j != 2131296326)
////        break label732;
////      j = 2131296348;
////    }
////    while (true)
////    {
////      this.horizontalGroup.check(i);
////      this.verticalGroup.check(j);
////      this.horizontalInversionToggleButton.setChecked(this.mPreferences.getBoolean("Horizontal Rotation GLES20Axis Inversion", false));
////      this.verticalInversionToggleButton.setChecked(this.mPreferences.getBoolean("Vertical Rotation GLES20Axis Inversion", false));
////      AlertDialog.Builder localBuilder1 = new AlertDialog.Builder(this);
////      localBuilder1.setView(localView1);
////      localBuilder1.setTitle(2131034143);
////      return localBuilder1.create();
////      if (i == 2131296320)
////      {
////        i = 2131296342;
////        break;
////      }
////      if (i != 2131296321)
////        break;
////      i = 2131296343;
////      break;
////      label732: if (j == 2131296327)
////        j = 2131296349;
////      else if (j == 2131296328)
////        j = 2131296350;
////    }
//    return null;
//  }
//
//  public boolean onCreateOptionsMenu(Menu paramMenu)
//  {
//    super.onCreateOptionsMenu(paramMenu);
//    getMenuInflater().inflate(2131230720, paramMenu);
//    return true;
//  }
//
//  public void onLoadingUpdate(LoadingUpdateEvent paramLoadingUpdateEvent)
//  {
//    final String str = paramLoadingUpdateEvent.getMessage();
//    runOnUiThread(new Runnable()
//    {
//      public void run()
//      {
////        if (this.val$type == 1)
////        {
////          Toast.makeText(jdField_this, str, 1).show();
////          return;
////        }
//        GLActivity.this.loadingTextView.setText(str);
//      }
//    });
//  }
//
//  public boolean onOptionsItemSelected(MenuItem paramMenuItem)
//  {
//    switch (paramMenuItem.getItemId())
//    {
//    default:
//      return false;
//    case 2131296359:
//      this.glSurface.toggleTexture();
//      return true;
//    case 2131296360:
//      this.glSurface.toggleLight();
//      return true;
//    case 2131296361:
//      this.glSurface.toggleWireframe();
//      return true;
//    case 2131296358:
//      showDialog(0);
//      return true;
//    case 2131296357:
//    }
//    showDialog(1);
//    return true;
//  }
//
//  protected void onPause()
//  {
//    super.onPause();
//    this.glSurface.onPause();
//  }
//
//  public void onProgressChanged(SeekBar paramSeekBar, int paramInt, boolean paramBoolean)
//  {
//    this.glSurface.updateLighting(paramInt, paramSeekBar.getId());
//  }
//
//  protected void onResume()
//  {
//    super.onResume();
//    this.glSurface.onResume();
//  }
//
//  public void onStartTrackingTouch(SeekBar paramSeekBar)
//  {
//  }
//
//  public void onStopTrackingTouch(SeekBar paramSeekBar)
//  {
//    switch (paramSeekBar.getId())
//    {
//    default:
//    case 0:
//    case 1:
//    case 2:
//    case 3:
//    case 4:
//    }
//    while (true)
//    {
//      this.editor.commit();
//      return;
////      this.editor.putInt("Light Red Value", paramSeekBar.getProgress());
////      continue;
////      this.editor.putInt("Light Green Value", paramSeekBar.getProgress());
////      continue;
////      this.editor.putInt("Light Blue Value", paramSeekBar.getProgress());
////      continue;
////      this.editor.putInt("Light Magnitude Value", paramSeekBar.getProgress());
////      continue;
////      this.editor.putInt("Light Type Value", paramSeekBar.getProgress());
//    }
//  }
//
//  private class Loader extends Thread
//  {
//    private Activity activity;
//
//    public Loader(Context arg2)
//    {
//      Object localObject;
//      this.activity = ((Activity)arg2);
//    }
//
//    public void run()
//    {
//      while (true)
//      {
//        int i;
//        if (!GLActivity.loading)
//        {
////          GLActivity.loading = true;
////          ObjLoader.addEventListener((LoadingUpdateEventListener)this.activity);
////          ObjLoader.clearMemory();
////          i = GLActivity.this.glSurface.load(GLActivity.this.sdCardDirectory + "/3DModelViewer/models/" + GLActivity.this.modelName, GLActivity.this.sdCardDirectory + "/3DModelViewer/textures/" + GLActivity.this.textureName);
////          ObjLoader.removeEventListener((LoadingUpdateEventListener)this.activity);
//        }
////        switch (i)
////        {
////        default:
////          return;
////          try
////          {
////            Thread.sleep(500L);
////          }
////          catch (InterruptedException localInterruptedException)
////          {
////          }
////        case 0:
////        case 1:
////        case 2:
////        case 3:
////        }
//      }
////      GLActivity.this.glSurface.updateLighting(GLActivity.this.mPreferences.getInt("Light Red Value", 100), 0);
////      GLActivity.this.glSurface.updateLighting(GLActivity.this.mPreferences.getInt("Light Green Value", 100), 1);
////      GLActivity.this.glSurface.updateLighting(GLActivity.this.mPreferences.getInt("Light Blue Value", 100), 2);
////      GLActivity.this.glSurface.updateLighting(GLActivity.this.mPreferences.getInt("Light Magnitude Value", 50), 3);
////      GLActivity.this.glSurface.updateLighting(GLActivity.this.mPreferences.getInt("Light Type Value", 50), 4);
////      GLActivity.this.glSurface.setInitialRotationAxes(true);
////      GLActivity.loading = false;
////      this.activity.runOnUiThread(new Runnable()
////      {
////        public void run()
////        {
////          GLActivity.this.setContentView(GLActivity.this.glSurface);
////        }
////      });
////      return;
////      GLActivity.loading = false;
////      this.activity.runOnUiThread(new Runnable()
////      {
////        public void run()
////        {
////          Toast.makeText(GLActivity.this, "Error: Out of Memory", 1).show();
////          GLActivity.this.finish();
////        }
////      });
////      return;
////      GLActivity.loading = false;
////      this.activity.runOnUiThread(new Runnable()
////      {
////        public void run()
////        {
////          Toast.makeText(GLActivity.this, "Error: Too Many Triangles", 1).show();
////          GLActivity.this.finish();
////        }
////      });
////      return;
////      GLActivity.loading = false;
////      this.activity.runOnUiThread(new Runnable()
////      {
////        public void run()
////        {
////          Toast.makeText(GLActivity.this, "Error: Invalid Model File", 1).show();
////          GLActivity.this.finish();
////        }
////      });
//    }
//  }
//}
//
