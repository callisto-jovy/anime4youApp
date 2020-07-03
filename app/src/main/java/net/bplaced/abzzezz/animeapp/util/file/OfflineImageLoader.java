/*
 * Copyright (c) 2020. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 28.06.20, 15:54
 */

package net.bplaced.abzzezz.animeapp.util.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import ga.abzzezz.util.logging.Logger;
import net.bplaced.abzzezz.animeapp.AnimeAppMain;
import net.bplaced.abzzezz.animeapp.util.ImageUtil;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;

import java.io.File;
import java.io.FileOutputStream;

public class OfflineImageLoader {

    public static void loadImage(String url, String aid, ImageView imageView, Context context) {
        //Get image Bitmap file
        File imageBitmap = new File(AnimeAppMain.getInstance().getImageStorage(), aid);
        if (!imageBitmap.exists()) {
            //Create new task
            new TaskExecutor().executeAsync(() -> {
                FileOutputStream fileOutputStream = new FileOutputStream(imageBitmap);
                //Get imagebitmap and save to file
                Logger.log("Getting image bitmap", Logger.LogType.INFO);
                Picasso.with(context).load(url).get().compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                //Close stream
                fileOutputStream.close();
                return null;
            }, new TaskExecutor.Callback<Object>() {
                @Override
                public void onComplete(Object result) {
                    /*
                     * If thread is done load image in
                     */
                    Picasso.with(context).load(imageBitmap).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
                }

                @Override
                public void preExecute() {
                }
            });
        } else {
            //Load image from saved
            Picasso.with(context).load(imageBitmap).resize(ImageUtil.dimensions[0], ImageUtil.dimensions[1]).into(imageView);
        }
    }
}
