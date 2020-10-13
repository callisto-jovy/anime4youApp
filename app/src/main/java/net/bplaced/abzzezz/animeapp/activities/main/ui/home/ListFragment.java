/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 15:16
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Optional;

public class ListFragment extends Fragment {

    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private AnimeAdapter animeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AnimeAppMain.getInstance().checkPermission(getActivity());
        final View root = inflater.inflate(R.layout.list_fragment_layout, container, false);

        final GridView gridView = root.findViewById(R.id.anime_grid);
        this.animeAdapter = new AnimeAdapter(AnimeAppMain.getInstance().getShowSaver().getShowSize(), getActivity());

        gridView.setAdapter(animeAdapter);
        /*
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> getInformation(position, new Intent(getActivity(), SelectedActivity.class)));
        /*
         * Set on long hold listener, then block the old one
         */
        gridView.setOnItemLongClickListener((parent, view, position, id) -> {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Remove file?")
                    .setContentText("Won't be able to recover this file!")
                    .setConfirmText("Yes,delete it!")
                    .setConfirmClickListener(ionAlert -> {
                        animeAdapter.removeItem(position);
                        ionAlert.dismissWithAnimation();
                    }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });

        final FloatingActionButton addButton = root.findViewById(R.id.add_aid);
        addButton.setOnClickListener(v -> {
            final InputDialogBuilder inputDialogBuilder = new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
                @Override
                public void onDialogInput(String text) {
                    animeAdapter.addItem(text);
                }

                @Override
                public void onDialogDenied() {
                }
            });
            inputDialogBuilder.showInput("Enter AID", "Enter AID to add anime", getActivity());
        });

        if (AnimeAppMain.getInstance().isVersionOutdated()) {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Outdated version")
                    .setContentText("Your app-version is outdated, please update it now!")
                    .setCancelText("Close").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
        }
        return root;
    }

    /**
     * Get shows information from index
     *
     * @param index  show index
     * @param intent intent
     */
    private void getInformation(final int index, final Intent intent) {
        final Optional<JSONObject> savedInformation = AnimeAppMain.getInstance().getShowSaver().getShow(index);

        if (!StringHandler.isOnline(getActivity().getApplicationContext()) && savedInformation.isPresent()) {
            startActivity(intent.putExtra("details", savedInformation.get().toString()));
            getActivity().finish();
            return;
        }

        savedInformation.ifPresent(jsonObject -> new TaskExecutor().executeAsync(new DataBaseTask(jsonObject, dataBaseSearch), new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(JSONObject result) {
                AnimeAppMain.getInstance().getShowSaver().refreshShow(result, index);
                startActivity(intent.putExtra("details", result.toString()));
                getActivity().finish();
            }

            @Override
            public void preExecute() {
                Logger.log("Fetching anime information", Logger.LogType.INFO);
            }
        }));
    }

    class AnimeAdapter extends BaseAdapter {

        private final Context context;
        private int size;

        public AnimeAdapter(int size, final Context context) {
            this.size = size;
            this.context = context;
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            return AnimeAppMain.getInstance().getShowSaver().getShow(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ImageView coverImage = new ImageView(context);
            try {
                final Optional<JSONObject> optionalJSONObject = AnimeAppMain.getInstance().getShowSaver().getShow(position);
                if (optionalJSONObject.isPresent()) {
                    final String imageURL = optionalJSONObject.get().getString(StringHandler.SHOW_IMAGE_URL);

                    if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false)) {
                        OfflineImageLoader.loadImage(imageURL, optionalJSONObject.get().getString(StringHandler.SHOW_ID), coverImage, getContext());
                    } else {
                        Picasso.with(context).load(imageURL).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(coverImage);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            coverImage.setAdjustViewBounds(true);
            return coverImage;
        }

        public void removeItem(final int index) {
            final Optional<JSONObject> itemToRemove = (Optional<JSONObject>) getItem(index);
            if (itemToRemove.isPresent()) {
                this.size--;
                try {

                    final File dir = new File(getActivity().getFilesDir(), itemToRemove.get().getString(StringHandler.SHOW_TITLE));
                    if (dir.listFiles() != null && dir.listFiles().length > 0) {
                        new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                                .setTitleText("Delete all remaining episodes?")
                                .setContentText("Won't be able to recover the files!")
                                .setConfirmText("Yes, delete!")
                                .setConfirmClickListener(ionAlert -> {
                                    for (final File file : dir.listFiles()) {
                                        Logger.log("Deleting file: " + file.delete(), Logger.LogType.INFO);
                                    }
                                    Toast.makeText(context, "Remaining files deleted.", Toast.LENGTH_SHORT).show();
                                    ionAlert.dismissWithAnimation();
                                }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                                .show();
                    }
                } catch (final JSONException e) {
                    e.printStackTrace();
                }
                AnimeAppMain.getInstance().getShowSaver().remove(index);
                notifyDataSetChanged();
            }
        }

        public void addItem(final String item) {
            if (!StringHandler.isOnline(getActivity())) {
                Toast.makeText(context, "You are currently not connected to the internet", Toast.LENGTH_LONG).show();
                return;
            }
            //Create new database request. get episodes, imageURL, name
            new TaskExecutor().executeAsync(new DataBaseTask(item, dataBaseSearch), new TaskExecutor.Callback<JSONObject>() {
                @Override
                public void onComplete(final JSONObject result) throws Exception {
                    size++;
                    AnimeAppMain.getInstance().getShowSaver().addShow(result);
                    notifyDataSetChanged();
                }

                @Override
                public void preExecute() {
                }
            });
        }
    }
}
