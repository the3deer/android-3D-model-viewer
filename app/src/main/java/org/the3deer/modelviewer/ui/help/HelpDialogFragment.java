package org.the3deer.modelviewer.ui.help;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.the3deer.modelviewer.R;
import org.the3deer.modelviewer.ui.DialogFragment;
import org.the3deer.util.android.ContentUtils;

public class HelpDialogFragment extends DialogFragment {

    private enum Action {
        DEMOS, HELP, ABOUT
    }

    public static HelpDialogFragment newInstance(int title, String[] items) {
        HelpDialogFragment frag = new HelpDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArray("items", items);
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
                            Bundle args = new Bundle();
                            args.putString("action", "navigate");
                            args.putInt("view", R.id.nav_demo1);
                            activity.getSupportFragmentManager().setFragmentResult("app", args);
                        }else if (which == 1) {
                            Bundle args = new Bundle();
                            args.putString("action", "navigate");
                            args.putInt("view", R.id.nav_demo2);
                            activity.getSupportFragmentManager().setFragmentResult("app", args);
                        } else if (which == 2) {
                            Bundle args = new Bundle();
                            args.putString("action", "navigate");
                            args.putInt("view", R.id.nav_demo3);
                            activity.getSupportFragmentManager().setFragmentResult("app", args);
                        }
                    });
                    break;
                case HELP:
                    Bundle args = new Bundle();
                    args.putString("action", "navigate");
                    args.putInt("view", R.id.nav_help_fragment);
                    activity.getSupportFragmentManager().setFragmentResult("app", args);
                    break;
                case ABOUT:
                    Bundle args2 = new Bundle();
                    args2.putString("action", "navigate");
                    args2.putInt("view", R.id.nav_about);
                    activity.getSupportFragmentManager().setFragmentResult("app", args2);
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