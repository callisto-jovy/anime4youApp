/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;

import java.io.File;

public class SettingsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.settings_layout, container, false);
        getParentFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragmentInner()).commit();
        return root;
    }

    public static class SettingsFragmentInner extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            Preference clearOfflineImages = findPreference("clear_offline_images_button");
            clearOfflineImages.setOnPreferenceClickListener(preference -> {
                new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                        .setTitleText("Delete all offline images?")
                        .setContentText("If you are offline and there are no caches images wont be loaded!")
                        .setConfirmText("Yes, delete!")
                        .setConfirmClickListener(ionAlert -> {
                            for (File imageFile : AnimeAppMain.getInstance().getImageStorage().listFiles())
                                Logger.log("Deleting file: " + imageFile.getName() + "- Success: " + imageFile.delete(), Logger.LogType.INFO);
                            ionAlert.dismissWithAnimation();
                        }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                        .show();
                return true;
            });
            /*
            Preference manageAnimeNotifications = findPreference("manage_anime_notifications");
            Fragment newFragment = new AnimeNotificationsFragment();
            manageAnimeNotifications.setOnPreferenceClickListener(preference -> {
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.simple_list_layout, newFragment).commit();
                return true;
            });

             */
        }
    }

}