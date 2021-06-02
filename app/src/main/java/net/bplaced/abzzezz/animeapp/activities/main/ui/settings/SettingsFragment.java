/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 18:09
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.settings;

import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceFragmentCompat;
import com.afollestad.materialdialogs.MaterialDialog;
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
        final View root = inflater.inflate(R.layout.fragment_settings, container, false);
        getParentFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragmentInner()).commit();
        return root;
    }

    public static class SettingsFragmentInner extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            findPreference("storage_clear_cache").setOnPreferenceClickListener(preference -> {
                new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                        .setTitleText(getString(R.string.offline_images_delete_all_title))
                        .setContentText(getString(R.string.offline_images_delete_all_content))
                        .setConfirmText(getString(R.string.offline_images_delete_all_confirm))
                        .setConfirmClickListener(ionAlert -> {
                            if (AnimeAppMain.getInstance().getImageStorage().exists() && AnimeAppMain.getInstance().getImageStorage().listFiles() != null) {
                                for (final File imageFile : AnimeAppMain.getInstance().getImageStorage().listFiles())
                                    Logger.log("Deleting file: " + imageFile.getName() + "- Success: " + imageFile.delete(), Logger.LogType.INFO);
                                ionAlert.dismissWithAnimation();
                            }
                        }).setCancelText(getString(R.string.abort))
                        .setCancelClickListener(IonAlert::dismissWithAnimation)
                        .show();
                return true;
            });

            findPreference("sync_mal").setOnPreferenceChangeListener((preference, v) -> {
                final boolean[] newValue = {(boolean) v};

                if (newValue[0]) {

                    new MaterialDialog.Builder(getContext())
                            .title("Myanimelist credentials")
                            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                            .customView(R.layout.dialog_two_input_password, false)
                            .positiveText("Sync")
                            .onPositive((dialog, which) -> {
                                assert dialog.getCustomView() != null : "Custom view is null";

                                final EditText usernameEditText = dialog.getCustomView().findViewById(R.id.dialog_two_input_username);
                                final EditText password = dialog.getCustomView().findViewById(R.id.dialog_two_input_password);
                                final String username = usernameEditText.getText().toString().trim();

                                AnimeAppMain.getInstance().getMyAnimeList().setupSync(username, password.getText().toString(), processFinished -> {
                                    if (processFinished) {
                                        new IonAlert(getActivity(), IonAlert.SUCCESS_TYPE)
                                                .setTitleText("Sync done")
                                                .show();

                                    } else {
                                        new IonAlert(getActivity(), IonAlert.SUCCESS_TYPE)
                                                .setTitleText("Sync was not successful")
                                                .setConfirmText("Done")
                                                .show();
                                        newValue[0] = false;

                                    }
                                });
                            }).show();
                }
                return true;
            });

            findPreference("copy_sd_card").setOnPreferenceClickListener(preference -> {
                new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                        .setTitleText(getString(R.string.move_files_title))
                        .setConfirmText(getString(R.string.move_files_confirm))
                        .setConfirmClickListener(ionAlert -> {
                            moveFiles();
                            ionAlert.dismissWithAnimation();
                        }).setCancelText(getString(R.string.abort))
                        .setCancelClickListener(IonAlert::dismissWithAnimation)
                        .show();
                return true;
            });
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
                    public void onComplete(R result) {
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