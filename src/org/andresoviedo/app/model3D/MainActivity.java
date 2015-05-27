package org.andresoviedo.app.model3D;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.andresoviedo.dddmodel.R;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// WebView myWebView = (WebView) findViewById(R.id.main_logo_webview);
		// myWebView.loadUrl("file:///android_res/raw/ic_launcher.gif");
//		init();
		MainActivity.this.startActivity(new Intent(MainActivity.this
				.getApplicationContext(), ModelActivity.class));
		MainActivity.this.finish();
	}

	private void init() {
		try {
			Thread tcopy = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						installExamples();
					} catch (Exception ex) {
						Toast.makeText(
								MainActivity.this.getApplicationContext(),
								"Unexpected error: " + ex.getMessage(),
								Toast.LENGTH_SHORT).show();
						Log.e("init", "Unexpected error: " + ex.getMessage(),
								ex);
					}
				}
			});
			Thread tsplash = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(000);
						MainActivity.this.startActivity(new Intent(
								MainActivity.this.getApplicationContext(),
								ModelActivity.class));
						MainActivity.this.finish();
					} catch (InterruptedException ex) {
						Toast.makeText(
								MainActivity.this.getApplicationContext(),
								"Unexpected error: " + ex.getMessage(),
								Toast.LENGTH_SHORT).show();
						Log.e("init", "Unexpected error: " + ex.getMessage(),
								ex);
					}
				}
			});
			// tcopy.start();
			// tsplash.start();
		} catch (Exception ex) {
			Toast.makeText(MainActivity.this.getApplicationContext(),
					"Unexpected error: " + ex.getMessage(), Toast.LENGTH_SHORT)
					.show();
			Log.e("init", "Unexpected error: " + ex.getMessage(), ex);
		}

		MainActivity.this.startActivity(new Intent(MainActivity.this
				.getApplicationContext(), ModelActivity.class));
		MainActivity.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private void installExamples() {

		if (!Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState()))
			return;

		copyAssets("models");
		copyAssets("textures");
	}

	private void copyAssets(String sourceDirectory) {
		OutputStream fileOutputStream = null;
		try {
			String[] files = getAssets().list(sourceDirectory);
			if (ArrayUtils.isEmpty(files)) {
				throw new IllegalStateException(
						"No se han encontrado los ficheros fuente de instalación");
			}
			File targetDir = new File(Environment.getExternalStorageDirectory()
					// TODO: asegurarnos que el directorio no colisione con otra
					// aplicación
					+ File.separator + "3DModelViewer2013" + File.separator
					+ sourceDirectory);
			if (!targetDir.exists()) {
				Log.d("copyAssets", "Creación del directorio '" + targetDir
						+ "' retorno '" + targetDir.mkdirs() + "'");
			}
			for (String file : files) {
				File sdFile = new File(targetDir, file);
				if (sdFile.exists()) {
					Log.d("copyAssets", "Fichero '" + sdFile
							+ "' ya existe. Continuando...");
					continue;
				}
				sdFile.createNewFile();
				fileOutputStream = new BufferedOutputStream(
						new FileOutputStream(sdFile));
				Log.d("copyAssets", "Copiando fichero '" + file + "' a '"
						+ sdFile + "'...");
				InputStream assetStream = getAssets().open(
						sourceDirectory + File.separator + file);
				IOUtils.copy(new BufferedInputStream(assetStream),
						fileOutputStream);
				assetStream.close();
				fileOutputStream.close();
			}
			Log.i("copyAssets", "Copiado directorio de assets '"
					+ sourceDirectory + "'");
		} catch (IOException ex) {
			Log.e("copyAssets",
					"Se ha producido una exepción copiando el directorio de assets '"
							+ sourceDirectory + "'", ex);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ex) {
					Log.e("copyAssets",
							"Se ha producido una exepción copiando ficheros desde '"
									+ sourceDirectory + "'", ex);
				}
			}
		}

	}
}
