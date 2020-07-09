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
import net.bplaced.abzzezz.animeapp.activities.main.SelectedAnimeActivity;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.DataBaseSearch;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.tasks.DataBaseTask;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AnimeListFragment extends Fragment {

    private final DataBaseSearch dataBaseSearch = new DataBaseSearch();
    private AnimeAdapter animeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //
        AnimeAppMain.getInstance().checkRequest(getActivity());

        View root = inflater.inflate(R.layout.anime_list_layout, container, false);
        GridView gridView = root.findViewById(R.id.anime_grid);
        this.animeAdapter = new AnimeAdapter(AnimeAppMain.getInstance().getAnimeSaver().getList(), getActivity());
        gridView.setAdapter(animeAdapter);
        /*
         * Set onclick listener, if clicked pass information through to selected anime.
         */
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                getInformation(AnimeAppMain.getInstance().getAnimeSaver().getAll(position), new Intent(getActivity(), SelectedAnimeActivity.class));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
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

        FloatingActionButton addAidButton = root.findViewById(R.id.add_aid);
        addAidButton.setOnClickListener(v -> {
            InputDialogBuilder inputDialogBuilder = new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
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

        return root;
    }


    private void getInformation(JSONObject savedInformation, Intent intent) throws JSONException {
        if (!StringHandler.isOnline(getActivity().getApplicationContext())) {
            intent.putExtra("anime_details", savedInformation.toString());
            startActivity(intent);
            getActivity().finish();
            return;
        }

        new TaskExecutor().executeAsync(new DataBaseTask(savedInformation.getString("id"), dataBaseSearch), new TaskExecutor.Callback<JSONObject>() {
            @Override
            public void onComplete(JSONObject result) {
                intent.putExtra("anime_details", result.toString());
                startActivity(intent);
                getActivity().finish();
            }

            @Override
            public void preExecute() {
                Logger.log("Fetching anime information", Logger.LogType.INFO);
            }
        });
    }

    class AnimeAdapter extends BaseAdapter {

        private final Context context;
        private final List<String> string;

        public AnimeAdapter(List<String> string, Context context) {
            this.string = string;
            this.context = context;
        }

        @Override
        public int getCount() {
            return string.size();
        }

        @Override
        public Object getItem(int position) {
            return string.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            try {
                JSONObject jsonObject = new JSONObject(string.get(position));
                String imageURL = jsonObject.getString("image_url");
                //If offline mode is enabled, load offline bitmap into imageview
                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false)) {
                    OfflineImageLoader.loadImage(imageURL, jsonObject.getString("id"), imageView, getContext());
                } else {
                    //Load image from url into imageview using picasso. (Cache images)
                    try {
                        Picasso.with(context).load(imageURL).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(imageView);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        System.out.println(position);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //Set view bounds
            imageView.setAdjustViewBounds(true);
            return imageView;
        }

        public void removeItem(final int index) {
            string.remove(index);
            AnimeAppMain.getInstance().getAnimeSaver().remove(index);
            notifyDataSetChanged();
        }

        public void addItem(final String item) {
            if (!StringHandler.isOnline(getActivity())) {
                Toast.makeText(context, "You are currently not connected to the internet, returning", Toast.LENGTH_LONG).show();
                return;
            }
            //Create new database request. get episodes, imageURL, name
            new TaskExecutor().executeAsync(new DataBaseTask(item, dataBaseSearch), new TaskExecutor.Callback<JSONObject>() {
                @Override
                public void onComplete(JSONObject result) throws Exception {
                    string.add(result.toString());
                    AnimeAppMain.getInstance().getAnimeSaver().add(result);
                    notifyDataSetChanged();
                }

                @Override
                public void preExecute() {
                }
            });
        }

        public List<String> getString() {
            return string;
        }
    }
}
