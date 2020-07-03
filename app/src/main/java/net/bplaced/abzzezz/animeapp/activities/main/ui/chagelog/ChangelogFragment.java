/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 14.06.20, 20:08
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.chagelog;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.scripter.URLHandler;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ChangelogFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.changelog_layout, container, false);

        new ChangelogGetter().execute();
        return root;
    }

    class ChangelogGetter extends AsyncTask<Void, Void, ArrayList> {

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param voids The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected ArrayList<String> doInBackground(Void... voids) {
            ArrayAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
            try {
                URLUtil.getURLContentAsArray(new URL(URLHandler.APP_CHANGELOG_TXT)).forEach(arrayAdapter::add);
                getActivity().runOnUiThread(() -> {
                    ListView listView = getActivity().findViewById(R.id.changelog_list_view);
                    listView.setAdapter(arrayAdapter);
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
