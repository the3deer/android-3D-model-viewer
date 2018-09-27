package org.andresoviedo.util.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by coco on 6/7/15.
 */
public class FileUtils {

    @FunctionalInterface
    public interface Callback {
        void onClick(File file);
    }

    /* Checks if external storage is available for read and write */
    /*public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }*/

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void createChooserDialog(Context context, String title, CharSequence message, File folder,
                                           String fileRegex, Callback callback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {
            callback.onClick(null);
        });
        final List<File> fileList = listFiles(folder, fileRegex);
        final File parentFile = addParentFile(folder, fileList);
        builder.setItems(getFilenames(parentFile, fileList), (DialogInterface dialog, int which) -> {
            File selectedFile = fileList.get(which);
            if (selectedFile == null || selectedFile.isDirectory()) {
                createChooserDialog(context, title, message, selectedFile, fileRegex, callback);
            } else {
                callback.onClick(selectedFile);
            }
        });
        builder.create().show();
    }

    private static File addParentFile(File folder, List<File> fileList) {
        File parentFile = null;
        if (folder != null) {
            if (!(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).equals(folder) ||
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).equals(folder) ||
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).equals(folder) ||
                    (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).equals
                                    (folder)) ||
                    (isExternalStorageReadable() && Environment.getExternalStorageDirectory().equals(folder)))) {
                parentFile = folder.getParentFile();
            }
            fileList.add(0, parentFile);
        }
        return parentFile;
    }

    private static List<File> listFiles(File folder, String fileRegex) {
        List<File> ret = new ArrayList<>();
        if (folder == null) {
            ret.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS));
            ret.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
            ret.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                ret.add(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS));
            }
            if (isExternalStorageReadable()) {
                ret.add(Environment.getExternalStorageDirectory());

            }

            // root
            ret.add(new File("/"));

            // check directory exists
            for (Iterator<File> it = ret.iterator(); it.hasNext(); ) {
                if (!it.next().exists()) {
                    it.remove();
                }
            }
        } else {
            Log.i("FileUtils","Listing files... "+folder.getAbsolutePath());
            try {
                File[] children = folder.listFiles(file ->
                        !file.isHidden() && file.exists() && (file.isDirectory() || file.getName().matches(fileRegex)));
                if (children != null) {
                    ret.addAll(Arrays.asList(children));
                }
            } catch (Exception e) {
                Log.e("FileUtils","Error opening directory "+folder.getAbsolutePath());
            }
        }
        Collections.sort(ret, (o1, o2) -> o1.getName().compareToIgnoreCase(o2.getName()));
        return ret;
    }

    private static String[] getFilenames(File upLevelFile, List<File> files) {
        String[] filenames = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) == null || upLevelFile == files.get(i)) {
                filenames[i] = "..";
            } else {
                filenames[i] = files.get(i).getName();
                if ("".equals(filenames[i])){
                    filenames[i] = "/";  // root folder?
                }
            }
        }
        return filenames;
    }
}