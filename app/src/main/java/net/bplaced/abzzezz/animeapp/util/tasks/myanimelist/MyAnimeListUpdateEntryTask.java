/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 18:10
 */

package net.bplaced.abzzezz.animeapp.util.tasks.myanimelist;

import net.bplaced.abzzezz.animeapp.util.Constant;
import net.bplaced.abzzezz.animeapp.util.datatypes.ArrayHelper;
import net.bplaced.abzzezz.animeapp.util.myanimelist.MyAnimeListToken;
import net.bplaced.abzzezz.animeapp.util.provider.holders.MyAnimeListHolder;
import net.bplaced.abzzezz.animeapp.util.tasks.TaskExecutor;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Callable;

public class MyAnimeListUpdateEntryTask extends TaskExecutor implements Callable<Integer>, MyAnimeListHolder {


    private final String[][] keyValuePairs;
    private final String showID;
    private final MyAnimeListToken token;

    public MyAnimeListUpdateEntryTask(final String showID, final MyAnimeListToken token, final String[]... keyValuePairs) {
        this.keyValuePairs = keyValuePairs;
        this.showID = showID;
        this.token = token;
    }


    public void executeAsync(Callback<Integer> responseCode) {
        super.executeAsync(this, responseCode);
    }

    @Override
    public Integer call() {
        final Connection.Response response;
        try {
            response = Jsoup
                    .connect(String.format(Locale.ENGLISH, MAL_API_SHOW, showID))
                    .method(Connection.Method.PUT)
                    .header(AUTHORIZATION[0], String.format(Locale.ENGLISH, AUTHORIZATION[1], token.getTokenType(), token.getAccessToken()))
                    .header(X_CLIENT[0], X_CLIENT[1])
                    .data(ArrayHelper.stringArrayToMap(keyValuePairs))
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .userAgent(Constant.USER_AGENT)
                    .execute();

            System.out.println(response.body());

            return response.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
}
