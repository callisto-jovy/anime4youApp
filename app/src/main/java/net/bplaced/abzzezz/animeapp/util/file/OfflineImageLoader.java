/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 28.06.20, 15:54
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import androidx.preference.PreferenceManager;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;
import java.io.FileOutputStream;

public class OfflineImageLoader {

    public static void loadImage(final String url, final String id, final ImageView imageView, final Context context) {
        //Get image Bitmap file
        final File imageBitmap = new File(AnimeAppMain.getInstance().getImageStorage(), id);
        if (!imageBitmap.exists()) {
            //Create new task
            new TaskExecutor().executeAsync(() -> {
                //Load image bitmap into new file, compress etc.
                Picasso.with(context).load(url).get().compress(Bitmap.CompressFormat.JPEG, PreferenceManager.getDefaultSharedPreferences(context).getInt("image_compression", 50), new FileOutputStream(imageBitmap));
                return null;
            }, new TaskExecutor.Callback<Object>() {
                @Override
                public void onComplete(Object result) {
                    //Load image in
                    Picasso.with(context).load(imageBitmap).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(imageView);
                }

                @Override
                public void preExecute() {
                    Logger.log("Offline image is being downloaded.", Logger.LogType.INFO);
                }
            });
        } else {
            //Load image from saved
            Picasso.with(context).load(imageBitmap).resize(ImageUtil.DIMENSIONS[0], ImageUtil.DIMENSIONS[1]).into(imageView);
        }
    }
}
