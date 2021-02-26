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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.scripter.StringHandler;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class ListFragment extends Fragment {

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

        if (AnimeAppMain.getInstance().isVersionOutdated()) {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Outdated version")
                    .setContentText("Your app-version is outdated, please update it now! If the auto update does not start automatically, download from the website!")
                    .setCancelText("Close").setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
        }

        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (StringHandler.isOffline(Objects.requireNonNull(getContext()))) return;

            for (int i = 0; i < AnimeAppMain.getInstance().getShowSaver().getShowSize(); i++) {
                int finalI = i;
                AnimeAppMain.getInstance().getShowSaver().getShow(i).ifPresent(show -> show.getProvider().refreshShow(show, refreshedShow -> {
                    AnimeAppMain.getInstance().getShowSaver().refreshShow(refreshedShow, finalI);
                    Toast.makeText(getContext(), "Refreshed show:" + refreshedShow.getTitle(), Toast.LENGTH_SHORT).show();
                }));
            }
            swipeRefreshLayout.setRefreshing(false);
        });
        return root;
    }

    /**
     * Get shows information from index
     *
     * @param index  show index
     * @param intent intent
     */
    private void getInformation(final int index, final Intent intent) {
        AnimeAppMain.getInstance().getShowSaver().getShow(index).ifPresent(show -> {
            IntentHelper.addObjectForKey(show, "show");
            this.startActivity(intent);
            Objects.requireNonNull(getActivity()).finish();
        });
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

            AnimeAppMain.getInstance().getShowSaver().getShow(position).ifPresent(show -> {
                final String imageURL = show.getImageURL();

                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false))
                    OfflineImageLoader.loadImage(imageURL, show, coverImage, getContext());
                else
                    Picasso.with(context).load(imageURL).resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1]).into(coverImage);
            });
            coverImage.setAdjustViewBounds(true);
            return coverImage;
        }

        public void removeItem(final int index) {
            if (getActivity().getFilesDir() == null) return;

            final Optional<Show> itemToRemove = (Optional<Show>) getItem(index);
            itemToRemove.ifPresent(show -> {
                this.size--;
                final File dir = new File(getActivity().getFilesDir(), show.getTitle());
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
                AnimeAppMain.getInstance().getShowSaver().remove(index);
                notifyDataSetChanged();
            });
        }
    }

}
