package org.andresoviedo.util.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by coco on 6/7/15.
 */
public class AssetUtils {

    @FunctionalInterface
    public interface Callback {
        void onClick(String asset);
    }

    public static void createChooserDialog(Context context, String title, CharSequence message, String folder,
                                           String fileRegex, Callback callback) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", (DialogInterface dialog, int which) -> {
            callback.onClick(null);
        });
        try {
            final String[] fileList = listFiles(context, folder, fileRegex);
            builder.setItems(fileList, (DialogInterface dialog, int which) -> {
                String selectedFile = fileList[which];
                callback.onClick(folder+"/"+selectedFile);
            });
        } catch (IOException ex) {
            Toast.makeText(context,"Error listing assets from "+folder, Toast.LENGTH_LONG).show();
        }
        builder.create().show();
    }

    private static String[] listFiles(Context context, String folder, String fileRegex) throws IOException {
        List<String> ret = new ArrayList<>();
        String[] list = context.getAssets().list(folder);
        for (String asset : list){
            if (asset.matches(fileRegex)) {
                ret.add(asset);
            }
        }
        return ret.toArray(new String[0]);
    }

    private static String[] getFilenames(File upLevelFile, List<File> files) {
        String[] filenames = new String[files.size()];
        for (int i = 0; i < files.size(); i++) {
            if (files.get(i) == null || upLevelFile == files.get(i)) {
                filenames[i] = "..";
            } else {
                filenames[i] = files.get(i).getName();
            }
        }
        return filenames;
    }

}