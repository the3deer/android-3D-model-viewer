package org.the3deer.android.viewer.ui.load;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import org.the3deer.android.viewer.SharedViewModel;
import org.the3deer.android.viewer.providers.ProviderInfo;
import org.the3deer.android.viewer.ui.DialogFragment;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoadDialogFragment extends DialogFragment {

    private static final Logger logger = Logger.getLogger(LoadDialogFragment.class.getSimpleName());

    private SharedViewModel sharedViewModel;

    public static LoadDialogFragment newInstance(int title, String[] items) {
        LoadDialogFragment frag = new LoadDialogFragment();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putStringArray("items", items);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        List<ProviderInfo> providers = sharedViewModel.getProviderManager().getProviderList();
        this.items = new String[providers.size()];
        for (int i = 0; i < providers.size(); i++) {
            this.items[i] = getString(providers.get(i).getTitleResId());
        }
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onClick(DialogInterface dialogI, int position) {
        List<ProviderInfo> providers = sharedViewModel.getProviderManager().getProviderList();
        if (position < 0 || position >= providers.size()) return;

        ProviderInfo providerInfo = providers.get(position);

        if (providerInfo.getAutoDismiss()) {
            dismiss();
        }

        try {
            sharedViewModel.getProviderManager().load(providerInfo.getId(), activity, (model) -> {
                if (model != null) {
                    dismiss();
                    sharedViewModel.getApi().loadModel(model);
                }
            });
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            Toast.makeText(activity, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
