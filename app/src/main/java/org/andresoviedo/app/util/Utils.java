package org.andresoviedo.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.util.Log;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * General purpose android utilities
 * 
 * @author andresoviedo
 *
 */
public final class Utils {

	/**
	 * Copy all assets found in the jar folder to the specified directory
	 * 
	 * @param sourceDirectory
	 */
	public static void copyAssets(Context context, String sourceDirectory, File targetDir) {
		OutputStream fileOutputStream = null;
		try {
			AssetManager assets = context.getAssets();
			String[] files = assets.list(sourceDirectory);
			if (files == null || files.length == 0) {
				throw new IllegalStateException("No se han encontrado los ficheros fuente de instalación");
			}
			if (!targetDir.exists()) {
				boolean ret = targetDir.mkdirs();
				Log.d("copyAssets", "Creación del directorio '" + targetDir + "' retorno '" + ret + "'");
			}
			for (String file : files) {
				File sdFile = new File(targetDir, file);
				if (sdFile.exists()) {
					Log.d("copyAssets", "Fichero '" + sdFile + "' ya existe. Continuando...");
					continue;
				}
				sdFile.createNewFile();
				fileOutputStream = new BufferedOutputStream(new FileOutputStream(sdFile));
				Log.d("copyAssets", "Copiando fichero '" + file + "' a '" + sdFile + "'...");
				InputStream assetStream = assets.open(sourceDirectory + File.separator + file);
				IOUtils.copy(new BufferedInputStream(assetStream), fileOutputStream);
				assetStream.close();
				fileOutputStream.close();
			}
			Log.i("copyAssets", "Copiado directorio de assets '" + sourceDirectory + "'");
		} catch (IOException ex) {
			Log.e("copyAssets",
					"Se ha producido una exepción copiando el directorio de assets '" + sourceDirectory + "'", ex);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException ex) {
					Log.e("copyAssets",
							"Se ha producido una exepci�n copiando ficheros desde '" + sourceDirectory + "'", ex);
				}
			}
		}

	}

	public static void printTouchCapabilities(PackageManager packageManager) {
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH)) {
			Log.i("utils", "System supports multitouch (2 fingers)");
		}
		if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT)) {
			Log.i("utils", "System supports advanced multitouch (multiple fingers). Cool!");
		}
	}

	/**
	 * Get the Intent for selecting content to be used in an Intent Chooser.
	 * 
	 * @return The intent for opening a file with Intent.createChooser()
	 * 
	 * @author paulburke
	 */
	public static Intent createGetContentIntent() {
		// Implicitly allow the user to select a particular kind of data
		final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		// The MIME data type filter
		intent.setType("*/*");
		// Only return URIs that can be opened with ContentResolver
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		return intent;
	}

}
