package org.the3deer.android.viewer.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.the3deer.engine.android.util.AndroidUtils;
import org.the3deer.android.viewer.ui.load.LoadDialogFragment;
import org.the3deer.android.viewer.R;

public class MainDialogFragment extends DialogFragment {

    private enum Action {
        LOAD_MODEL, HELP, EXIT,
        SETTINGS, DEMO, GITHUB // not enabled
    }

    public static MainDialogFragment newInstance(int title, String[] array) {
        MainDialogFragment frag = new MainDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArray("items", array);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {
        try {
            Action action = Action.values()[position];
            switch (action) {
                case LOAD_MODEL:
                    DialogFragment newFragment = LoadDialogFragment.newInstance(R.string.dialog_load_title,
                            getResources().getStringArray(R.array.dialog_load_from));
                    newFragment.show(activity.getSupportFragmentManager(), "dialog");
                    break;
                case DEMO:
                    /*HomeFragment modelFragment = HomeFragment.newInstance(null, null, false);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.nav_host_fragment_content_main, modelFragment, "demo")
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();
                    dismiss();*/
                    break;
                case GITHUB:
                    AndroidUtils.openUrl(getActivity(), "https://github.com/the3deer/android-3D-model-viewer");
                    break;
                case HELP:
                    final Bundle result = new Bundle();
                    result.putString("action", "help");
                    activity.getSupportFragmentManager().setFragmentResult("app", result);
                    dismiss();
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
            Log.e("MainDialogFragment",ex.getMessage(),ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}