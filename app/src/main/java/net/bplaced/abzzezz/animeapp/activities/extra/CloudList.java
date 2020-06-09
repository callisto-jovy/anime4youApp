/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 26.05.20, 14:39
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import ga.abzzezz.util.data.URLUtil;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.AnimeListActivity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class CloudList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * Set theme
         */
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_mode", false)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.LightTheme);
        }

        super.onCreate(savedInstanceState);
        /**
         * Create list
         */
        setContentView(R.layout.activity_cloud_list);
        ListView listView = findViewById(R.id.cloud_list_view);
        /**
         * Let the threads do their work
         */
        FTPGetter ftpGetter = new FTPGetter();
        ftpGetter.execute(listView);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AnimeListActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }


    class FTPGetter extends AsyncTask<ListView, Void, Void> {
        @Override
        protected Void doInBackground(ListView... adapter) {
            try {
                /**
                 * Create new Adapter
                 */
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
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
                AnimeCloudListGetter animeListGetter = new AnimeCloudListGetter();
                //Run on Ui , otherwise the app crashes
                runOnUiThread(() -> {
                    //Set on click listener
                    adapter[0].setOnItemClickListener((parent, view, position, id) -> {
                        animeListGetter.execute(files.get(position));
                    });

                    //Finally set the adapter and add files to adapter
                    arrayAdapter.addAll(files);
                    adapter[0].setAdapter(arrayAdapter);
                });
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "Can not resolve host to fetch Lists, no internet connected?", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
            return null;
        }
    }

    /**
     * Task to add all animes from cloud
     */
    class AnimeCloudListGetter extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... strings) {
            try {
                //Pull and add
                URLUtil.getURLContentAsArray(new URL("http://abzzezz.bplaced.net/lists/" + strings[0])).forEach(SplashScreen.saver::add);
            } catch (MalformedURLException e) {
                Logger.log("Error getting from cloud.", Logger.LogType.ERROR);
                e.printStackTrace();
            }
            return null;
        }
    }

}
