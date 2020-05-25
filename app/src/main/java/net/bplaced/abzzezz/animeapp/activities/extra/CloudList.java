/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 21.05.20, 17:50
 */

package net.bplaced.abzzezz.animeapp.activities.extra;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;
import ga.abzzezz.util.data.URLUtil;
import net.bplaced.abzzezz.animeapp.R;
import net.bplaced.abzzezz.animeapp.activities.main.AnimeListActivity;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class CloudList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud_list);

        try {
            ListView listView = findViewById(R.id.cloud_list_view);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1);
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect("abzzezz.bplaced.net");
            ftpClient.login("abzzezz_client", "AzA33EUSgU7KZvbj");
            FTPFile[] ftpFile = ftpClient.listFiles("/www/lists/");
            for (FTPFile file : ftpFile) {
                adapter.add(file.getName());
            }
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                try {
                    URLUtil.getURLContentAsArray(new URL("http://abzzezz.bplaced.net/lists/" + ftpFile[position].getName())).forEach(SplashScreen.saver::add);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, AnimeListActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
