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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class SettingsFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.settings_layout, container, false);
        getParentFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragmentInner()).commit();
        return root;
    }

    public static class SettingsFragmentInner extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            final Preference clearOfflineImages = findPreference("clear_offline_images_button");
            final Preference clearTacker = findPreference("clear_tracker");
            final Preference copySdCard = findPreference("copy_sd_card");

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

            clearTacker.setOnPreferenceClickListener(preference -> {
                new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                        .setTitleText("Clear download tracker?")
                        .setContentText("Your log will be deleted.")
                        .setConfirmText("Yes, delete!")
                        .setConfirmClickListener(ionAlert -> {
                            AnimeAppMain.getInstance().getDownloadTracker().clearTrack();
                            ionAlert.dismissWithAnimation();
                        }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                        .show();
                return true;
            });

            copySdCard.setOnPreferenceClickListener(preference -> {
                new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                        .setTitleText("Move files?")
                        .setConfirmText("Move !")
                        .setConfirmClickListener(ionAlert -> {
                            moveFiles();
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

        private void moveFiles() {
            final File sdCard = getActivity().getExternalCacheDir();
            final File externalShowDir = new File(sdCard, "Shows");
            if (!externalShowDir.exists()) externalShowDir.mkdir();
            if (sdCard.exists()) {
                Toast.makeText(getContext(), "Starting file transfer... This could take some time", Toast.LENGTH_SHORT).show();
                new TaskExecutor().executeAsync(() -> {
                    if (getActivity().getFilesDir().listFiles() != null) {
                        for (File dir : getActivity().getFilesDir().listFiles()) {
                            if (dir.isDirectory() && !(dir.listFiles().length == 0)) {
                                final File newDir = new File(externalShowDir, dir.getName());
                                if (!newDir.exists()) newDir.mkdir();
                                for (File innerFiles : dir.listFiles()) {
                                    try {
                                        Files.move(innerFiles.toPath(), new File(newDir, innerFiles.getName()).toPath());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }, new TaskExecutor.Callback<R>() {
                    @Override
                    public void onComplete(R result) throws Exception {
                        Toast.makeText(getContext(), "Done transferring files.", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void preExecute() {

                    }
                });
            }
        }
    }

}