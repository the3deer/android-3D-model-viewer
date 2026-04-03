package org.the3deer.android.viewer.ui;

import static org.the3deer.engine.android.util.ContentUtils.documentsProvided;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.the3deer.android.viewer.R;
import org.the3deer.engine.android.util.AssetUtils;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DialogUtils {

    private static final Logger logger = Logger.getLogger(DialogUtils.class.getSimpleName());

    public static AlertDialog.Builder createChooserDialog(Context context, String title, CharSequence message,
                                                          List<String> fileListAssets, String fileRegex, AssetUtils.Callback callback) {
        return createChooserDialog(context, title, message, fileListAssets, null, fileRegex, callback);
    }

    public static AlertDialog.Builder createChooserDialog(Context context, String title, CharSequence message,
                                                          List<String> fileListAssets, Map<String, byte[]> icons,
                                                          String fileRegex, AssetUtils.Callback callback) {

        final List<String> fileListModels = new ArrayList<>();
        for (String file : fileListAssets) {
            if (file.matches(fileRegex)) {
                fileListModels.add(file);
            }
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setNegativeButton("Cancel", (DialogInterface dialog, int which) -> callback.onClick(null));

        if (icons == null) {
            final String[] fileListArray = new String[fileListModels.size()];
            for (int i = 0; i < fileListModels.size(); i++) {
                final String url = fileListModels.get(i);
                fileListArray[i] = url.substring(url.lastIndexOf('/') + 1);
            }
            builder.setItems(fileListArray, (DialogInterface dialog, int which) -> {
                documentsProvided.clear();
                for (String asset : fileListAssets) {
                    try {
                        documentsProvided.put(asset.substring(asset.lastIndexOf("/") + 1), URI.create(asset));
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                callback.onClick(fileListModels.get(which));
            });
        } else {
            final ArrayList<Map<String, Object>> modelList = new ArrayList<>();
            for (int i = 0; i < fileListModels.size(); i++) {
                final String url = fileListModels.get(i);
                final String filename = url.substring(url.lastIndexOf('/') + 1);
                final String label = filename.endsWith(".index") ? filename.substring(0, filename.length() - 6) + "..." : filename;
                final String icon = filename + ".jpg";
                final String icon2 = filename + ".png";
                final Map<String, Object> hashMap = new HashMap<>();
                hashMap.put("name", label);
                if (icons.containsKey(icon)) {
                    hashMap.put("image", String.valueOf(i));
                    hashMap.put("bitmap", BitmapFactory.decodeStream(new ByteArrayInputStream(icons.get(icon))));
                } else if (icons.containsKey(icon2)) {
                    hashMap.put("image", String.valueOf(i));
                    hashMap.put("bitmap", BitmapFactory.decodeStream(new ByteArrayInputStream(icons.get(icon2))));
                }
                hashMap.put("url", url);
                modelList.add(hashMap);
            }

            final String[] from = {"name", "image"};
            final int[] to = {R.id.textView, R.id.imageView};
            final ListAdapter textImageAdapter = new SimpleAdapter(context, modelList, R.layout.list_text_with_image, from, to) {
                @Override
                public void setViewImage(ImageView v, String value) {
                    v.setImageBitmap(null);
                    if (value != null && value.length() > 0) {
                        try {
                            v.setImageBitmap((Bitmap) modelList.get(Integer.parseInt(value)).get("bitmap"));
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE,  ex.getMessage(), ex);
                        }
                    }
                }
            };
            builder.setAdapter(textImageAdapter, (DialogInterface dialog, int which) -> {
                documentsProvided.clear();
                for (String asset : fileListAssets) {
                    try {
                        documentsProvided.put(asset.substring(asset.lastIndexOf("/") + 1), URI.create(asset));
                    } catch (Exception e) {
                        logger.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
                callback.onClick((String) modelList.get(which).get("url"));
            });
        }

        return builder;
    }
}
