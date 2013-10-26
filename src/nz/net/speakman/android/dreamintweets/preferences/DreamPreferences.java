/**
 * Copyright 2013 Adam Speakman
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.net.speakman.android.dreamintweets.preferences;

import twitter4j.auth.AccessToken;
import android.content.Context;
import android.content.SharedPreferences;

public class DreamPreferences {
    private static final String DREAM_PREFS_NAME = "nz.net.speakman.android.dreamintweets.preferences";
    private static final String KEY_ACCESS_TOKEN = "accessToken";
    private static final String KEY_ACCESS_TOKEN_SECRET = "accessTokenSecret";
    private static final String KEY_ACCESS_TOKEN_USER_ID = "accessTokenUserId";
    private Context mContext;

    public DreamPreferences(Context context) {
        mContext = context;
    }

    private SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(DREAM_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public void putAccessToken(AccessToken token) {
        getPrefs().edit().putString(KEY_ACCESS_TOKEN, token.getToken())
                .putString(KEY_ACCESS_TOKEN_SECRET, token.getTokenSecret())
                .putLong(KEY_ACCESS_TOKEN_USER_ID, token.getUserId()).commit();
    }

    public AccessToken getAccessToken() {
        SharedPreferences prefs = getPrefs();
        String token = prefs.getString(KEY_ACCESS_TOKEN, "");
        String tokenSecret = prefs.getString(KEY_ACCESS_TOKEN_SECRET, "");
        long userId = prefs.getLong(KEY_ACCESS_TOKEN_USER_ID, 0);
        AccessToken at = new AccessToken(token, tokenSecret, userId);
        return at;
    }

    public boolean userIsLoggedIn() {
        SharedPreferences prefs = getPrefs();
        if (prefs.contains(KEY_ACCESS_TOKEN)
                && prefs.contains(KEY_ACCESS_TOKEN_SECRET)
                && prefs.contains(KEY_ACCESS_TOKEN_USER_ID)) {
            return true;
        }
        return false;
    }

    public void logoutUser() {
        getPrefs().edit().remove(KEY_ACCESS_TOKEN)
                .remove(KEY_ACCESS_TOKEN_SECRET)
                .remove(KEY_ACCESS_TOKEN_USER_ID).commit();
    }

}
