/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.06.21, 19:21
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.other;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.ui.home.SelectedActivity;
import net.bplaced.abzzezz.animeapp.activities.main.ui.player.PlayerActivity;

import java.io.File;
import java.util.Objects;
import java.util.Optional;

public class EpisodeBottomSheet extends BottomSheetDialogFragment {

    private final ImageView showCover;
    private final String showName;
    private final int episode;
    private final File showDirectory;

    private final SelectedActivity parent;

    public EpisodeBottomSheet(final SelectedActivity parent, final ImageView showCover, final int episode) {
        this.parent = parent;
        this.showCover = showCover;
        this.showDirectory = parent.getShowDirectory();
        this.showName = parent.getShow().getShowTitle();
        this.episode = episode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_selected_show_bottom_sheet_dialog, container, false);

        final ImageView imageView = view.findViewById(R.id.selected_show_bottom_sheet_cover_image_view);
        imageView.setImageDrawable(showCover.getDrawable()); //Copy cover image

        final TextView showTitleTextView = view.findViewById(R.id.selected_show_bottom_sheet_title_text_view);
        showTitleTextView.setText(showName);

        final TextView episodeTextView = view.findViewById(R.id.selected_show_bottom_sheet_episode_text_view);
        episodeTextView.setText(getString(R.string.show_episode, episode));

        final Button leftButton = view.findViewById(R.id.selected_show_bottom_sheet_left_button);
        final boolean isDownloaded = AnimeAppMain.INSTANCE.getShowSaver().isEpisodeDownloaded(episode, showDirectory);

        if (isDownloaded) leftButton.setText(R.string.play_episode_button);

        leftButton.setOnClickListener(v -> {
            if (isDownloaded)
                playEpisodeFromSave(episode);
            else
                parent.getEpisode(episode, 1, 0, false);
        });

        final Button markButton = view.findViewById(R.id.selected_show_bottom_sheet_mark_as_watched_button);
        setButtonText(markButton);

        markButton.setOnClickListener(v -> {
            parent.getShow().setEpisodeWatched(episode); //Increment the number of episodes watched
            AnimeAppMain.INSTANCE.getMyAnimeList().updateShowEpisodes(parent.getShow());
            setButtonText(markButton);
            parent.refreshAdapter();
        });

        final Button deleteButton = view.findViewById(R.id.selected_show_button_sheet_delete_button);
        deleteButton.setActivated(isDownloaded);

        deleteButton.setOnClickListener(v -> {
            parent.deleteItem(episode);
            deleteButton.setActivated(false);
        });

        return view;
    }

    private void setButtonText(final Button button) {
        button.setText(parent.getShow().isEpisodeWatched(episode) ? R.string.mark_unwatched_button_text : R.string.mark_watched_button_text);
    }

    /**
     * Play episode from file
     *
     * @param index episode
     */
    private void playEpisodeFromSave(final int index) {
        Intent intent = null;
        final Optional<File> videoFile = AnimeAppMain.INSTANCE.getShowSaver().getEpisodeFile(index, showDirectory);

        if (videoFile.isPresent()) {
            final int mode = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(getContext()).getString("video_player_preference", "0"));
            if (mode == 0) {
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".provider", videoFile.get()), "video/mp4");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else if (mode == 1) {
                intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("path", videoFile.get().getAbsolutePath());
            }
            if (intent == null) {
                Toast.makeText(getContext(), "Cannot use selected player", Toast.LENGTH_SHORT).show();
            } else
                startActivity(Objects.requireNonNull(intent));
        }
    }
}
