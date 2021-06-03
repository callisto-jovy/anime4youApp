/*
 * Copyright (c) 2021. Roman P.
 * All code is owned by Roman P. APIs are mentioned.
 * Last modified: 30.05.21, 18:11
 */

package net.bplaced.abzzezz.animeapp.util.provider.holders;

public interface MyAnimeListHolder {

    String MAL_API_TOKEN = "https://api.myanimelist.net/v2/auth/token"; //API endpoint to retrieve the token
    String MAL_API_TOKEN_REFRESH = "https://myanimelist.net/v1/oauth2/token"; //API endpoint to refresh the token
    String MAL_API_SHOW = "https://api.myanimelist.net/v2/anime/%s/my_list_status"; //API endpoint to update the status of an show (Format args: show id)

    String[] GRANT_TYPE_PASSWORD = new String[]{"grant_type", "password"}; //Grant type for retrieving the token with the user's username & password
    String[] GRANT_TYPE_TOKEN_REFRESH = new String[]{"grant_type", "refresh_token"}; //Grant type for refreshing the token

    String[] CLIENT_ID = new String[]{"client_id", "6114d00ca681b7701d1e15fe11a4987e"};
    String[] X_CLIENT = new String[]{"X-MAL-Client-ID", "6114d00ca681b7701d1e15fe11a4987e"}; //The official app's client id, needed for every request, otherwise a code 401 is returned (unauthorized)

    String[][] TOKEN_HEADERS = new String[][]{new String[]{X_CLIENT[0], X_CLIENT[1]}}; //Token headers

    String[] AUTHORIZATION = new String[]{"authorization", "%s %s"}; //Authorization headers, needed with every request (Format args: token type, access token)

}
