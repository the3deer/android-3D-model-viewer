package org.the3deer.android.viewer.ui.legacy;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceGroup;

import java.util.Map;

public interface PreferenceAdapter {

    default void onRestoreInstanceState(Bundle state) {
    }

    default void onSaveInstanceState(Bundle outState){
    }

    default void onRestorePreferences(@Nullable Map<String,?> preferences){
    }

    default void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey, Context context, PreferenceGroup screen){
    }
}
