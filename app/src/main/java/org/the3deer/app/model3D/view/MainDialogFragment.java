package org.the3deer.app.model3D.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import org.andresoviedo.dddmodel2.R;
import org.the3deer.android_3d_model_engine.ModelFragment;
import org.the3deer.app.model3D.demo.EarCutDemoFragment;
import org.the3deer.app.model3D.demo.GlyphsDemoFragment;
import org.the3deer.util.android.AndroidUtils;
import org.the3deer.util.android.ContentUtils;
import org.the3deer.util.android.DialogFragment;
import org.the3deer.util.view.TextActivity;

public class MainDialogFragment extends DialogFragment {

    private enum Action {
        LOAD_MODEL, DEMOS, HELP, ABOUT, EXIT,
        SETTINGS, DEMO, GITHUB // not enabled
    }

    public static MainDialogFragment newInstance(int title) {
        MainDialogFragment frag = new MainDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putInt("items", R.array.dialog_menu_items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {
        try {
            Action action = Action.values()[position];
            switch (action) {
                case LOAD_MODEL:
                    DialogFragment newFragment = LoadDialogFragment.newInstance(R.string.alert_dialog_title);
                    newFragment.show(getParentFragmentManager(), "dialog");
                    break;
                case DEMO:
                    ModelFragment modelFragment = ModelFragment.newInstance(null, null, false);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.main_container, modelFragment, "demo")
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    dismiss();
                    break;
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

                case GITHUB:
                    AndroidUtils.openUrl(getActivity(), "https://github.com/the3deer/android-3D-model-viewer");
                    break;

                case ABOUT:
                    Intent aboutIntent = new Intent(activity, TextActivity.class);
                    aboutIntent.putExtra("title", items[position]);
                    aboutIntent.putExtra("text", getResources().getString(R.string.about_text));
                    startActivity(aboutIntent);
                    break;
                case HELP:
                    Intent helpIntent = new Intent(activity, TextActivity.class);
                    helpIntent.putExtra("title", items[position]);
                    helpIntent.putExtra("text", getResources().getString(R.string.help_text));
                    startActivity(helpIntent);
                    break;
                case SETTINGS:
                    break;
                case EXIT:
                    activity.finishAffinity();
                    break;
                default:
                    Toast.makeText(activity, "Unrecognized action '" + action + "'",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        } catch (Exception ex) {
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}