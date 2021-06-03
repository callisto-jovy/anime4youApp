/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.04.21, 14:29
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.bottomsheets.BottomSheet;
import com.afollestad.materialdialogs.customview.DialogCustomViewExtKt;
import com.afollestad.materialdialogs.list.DialogSingleChoiceExtKt;
import com.google.android.material.slider.Slider;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.activities.main.ui.other.EpisodeBottomSheet;
import net.bplaced.abzzezz.animeapp.activities.main.ui.player.StreamPlayer;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.myanimelist.MyAnimeListWatchingStatus;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder.InputDialogListener;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class SelectedActivity extends AppCompatActivity {

    public EpisodeAdapter episodeAdapter;
    private File showDirectory;

    private Show show;

    private Provider currentProvider;
    private ColorStateList defaultTextColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_show);

        this.show = (Show) IntentHelper.getObjectForKey("show"); //Receive show object from intent helper
        this.showDirectory = new File(getFilesDir(), show.getID()); //Set the show's directory


        //Fill in labels
        ((TextView) this.findViewById(R.id.selected_show_title_text_view)).setText(show.getShowTitle());
        ((TextView) this.findViewById(R.id.selected_show_id_text_view)).setText(getString(R.string.show_id, show.getID()));
        ((TextView) this.findViewById(R.id.show_directory_total_size_text_view)).append(FileUtil.calculateFileSize(showDirectory));
        ((TextView) findViewById(R.id.selected_show_score_text_view)).setText(getString(R.string.show_score, show.getShowScore()));

        final TextView episodeCountTextView = this.findViewById(R.id.selected_show_episode_count_text_view);
        episodeCountTextView.setText(getString(R.string.show_episodes, show.getEpisodeCount()));

        this.defaultTextColor = episodeCountTextView.getTextColors(); //This is needed to revert the text colors back to default, android you are a little bitch...

        final ImageView showCover = this.findViewById(R.id.selected_show_cover_image_view); //Set cover
        final Toolbar applicationToolbar = this.findViewById(R.id.selected_show_toolbar); //Set toolbar
        setSupportActionBar(applicationToolbar); //Enable action bar

        Objects.requireNonNull(getSupportActionBar()).setTitle(show.getShowTitle()); //Set the toolbar'S title

        //Use image loader
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("offline_mode", false))
            OfflineImageLoader.loadImage(show.getImageURL(), show, showCover, this); //Offline image loader
        else
            Picasso.with(getApplicationContext())
                    .load(show.getImageURL())
                    .resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1])
                    .into(showCover); //Load from url using picasso & resize to fit the image view bounds

        //Configure swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = this.findViewById(R.id.selected_show_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (URLUtil.isOffline(Objects.requireNonNull(getApplicationContext()))) return; //Reject offline requests

            final Provider requestedProvider = currentProvider; //Create a local copy of the current provider, in case the provider is switched (so that data does not mix)

            final long showTimestampDifference = show.getTimestampDifference(requestedProvider); //Get the time difference between the saved episodes and the current system internal time

            if (TimeUnit.MILLISECONDS.toMinutes(showTimestampDifference) >= 5) { //Lets the user only refresh every five minutes
                requestedProvider.getShowEpisodeReferrals(show, jsonArray -> {
                    show.addEpisodesForProvider(jsonArray, requestedProvider); //Adds the episodes to the provider

                    if (currentProvider == requestedProvider) { //Don't update if the provider was switched
                        //TODO: Remove "hack"
                        episodeCountTextView.setText(getString(R.string.show_episodes, jsonArray.length())); //Update text view
                        episodeAdapter.setSize(jsonArray.length()); //Update adapter with the new available episode count
                        refreshAdapter(); //Refresh the adapter so all changes are shown
                    }
                    //Display a little message
                    Toast.makeText(this,
                            String.format(Locale.ENGLISH, "Refreshed episodes for %s, new episode count for provider %s %d", show.getShowTitle(), requestedProvider.getName(), jsonArray.length()),
                            Toast.LENGTH_SHORT
                    ).show();

                });
            } else {
                Toast.makeText(
                        this,
                        String.format(Locale.ENGLISH, "You have already refreshed %d minutes ago. Please wait five minutes",
                                TimeUnit.MILLISECONDS.toMinutes(showTimestampDifference)),
                        Toast.LENGTH_SHORT
                ).show();
            }

            swipeRefreshLayout.setRefreshing(false);
        });

        //Configure the show's provider spinner
        final Spinner providerSpinner = findViewById(R.id.show_provider_spinner);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1); //Create a new array adapter
        //Add all providers from the provider enum, excluding the NULL provider
        try (final Stream<Providers> providerStream = Stream.of(Providers.values())) {
            arrayAdapter.addAll(providerStream.filter(provider -> provider != Providers.NULL).map(Enum::name).toArray(String[]::new));
        }
        providerSpinner.setAdapter(arrayAdapter);

        providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                //Load episodes from selected provider
                currentProvider = Providers.valueOf(arrayAdapter.getItem(position)).getProvider();

                final int providerEpisodeLength = show.getShowEpisodes(currentProvider).length();
                //TODO: Remove "hack"
                episodeCountTextView.setText(getString(R.string.show_episodes, providerEpisodeLength)); //Update count
                episodeAdapter.setSize(providerEpisodeLength); //Update the size
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Set adapter
        this.episodeAdapter = new EpisodeAdapter(show.getEpisodeCount(), this);

        //Episode list view
        final GridView gridView = findViewById(R.id.show_episode_item_grid);
        gridView.setAdapter(episodeAdapter);

        gridView.setOnItemClickListener((adapterView, view, index, l) -> {
            final EpisodeBottomSheet episodeBottomSheet = new EpisodeBottomSheet(SelectedActivity.this, showCover, index);
            episodeBottomSheet.show(getSupportFragmentManager(), "Episode Bottom Sheet");
        });

        findViewById(R.id.download_show_button).setOnClickListener(listener ->
                getEpisode(AnimeAppMain.getInstance().getShowSaver().getLatestEpisode(showDirectory),
                        show.getEpisodeCount(),
                        0,
                        false));
    }

    /**
     * Toolbar's options
     *
     * @param menu menu to display
     * @return has the menu been created
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.anime_selected_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }


    /**
     * Items selected
     *
     * @param item selected item
     * @return was item selected
     */
    @SuppressLint("CheckResult")
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        final int itemID = item.getItemId();

        if (itemID == R.id.download_bound) {
            final InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogListener() {
                @Override
                public void onDialogInput(final String text) {
                    getEpisode(AnimeAppMain.getInstance().getShowSaver().getLatestEpisode(showDirectory), Integer.parseInt(text), 0, false);
                }

                @Override
                public void onDialogDenied() {
                }
            });
            dialogBuilder.showInput("Download bound", "Enter bound", this);
        } else if (itemID == R.id.set_show_status) {
            final List<String> items = Arrays.asList(
                    getString(R.string.show_status_item_watching),
                    getString(R.string.show_status_item_completed),
                    getString(R.string.show_status_item_on_hold),
                    getString(R.string.show_status_item_dropped),
                    getString(R.string.show_status_item_plan_to_watch)
            );

            final int i = MyAnimeListWatchingStatus.getFromStatus(getShow().getWatchingStatus()).getIndex();

            final MaterialDialog dialog = new MaterialDialog(this, new BottomSheet());
            dialog.title(null, "Select state");
            DialogSingleChoiceExtKt.listItemsSingleChoice(dialog, null, items, null, i, true, (materialDialog, integer, s) -> {
                AnimeAppMain.getInstance().getMyAnimeList().updateShowState(getShow(), MyAnimeListWatchingStatus.getFromIndex(integer).getString());
                return null;
            });
            dialog.show();

        } else if (itemID == R.id.set_show_score) {
            final MaterialDialog dialog = new MaterialDialog(this, new BottomSheet());
            dialog.title(null, "Select score");
            DialogCustomViewExtKt.customView(dialog, R.layout.dialog_slider, null, false, true, false, true);

            ((Slider) dialog.getView().findViewById(R.id.seekBar)).setValue((float) getShow().getShowScore());

            dialog.positiveButton(null, "Set", materialDialog -> {
                assert materialDialog.getView() != null : "Custom view is null";

                final Slider seekBar = materialDialog.getView().findViewById(R.id.seekBar);
                AnimeAppMain.getInstance().getMyAnimeList().updateShowScore(getShow(), (int) seekBar.getValue());
                getShow().setShowScore(seekBar.getValue());
                return null;
            });
            dialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * On Back pressed
     */
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, DrawerMainMenu.class));
        finish();
        super.onBackPressed();
    }

    /**
     * Download method
     *
     * @param start        start
     * @param countMax     max download
     * @param currentCount current episode
     */
    public void getEpisode(final int start, final int countMax, final int currentCount, final boolean stream) {
        Logger.log("Next episode: " + start, Logger.LogType.INFO);
        final int[] count = {currentCount, start};

        // Check if count is bigger than the max episodes to download
        if (count[0] >= countMax) {
            Logger.log("current episode exceeds max / start exceeds max", Logger.LogType.ERROR);
            return;
        }

        currentProvider.handleURLRequest(show, getApplicationContext(),
                optionalURL -> {
                    if (optionalURL.isPresent()) {
                        final String url = optionalURL.get();
                        if (stream) {
                            final Intent intent = new Intent(SelectedActivity.this, StreamPlayer.class);
                            intent.putExtra("stream", url);
                            startActivity(intent);
                            finish();
                        } else
                            currentProvider.handleDownload(this, url, show, showDirectory, count[0], count[1], countMax);
                    } else {
                        Toast.makeText(this, String.format(Locale.ENGLISH, "No link found for episode %d with provider %s", count[1], currentProvider.getName()), Toast.LENGTH_SHORT).show();
                    }
                }, count[0], count[1], countMax);
    }

    public File getShowDirectory() {
        return showDirectory;
    }

    public Show getShow() {
        return show;
    }

    /**
     * Delete file
     *
     * @param index episode to delete (index)
     */
    public void deleteItem(final int index) {
        //Get episode file, delete then notify dataset changes
        AnimeAppMain.getInstance().getShowSaver().getEpisodeFile(index, showDirectory).ifPresent(file -> {
            Logger.log("Deleted: " + file.delete(), Logger.LogType.INFO);
            refreshAdapter();
        });
    }

    /**
     * Refresh the grid layout's adapter eg. notify any data set changes
     */
    public void refreshAdapter() {
        episodeAdapter.notifyDataSetChanged();
    }

    /**
     * Episode adapter
     */
    class EpisodeAdapter extends BaseAdapter {

        private final Context context;
        private int size;

        public EpisodeAdapter(final int size, final Context context) {
            this.size = size;
            this.context = context;
        }

        @Override
        public int getCount() {
            return size;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        public void setSize(int size) {
            this.size = size;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_episode, parent, false);
            } else view = convertView;

            final CheckedTextView checkedTextView = view.findViewById(R.id.episode_int_text_view);
            checkedTextView.setText(String.valueOf(position));
            checkedTextView.setChecked(show.getEpisodesWatched() >= position); //Every episode watched is marked (TODO: Episodes not watched in order)
            //Highlight downloaded episodes, i.e. give those text views a different color
            if (AnimeAppMain.getInstance().getShowSaver().isEpisodeDownloaded(position, showDirectory))
                checkedTextView.setTextColor(getColor(R.color.colorAccent));
            else checkedTextView.setTextColor(defaultTextColor); //Revert text color back to default

            return view;
        }
    }
}

