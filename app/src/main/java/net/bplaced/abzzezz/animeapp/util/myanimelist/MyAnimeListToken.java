/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 02.06.21, 16:30
 */

package net.bplaced.abzzezz.animeapp.util.myanimelist;

import org.json.JSONException;
import org.json.JSONObject;

public class MyAnimeListToken {

    private final String refreshToken;
    private final String accessToken;
    private final String tokenType;
    private final long expiration;

    public MyAnimeListToken(final String accessToken, final String refreshToken, final String tokenType, final long expiration) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.expiration = expiration;
    }

    public MyAnimeListToken(final JSONObject jsonObject) throws JSONException {
        this.refreshToken = jsonObject.getString("access_T");
        this.accessToken = jsonObject.getString("refresh_T");
        this.tokenType = jsonObject.getString("t_type");
        this.expiration = jsonObject.getLong("exp");
    }

    @Override
    public String toString() {
        try {
            return new JSONObject()
                    .put("access_T", accessToken)
                    .put("refresh_T", refreshToken)
                    .put("t_type", tokenType)
                    .put("exp", expiration)
                    .toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONObject().toString();
        }
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiration() {
        return expiration;
    }
}
