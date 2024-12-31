package org.the3deer.app.model3D.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.andresoviedo.dddmodel2.R;
import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.app.model3D.demo.EarCutDemoFragment;
import org.the3deer.app.model3D.demo.GlyphsDemoFragment;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.DialogFragment;
import org.the3deer.util.view.TextActivity;

public class HelpDialogFragment extends DialogFragment {

    private enum Action {
        DEMOS, HELP, ABOUT
    }

    public static HelpDialogFragment newInstance(int title) {
        HelpDialogFragment frag = new HelpDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("items", R.array.dialog_help_items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {
        try {
            Action action = Action.values()[position];
            switch (action) {
                case DEMOS:
                    ContentUtils.showListDialog(activity, "Demos List", new String[]{"Simple Objects", "GUI", "Geometry"}, (DialogInterface dialog, int which) -> {
                        if (which == 0) {
                            ModelFragment modelFragment2 = ModelFragment.newInstance(null, null, true);
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_container, modelFragment2, "demo_0")
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                            dismiss();
                        } else if (which == 1) {
                            ModelFragment modelFragment2 = new GlyphsDemoFragment();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_container, modelFragment2, "demo_1")
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                            dismiss();
                        } else if (which == 2) {
                            ModelFragment modelFragment2 = new EarCutDemoFragment();
                            activity.getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.main_container, modelFragment2, "demo_2")
                                    .setReorderingAllowed(true)
                                    .addToBackStack(null)
                                    .commit();
                            dismiss();
                        }
                    });
                    break;
                case HELP:
                    Intent helpIntent = new Intent(activity, TextActivity.class);
                    helpIntent.putExtra("title", items[position]);
                    helpIntent.putExtra("text", getResources().getString(R.string.help_text));
                    startActivity(helpIntent);
                    break;
                case ABOUT:
                    Intent aboutIntent = new Intent(activity, TextActivity.class);
                    aboutIntent.putExtra("title", items[position]);
                    aboutIntent.putExtra("text", getResources().getString(R.string.about_text));
                    startActivity(aboutIntent);
                    break;
                default:
                    Toast.makeText(activity, "Unrecognized action '" + action + "'",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception ex) {
            Log.e("MainDialogFragment",ex.getMessage(),ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}