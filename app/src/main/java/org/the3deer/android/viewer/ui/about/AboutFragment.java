package org.the3deer.android.viewer.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.the3deer.android.viewer.R;
import org.the3deer.android.viewer.databinding.FragmentAboutBinding;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AboutFragment extends Fragment {

    private static final Logger logger = Logger.getLogger(AboutFragment.class.getSimpleName());

    private FragmentAboutBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Link to Website
        binding.linkWebsite.setOnClickListener(v -> {
            openUrl(getString(R.string.about_link_website_url));
        });

        // Link to Source Code (GitHub)
        binding.linkGithub.setOnClickListener(v -> {
            openUrl(getString(R.string.about_link_source_url));
        });

        // Link to Report an Issue
        binding.linkIssues.setOnClickListener(v -> {
            openUrl(getString(R.string.about_link_issues_url));
        });
    }

    /**
     * Triggers an intent to open the URL in an external browser
     */
    private void openUrl(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            logger.log(Level.SEVERE,  "Error opening URL: " + url + ". message: " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
