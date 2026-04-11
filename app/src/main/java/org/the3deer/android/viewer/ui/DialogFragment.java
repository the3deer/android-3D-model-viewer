package org.the3deer.android.viewer.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

public class DialogFragment extends androidx.fragment.app.DialogFragment implements DialogInterface.OnClickListener {

    // params
    protected int title;
    protected String[] items;

    // variables
    protected FragmentActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = getActivity();
        this.title = getArguments().getInt("title");
        if (getArguments().containsKey("itemsId")){
            this.items = getResources().getStringArray(getArguments().getInt("itemsId"));
        } else {
            this.items = getArguments().getStringArray("items");
        }

        // this fragment will be displayed in a dialog
        //setShowsDialog(true);
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return super.onCreateDialog(savedInstanceState);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                //.setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                /*.setPositiveButton(R.string.dialog_ok,
                        (dialog, whichButton) -> onOk()
                )
                .setNegativeButton(R.string.dialog_cancel,
                        (dialog, whichButton) -> onCancel()
                )*/
                .setItems(items, this);


        return builder
                .create();
    }

    protected void onOk(){

    }

    protected void onCancel(){

    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {
    }

}