/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 23.06.20, 18:16
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.search;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.provider.ProviderType;
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.tasks.anime4you.Anime4YouSearchDBTask;
import net.bplaced.abzzezz.animeapp.util.tasks.gogoanime.GogoAnimeFetchTask;
import net.bplaced.abzzezz.animeapp.util.ui.InputDialogBuilder;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.search_layout, container, false);
        final ListView listView = root.findViewById(R.id.simple_list_view);
        final SearchView showSearch = root.findViewById(R.id.show_search_view);
        /*
        Set adapter
         */
        listView.setAdapter(new SearchAdapter(new ArrayList<>(), root.getContext()));

        showSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                new Anime4YouSearchDBTask(query).executeAsync(new TaskExecutor.Callback<List<JSONObject>>() {
                    @Override
                    public void onComplete(final List<JSONObject> result) {
                        showSearch.clearFocus();

                        ((SearchAdapter) listView.getAdapter()).getEntries().clear();
                        ((SearchAdapter) listView.getAdapter()).getEntries().addAll(result);
                        ((SearchAdapter) listView.getAdapter()).notifyDataSetChanged();
                    }

                    @Override
                    public void preExecute() {
                        Log.i("Search", "Staring search");
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        root.findViewById(R.id.add_aid).setOnClickListener(v -> new InputDialogBuilder(new InputDialogBuilder.InputDialogListener() {
            @Override
            public void onDialogInput(String text) {
                new GogoAnimeFetchTask(text).executeAsync(new TaskExecutor.Callback<Show>() {
                    @Override
                    public void onComplete(final Show result) throws Exception {
                        AnimeAppMain.getInstance().getShowSaver().addShow(result);
                        Toast.makeText(root.getContext(), "Added show!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void preExecute() {

                    }
                });
                /*

                if (!StringHandler.isOnline(Objects.requireNonNull(getActivity()))) {
                    Toast.makeText(root.getContext(), "You are currently not connected to the internet", Toast.LENGTH_LONG).show();
                    return;
                }
                //Create new database request. get episodes, imageURL, name
                new TaskExecutor().executeAsync(new Anime4YouDataBaseTask(text, new AniDBSearch()), new TaskExecutor.Callback<JSONObject>() {
                    @Override
                    public void onComplete(final JSONObject result) throws Exception {
                        AnimeAppMain.getInstance().getShowSaver().addShow(result);
                        Toast.makeText(root.getContext(), "Added show!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void preExecute() {
                    }
                });

                 */
            }

            @Override
            public void onDialogDenied() {
            }
        }).showInput("Enter AID", "Enter AID to add anime", getActivity()));

        return root;
    }

    static class SearchAdapter extends BaseAdapter {
        private final List<JSONObject> entries;
        private final Context context;

        public SearchAdapter(final List<JSONObject> entries, final Context context) {
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
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.show_item_layout, parent, false);

                final JSONObject jsonAtIndex = (JSONObject) getItem(position);

                convertView.setOnClickListener(listener -> {
                    try {
                        AnimeAppMain.getInstance().getShowSaver().addShow(ProviderType.ANIME4YOU.getProvider().getShow(jsonAtIndex));
                        Toast.makeText(context, "Added show!", Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

                final TextView showTitle = convertView.findViewById(R.id.show_title);
                final TextView showEpisodes = convertView.findViewById(R.id.show_episodes);
                final TextView showLanguage = convertView.findViewById(R.id.show_language);
                final TextView showYear = convertView.findViewById(R.id.show_year);

                try {
                    showTitle.append(jsonAtIndex.getString("titel"));
                    showEpisodes.append(jsonAtIndex.getString("Letzte"));
                    showLanguage.append(jsonAtIndex.getString("Untertitel"));
                    showYear.append(jsonAtIndex.getString("Jahr"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


            return convertView;
        }

        public List<JSONObject> getEntries() {
            return entries;
        }
    }
}
