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
import net.bplaced.abzzezz.animeapp.util.show.Show;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import net.bplaced.abzzezz.animeapp.util.ui.ImageUtil;

import java.io.File;
import java.io.FileOutputStream;

public class OfflineImageLoader {

    /**
     * Downloads a bitmap from a URL and saves it to the image storage folder
     * @param url URL (String) to download the image from
     * @param show Show the image will be associated with
     * @param imageView imageView to load the image into
     * @param context context for picasso image grabbing
     */
    public static void loadImage(final String url, final Show show, final ImageView imageView, final Context context) {
        //Get image Bitmap file
        final File imageBitmap = new File(AnimeAppMain.getInstance().getImageStorage(), show.getProvider().getName() + show.getID());
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
                    Picasso.with(context).load(imageBitmap).resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1]).into(imageView);
                }

                @Override
                public void preExecute() {
                    Logger.log("Offline image is loading...", Logger.LogType.INFO);
                }
            });
        } else {
            //Load image from saved
            Picasso.with(context).load(imageBitmap).resize(ImageUtil.IMAGE_COVER_DIMENSIONS[0], ImageUtil.IMAGE_COVER_DIMENSIONS[1]).into(imageView);
        }
    }
}
