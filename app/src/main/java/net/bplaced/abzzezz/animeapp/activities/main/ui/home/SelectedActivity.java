/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 07.11.20, 20:32
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.data.FileUtil;
import ga.abzzezz.util.logging.Logger;
import ga.abzzezz.util.stringing.StringUtil;
import id.ionbit.ionalert.IonAlert;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.DrawerMainMenu;
import net.bplaced.abzzezz.animeapp.activities.main.ui.player.PlayerActivity;
import net.bplaced.abzzezz.animeapp.activities.main.ui.player.StreamPlayer;
import net.bplaced.abzzezz.animeapp.util.IntentHelper;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.file.OfflineImageLoader;
import net.bplaced.abzzezz.animeapp.util.provider.Provider;
import net.bplaced.abzzezz.animeapp.util.provider.Providers;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder.InputDialogListener;

import java.io.File;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.TimeUnit;

public class SelectedActivity extends AppCompatActivity {

    public EpisodeAdapter episodeAdapter;
    private File showDirectory;

    private Show show;

    private Provider currentProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(AnimeAppMain.getInstance().getThemeId());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_show_layout);

        this.show = (Show) IntentHelper.getObjectForKey("show"); //Receive show object from intent helper
        this.showDirectory = new File(getFilesDir(), show.getID()); //Set the show's directory

        //Fill in labels
        ((TextView) this.findViewById(R.id.selected_show_title_text_view)).setText(show.getShowTitle());
        ((TextView) this.findViewById(R.id.selected_show_episode_count_text_view)).append(String.valueOf(show.getEpisodeCount()));
        ((TextView) this.findViewById(R.id.selected_show_id_text_view)).append(show.getID());
        ((TextView) this.findViewById(R.id.show_directory_total_size_text_view)).append(FileUtil.calculateFileSize(showDirectory));

        // ((TextView) findViewById(R.id.selected_anime_language)).append(show.getLanguage());


        final ImageView showCover = this.findViewById(R.id.show_cover_image_view); //Set cover
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
                    .into(showCover); //Load from url using picasso


        //Configure swipe refresh layout
        final SwipeRefreshLayout swipeRefreshLayout = this.findViewById(R.id.selected_show_swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (URLUtil.isOffline(Objects.requireNonNull(getApplicationContext()))) return;

            final long showTimestampDifference = show.getTimestampDifference(currentProvider);

            if (TimeUnit.MILLISECONDS.toMinutes(showTimestampDifference) >= 5) {
                currentProvider.getShowEpisodeReferrals(show, jsonArray -> {
                    show.addEpisodesForProvider(jsonArray, currentProvider);
                    Toast.makeText(this,
                            String.format("Refreshed episodes for %s, new episode count for provider %d", show.getShowTitle(),
                                    (jsonArray.length() + 1)),
                            Toast.LENGTH_SHORT
                    ).show();
                }); //Update episodes
            } else {
                Toast.makeText(
                        this,
                        String.format("You have already refreshed %d minutes ago. Please wait five minutes",
                                TimeUnit.MILLISECONDS.toMinutes(showTimestampDifference)),
                        Toast.LENGTH_SHORT
                ).show();
            }

            final int providerEpisodeLength = show.getShowEpisodes(currentProvider).length();
            //TODO: Remove "hack"
            ((TextView) findViewById(R.id.selected_show_episode_count_text_view)).setText("Episodes:" + providerEpisodeLength); //Update count
            episodeAdapter.setEpisodes(providerEpisodeLength);

            swipeRefreshLayout.setRefreshing(false);
        });

        //Configure the show's provider spinner
        final Spinner providerSpinner = findViewById(R.id.show_provider_spinner);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1); //Create a new array adapter
        //Add all providers from the provider enum, excluding the NULL provider
        arrayAdapter.addAll(Arrays.stream(Providers.values()).filter(providers -> providers != Providers.NULL).map(Enum::name).toArray(String[]::new));
        providerSpinner.setAdapter(arrayAdapter);

        //TODO: Provider switching

        providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Load episodes from selected provider
                currentProvider = Providers.valueOf(arrayAdapter.getItem(position)).getProvider();

                final int providerEpisodeLength = show.getShowEpisodes(currentProvider).length();
                //TODO: Remove "hack"
                ((TextView) findViewById(R.id.selected_show_episode_count_text_view)).setText("Episodes:" + providerEpisodeLength); //Update count
                episodeAdapter.setEpisodes(providerEpisodeLength);
                refreshAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //Set adapter
        this.episodeAdapter = new EpisodeAdapter(show.getEpisodeCount(), getApplicationContext());

        //Episode list view
        final ListView listView = findViewById(R.id.show_episode_item_grid);
        listView.setAdapter(episodeAdapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
            //TODO: Rework?
            final boolean isDownloaded = isEpisodeDownloaded(i);
            new IonAlert(SelectedActivity.this, IonAlert.NORMAL_TYPE)
                    .setConfirmText("Stream")
                    .setConfirmClickListener(ionAlert -> getEpisode(i, 1, 0, true))
                    .setCancelText(isDownloaded ? "Play downloaded" : "Cancel")
                    .setCancelClickListener(ionAlert -> {
                        if (isDownloaded)
                            playEpisodeFromSave(i);
                        else
                            ionAlert.dismissWithAnimation();
                    }).show();
        });
        findViewById(R.id.download_show_button).setOnClickListener(listener -> getEpisode(getLatestEpisode(), show.getEpisodeCount(), 0, false));
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
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int itemID = item.getItemId();
        switch (itemID) {
            case R.id.download_bound:
                final InputDialogBuilder dialogBuilder = new InputDialogBuilder(new InputDialogListener() {
                    @Override
                    public void onDialogInput(final String text) {
                        getEpisode(getLatestEpisode(), Integer.parseInt(text), 0, false);
                    }

                    @Override
                    public void onDialogDenied() {
                    }
                });
                dialogBuilder.showInput("Download bound", "Enter bound", this);
                break;
            case R.id.toogle_notifications_show:
                //Add to notification manager
                /*
                 * TODO: Rework
                 */
                //  AnimeAppMain.getInstance().getAnimeNotifications().add(title.concat(StringUtil.splitter) + id, String.valueOf(episodes));
                break;
            default:
                break;
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
                optionalURL -> optionalURL.ifPresent(url -> {
                    if (stream) {
                        final Intent intent = new Intent(SelectedActivity.this, StreamPlayer.class);
                        intent.putExtra("stream", url);
                        startActivity(intent);
                        finish();
                    } else
                        currentProvider.handleDownload(this, url, show, showDirectory, count[0], count[1], countMax);
                }), count[0], count[1], countMax);
    }

    /**
     * Play episode from file
     *
     * @param index episode
     */
    private void playEpisodeFromSave(final int index) {
        Intent intent = null;
        final Optional<File> videoFile = getEpisodeFile(index);

        if (videoFile.isPresent()) {
            final int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("video_player_preference", "0"));
            if (mode == 0) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", videoFile.get()), "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (mode == 1) {
                intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("path", videoFile.get().getAbsolutePath());
            }
            startActivity(Objects.requireNonNull(intent));
        }
    }

    /**
     * Get episode file from index
     *
     * @param index the requested index
     * @return optional with the file or empty
     */
    public Optional<File> getEpisodeFile(final int index) {
        if (showDirectory.listFiles() != null) {
            final String indexString = String.valueOf(index);
            for (final File file : showDirectory.listFiles()) {
                if (file.isFile() && file.getName().substring(0, file.getName().lastIndexOf(".")).equals(indexString)) {
                    return Optional.of(file);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Get the show's last episode, e.g. the file with the highest integer
     *
     * @return the highest integer found, comparing all the different filenames
     */
    public int getLatestEpisode() {
        if (showDirectory.listFiles() != null) {
            final OptionalInt highest = Arrays.stream(showDirectory.listFiles())
                    .filter(File::isFile)
                    .map(s -> StringUtil.extractNumberI(s.getName().substring(0, s.getName().lastIndexOf("."))))
                    .mapToInt(integer -> integer)
                    .max();
            if (highest.isPresent()) return highest.getAsInt() + 1;
            else return 0;
        }
        return 0;
    }

    /**
     * Check if a certain episode is downloaded
     *
     * @param index index to search for
     * @return if the file has been found
     */
    private boolean isEpisodeDownloaded(final int index) {
        if (showDirectory.listFiles() != null) {
            for (final File file : showDirectory.listFiles()) {
                if (file.isFile()) {
                    if (file.getName().substring(0, file.getName().lastIndexOf(".")).equals(String.valueOf(index)))
                        return true;
                }
            }
        }
        return false;
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
        private int episodes;

        public EpisodeAdapter(final int episodes, final Context context) {
            this.episodes = episodes;
            this.context = context;
        }

        @Override
        public int getCount() {
            return episodes;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        public void setEpisodes(int episodes) {
            this.episodes = episodes;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(context).inflate(R.layout.episode_item_layout, parent, false);

            final TextView textView = convertView.findViewById(R.id.episode_name);
            final ImageView actionButton = convertView.findViewById(R.id.download_button);
            textView.setText("Episode: " + position);

            if (isEpisodeDownloaded(position)) {
                textView.setTextColor(0xFF30475e);
                actionButton.setImageResource(R.drawable.delete);
                actionButton.setOnClickListener(view ->
                        new IonAlert(SelectedActivity.this, IonAlert.WARNING_TYPE)
                                .setTitleText("Delete file?")
                                .setContentText("Won't be able to recover this file!")
                                .setConfirmText("Yes, delete it!")
                                .setConfirmClickListener(ionAlert -> {
                                    episodeAdapter.deleteItem(position);
                                    ionAlert.dismissWithAnimation();
                                }).setCancelText("Abort").setCancelClickListener(IonAlert::dismissWithAnimation)
                                .show());
            } else {
                textView.setTextColor(0xFFFFFFF);
                actionButton.setImageResource(R.drawable.download);
                actionButton.setOnClickListener(view -> getEpisode(position, 1, 0, false));
            }
            return convertView;
        }

        /**
         * Delete file
         *
         * @param index episode to delete (index)
         */
        public void deleteItem(final int index) {
            //Get episode file, delete then notify dataset changes
            getEpisodeFile(index).ifPresent(file -> {
                Logger.log("Deleted: " + file.delete(), Logger.LogType.INFO);
                notifyDataSetChanged();
            });
        }
    }
}

