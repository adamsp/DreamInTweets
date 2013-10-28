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

package nz.net.speakman.android.dreamintweets.activities;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import nz.net.speakman.android.dreamintweets.DreamApplication;
import nz.net.speakman.android.dreamintweets.R;
import nz.net.speakman.android.dreamintweets.preferences.DreamPreferences;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION_CODES;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!userIsLoggedIn()) {
            startSignInActivity();
            return;
        }
        setContentView(R.layout.activity_main);
        showScreenName();
    }

    public void onSignOutButtonClick(View v) {
        new DreamPreferences(this).logoutUser();
        startSignInActivity();
    }

    @SuppressLint("InlinedApi")
    public void onSettingsButtonClick(View v) {
        Intent intent;
        if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
            intent = new Intent(Settings.ACTION_DREAM_SETTINGS);
        } else {
            intent = new Intent(Settings.ACTION_DISPLAY_SETTINGS);
        }
        startActivity(intent);
    }

    private void startSignInActivity() {
        Intent i = new Intent(this, SignInActivity.class);
        startActivity(i);
        finish();
    }

    private boolean userIsLoggedIn() {
        return new DreamPreferences(this).userIsLoggedIn();
    }

    private void showScreenName() {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                String screenname = null;
                try {
                    Twitter twitter = ((DreamApplication)getApplication()).getTwitter();
                    twitter.setOAuthAccessToken(new DreamPreferences(MainActivity.this).getAccessToken());
                    screenname = twitter.getScreenName();
                } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                } catch (TwitterException e) {
                    // TODO Auto-generated catch block
                }
                return screenname;
            }
            
            @Override
            protected void onPostExecute(String result) {
                TextView tv = (TextView) findViewById(R.id.signed_in_as_text);
                if (tv != null && result != null) {
                    tv.setText(getString(R.string.main_activity_signed_in_as,
                            result));
                    tv.setVisibility(View.VISIBLE);
                }
            }

        }.execute();
    }

}
