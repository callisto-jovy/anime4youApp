/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 18:11
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

public interface MyAnimeListHolder {

    String MAL_API_OAUTH = "https://myanimelist.net/v1/oauth2/authorize?response_type=code&client_id=%s&state=%s&code_challenge=%s&code_challenge_method=plain";
    //Client id, oauth2 state, pkce challenge code
    String MAL_API_TOKEN = "https://api.myanimelist.net/v2/auth/token";
    String MAL_API_TOKEN_REFRESH = "https://myanimelist.net/v1/oauth2/token";
    String MAL_API_SHOW = "https://api.myanimelist.net/v2/anime/%s/my_list_status";

    String[] GRANT_TYPE_PASSWORD = new String[]{"grant_type", "password"};

    String[] GRANT_TYPE_TOKEN_REFRESH = new String[]{"grant_type", "refresh_token"};

    String[] CLIENT_ID = new String[]{"client_id", "6114d00ca681b7701d1e15fe11a4987e"};
    String[] X_CLIENT = new String[]{"X-MAL-Client-ID", "6114d00ca681b7701d1e15fe11a4987e"};

    String[][] TOKEN_HEADERS = new String[][]{new String[]{X_CLIENT[0], X_CLIENT[1]}};

    String[] AUTHORIZATION = new String[]{"authorization", "%s %s"};

}
