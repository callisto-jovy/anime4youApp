/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 22:00
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;

import java.io.File;
import java.util.Optional;

public class ListFragment extends Fragment {

    @SuppressLint("CheckResult")
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_shows, container, false);

        final GridView gridView = root.findViewById(R.id.show_item_grid);

        final ShowAdapter showAdapter = new ShowAdapter(AnimeAppMain.INSTANCE.getShowSaver().getShowSize(), getActivity());
        gridView.setAdapter(showAdapter);
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
                    .setConfirmText("Yes, delete")
                    .setConfirmClickListener(ionAlert -> {
                        showAdapter.removeItem(position);
                        ionAlert.dismissWithAnimation();
                    })
                    .setCancelText("Abort")
                    .setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
            return true;
        });

        if (AnimeAppMain.INSTANCE.isVersionOutdated()) {
            new IonAlert(getActivity(), IonAlert.WARNING_TYPE)
                    .setTitleText("Outdated version")
                    .setContentText("Your app-version is outdated, please update it now! If the auto update does not start automatically, download from the website!")
                    .setCancelText("Close")
                    .setCancelClickListener(IonAlert::dismissWithAnimation)
                    .show();
        }
        //Configure swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.shows_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (URLUtil.isOffline(getContext())) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }

            if (AnimeAppMain.INSTANCE.getMyAnimeList().isSyncable()) {
                AnimeAppMain.INSTANCE.getMyAnimeList().startSync(aBoolean -> {
                    if (!aBoolean) {
                        final MaterialDialog dialog = new MaterialDialog(getContext(), new BottomSheet());
                        dialog.title(null, "Myanimelist credentials");
                        DialogCustomViewExtKt.customView(dialog, R.layout.dialog_two_input_password, null, false, true, false, true);

                        dialog.positiveButton(null, "Sync", materialDialog -> {
                            assert materialDialog.getView() != null : "Custom view is null";

                            final EditText usernameEditText = materialDialog.getView().findViewById(R.id.dialog_two_input_username);
                            final EditText password = materialDialog.getView().findViewById(R.id.dialog_two_input_password);
                            final String username = usernameEditText.getText().toString().trim();

                            AnimeAppMain.INSTANCE.getMyAnimeList().setupSync(username, password.getText().toString(), processFinished -> {
                                if (processFinished)
                                    new IonAlert(getActivity(), IonAlert.SUCCESS_TYPE)
                                            .setTitleText("Sync done")
                                            .setConfirmClickListener(IonAlert::dismissWithAnimation)
                                            .show();
                                else
                                    new IonAlert(getActivity(), IonAlert.ERROR_TYPE)
                                            .setTitleText("Sync was not successful")
                                            .setConfirmText("Done")
                                            .setConfirmClickListener(IonAlert::dismissWithAnimation)
                                            .show();
                            });
                            return null;
                        });
                        dialog.show();
                    }
                    showAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                });
            }
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
        AnimeAppMain.INSTANCE.getShowSaver().getShow(index).ifPresent(show -> {
            IntentHelper.addObjectForKey(show, "show");
            this.startActivity(intent);
            requireActivity().finish();
        });
    }

    class ShowAdapter extends BaseAdapter {

        private final Context context;
        private int size;

        public ShowAdapter(int size, final Context context) {
            this.size = size;
            this.context = context;
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            return AnimeAppMain.INSTANCE.getShowSaver().getShow(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;

            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_show, parent, false);
            } else view = convertView;

            final ImageView coverImage = view.findViewById(R.id.list_show_cover_image_view);
            final TextView showTitle = view.findViewById(R.id.list_show_title_text_view);
            //Grab show
            AnimeAppMain.INSTANCE.getShowSaver().getShow(position).ifPresent(show -> {
                showTitle.setText(show.getShowTitle()); //Set title text view
                //Load image
                final String imageURL = show.getImageURL();

                if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("offline_mode", false))
                    OfflineImageLoader.loadImage(imageURL, show, coverImage, getContext());
                else
                    Picasso.with(context)
                            .load(imageURL)
                            .resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1])
                            .into(coverImage);
            });
            return view;
        }

        public void removeItem(final int index) {
            if (getActivity().getFilesDir() == null) return;

            final Optional<Show> itemToRemove = (Optional<Show>) getItem(index);
            itemToRemove.ifPresent(show -> {
                this.size--;
                final File dir = new File(getActivity().getFilesDir(), show.getShowTitle());
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
                            })
                            .setCancelText("Abort")
                            .setCancelClickListener(IonAlert::dismissWithAnimation)
                            .show();
                }
                AnimeAppMain.INSTANCE.getShowSaver().remove(index);
                notifyDataSetChanged();
            });
        }
    }

}
