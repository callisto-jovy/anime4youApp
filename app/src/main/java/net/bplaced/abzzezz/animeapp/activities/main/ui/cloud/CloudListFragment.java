/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 25.06.20, 14:56
 */

package net.bplaced.abzzezz.animeapp.activities.main.ui.cloud;

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
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class CloudListFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.simplelist_layout, container, false);
        ListView listView = root.findViewById(R.id.cloud_list_view);
        /**
         * Let the threads do their work
         */

        new TaskExecutor().executeAsync(() -> {
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
            //List for filenames
            ArrayList<String> files = new ArrayList<>();
            //new FTP Client connect and login
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect("abzzezz.bplaced.net");
            ftpClient.login("abzzezz_client", "AzA33EUSgU7KZvbj");
            FTPFile[] ftpFile = ftpClient.listFiles("/www/lists/");
            //List files and add files to filename list
            for (FTPFile file : ftpFile) {
                if (file.isFile()) files.add(file.getName());
            }
            //New thread to add animes to list
            //Run on Ui , otherwise the app crashes
            getActivity().runOnUiThread(() -> {
                //Set on click listener
                listView.setOnItemClickListener((parent, view, position, id) -> {
                    new TaskExecutor().executeAsync(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            try {
                                //Pull and add
                                URLUtil.getURLContentAsArray(new URL("http://abzzezz.bplaced.net/lists/" + files.get(position))).forEach(AnimeAppMain.getInstance().getAnimeSaver()::add);
                            } catch (MalformedURLException e) {
                                Logger.log("Error getting from cloud.", Logger.LogType.ERROR);
                                e.printStackTrace();
                            }
                            return null;
                        }
                    }, new TaskExecutor.Callback<Object>() {
                        @Override
                        public void onComplete(Object result) {

                        }

                        @Override
                        public void preExecute() {

                        }
                    });
                });

                //Finally set the adapter and add files to adapter
                arrayAdapter.addAll(files);
                listView.setAdapter(arrayAdapter);
            });
            return null;
        }, new TaskExecutor.Callback<Object>() {
            @Override
            public void onComplete(Object result) {
            }

            @Override
            public void preExecute() {
            }
        });

        return root;
    }
}
