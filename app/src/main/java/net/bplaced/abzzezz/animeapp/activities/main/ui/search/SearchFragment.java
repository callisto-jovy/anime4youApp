/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 06.04.21, 18:56
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import com.squareup.picasso.Picasso;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.connection.URLUtil;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.myanimelist.MyAnimeListSearchTask;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_search, container, false);
        final ListView listView = root.findViewById(R.id.simple_list_view);
        final SearchView showSearch = root.findViewById(R.id.show_search_view);
        //Set adapter
        listView.setAdapter(new SearchAdapter(new ArrayList<>(), root.getContext()));

        showSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (URLUtil.isOffline(getContext())) return true;

                final SearchAdapter searchAdapter = (SearchAdapter) listView.getAdapter();
                searchAdapter.getEntries().clear();

                new MyAnimeListSearchTask(query).executeAsync(new TaskExecutor.Callback<List<Show>>() {
                    @Override
                    public void onComplete(final List<Show> result) {
                        searchAdapter.getEntries().addAll(result);
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void preExecute() {

                    }
                });
                showSearch.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return root;
    }

    static class SearchAdapter extends BaseAdapter {
        private final List<Show> entries;
        private final Context context;

        public SearchAdapter(final List<Show> entries, final Context context) {
            this.entries = entries;
            this.context = context;
        }

        @Override
        public int getCount() {
            return entries.size();
        }

        @Override
        public Object getItem(int position) {
            return entries.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = LayoutInflater.from(context).inflate(R.layout.item_search, parent, false);
            } else
                view = convertView;

            final Show showAtIndex = (Show) getItem(position);

            view.setOnClickListener(listener -> {
                AnimeAppMain.INSTANCE.getShowSaver().addShow(showAtIndex);
                Toast.makeText(context, "Added show!", Toast.LENGTH_SHORT).show();
            });

            final TextView showTitle = view.findViewById(R.id.search_show_title_text_view);
            final TextView showEpisodes = view.findViewById(R.id.search_show_episodes_text_view);
            final TextView showScore = view.findViewById(R.id.search_show_score_text_view);
            final TextView showID = view.findViewById(R.id.search_show_id_text_view);

            final ImageView imageView = view.findViewById(R.id.search_show_cover_image_view);
            //Load image from url into the image view
            Picasso.with(context)
                    .load(showAtIndex.getImageURL())
                    .resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1])
                    .into(imageView);

            //Format using android locale
            showTitle.setText(context.getString(R.string.show_title, showAtIndex.getShowTitle())); //append the show's title
            showEpisodes.setText(context.getString(R.string.show_episodes, showAtIndex.getEpisodeCount())); //Append the episode count
            showScore.setText(context.getString(R.string.show_score, showAtIndex.getShowScore())); //Append the provider
            showID.setText(context.getString(R.string.show_id, showAtIndex.getID()));

            return view;
        }

        public List<Show> getEntries() {
            return entries;
        }
    }
}
