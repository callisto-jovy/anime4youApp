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
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class MyAnimeListTokenTask extends TaskExecutor implements Callable<Optional<MyAnimeListToken>>, MyAnimeListHolder {

    private final String username, password;

    public MyAnimeListTokenTask(final String username, final String password) {
        this.username = username;
        this.password = password;
    }

    public void executeAsync(Callback<Optional<MyAnimeListToken>> callback) {
        super.executeAsync(this, callback);
    }

    @Override
    public Optional<MyAnimeListToken> call() {
        final Map<String, String> args = new HashMap<>();
        args.put(GRANT_TYPE_PASSWORD[0], GRANT_TYPE_PASSWORD[1]);
        args.put(CLIENT_ID[0], CLIENT_ID[1]);
        args.put("username", username);
        args.put("password", password);
        final Document document;
        try {
            document = Jsoup
                    .connect(MAL_API_TOKEN)
                    .headers(ArrayHelper.stringArrayToMap(TOKEN_HEADERS))
                    .data(args)
                    .ignoreContentType(true)
                    .userAgent(Constant.USER_AGENT)
                    .post();

            final JSONObject tokenObject = new JSONObject(document.text());

            final MyAnimeListToken token = new MyAnimeListToken
                    (
                            tokenObject.getString("access_token"),
                            tokenObject.getString("refresh_token"), tokenObject.getString("token_type"),
                            (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(tokenObject.getInt("expires_in")))
                    );

            return Optional.of(token);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
