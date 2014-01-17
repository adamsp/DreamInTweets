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

package nz.net.speakman.android.dreamintweets.daydream;

import nz.net.speakman.android.dreamintweets.DreamApplication;
import nz.net.speakman.android.dreamintweets.R;
import nz.net.speakman.android.dreamintweets.activities.SignInActivity;
import nz.net.speakman.android.dreamintweets.preferences.DreamPreferences;
import nz.net.speakman.android.dreamintweets.twitterstream.TwitterStreamAdapter;
import nz.net.speakman.android.dreamintweets.twitterstream.TwitterStreamListener;
import twitter4j.TwitterStream;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.service.dreams.DreamService;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class TweetDream extends DreamService {
    
    private TwitterStream twitterStream;
    private BroadcastReceiver mConnectivityBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)) {
                displayConnectivityIssues();
            } else {
                displayTweets();
            }
        }
    };
    private IntentFilter mConnectivityBroadcastFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    
    /**
     * <p>
     * Because the {@code ConnectivityManager.CONNECTIVITY_ACTION} broadcast is apparently only sticky on 
     * <i>some</i> devices (and I can't find any documentation to the contrary), we need to track whether 
     * we're currently displaying tweets or not.
     * </p>
     * See this link: http://stackoverflow.com/a/16428823/1217087
     */
    private boolean mDisplayingTweets = false;
    
    private DreamPreferences mPreferences;
    
    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        setInteractive(true);
        
        mPreferences = new DreamPreferences(this);

        if (!mPreferences.userIsLoggedIn()) {
            displayNotLoggedIn();
            return;
        }
        
        if (isConnected()) {
            displayTweets();
        } else {
            displayConnectivityIssues();
        }
        
        registerForConnectivityChangeBroadcasts();

    }
    
    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        unregisterForConnectivityChangeBroadcasts();
        // As of 4.4 KitKat, have to close the stream off the UI thread.
        // Also, it now appears to take up to 30 seconds to return.
        // http://stackoverflow.com/q/20306498/1217087
        if(twitterStream != null) {
            new AsyncTask<TwitterStream, Void, Void>() {
                @Override protected Void doInBackground(TwitterStream... params) {
                    params[0].shutdown();
                    return null;
                }
            }.execute(twitterStream);
        }
    }
    
    private void displayNotLoggedIn() {
        setContentView(R.layout.dream_not_signed_in);
        findViewById(R.id.not_signed_in_container).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(TweetDream.this, SignInActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });
    }
    
    private void displayConnectivityIssues() {
        mDisplayingTweets = false;
        setContentView(R.layout.dream_no_connectivity);
    }
    
    private void displayTweets() {
        if (mDisplayingTweets) {
            return;
        }
        mDisplayingTweets = true;
        setContentView(R.layout.dream_twitter_stream);
        
        ListView lv = (ListView) findViewById(R.id.dream_twitter_stream_list_view);
        TwitterStreamAdapter streamAdapter = new TwitterStreamAdapter(this);
        lv.setAdapter(streamAdapter);

        TwitterStreamListener streamListener = new TwitterStreamListener(streamAdapter);
        twitterStream = ((DreamApplication)getApplication()).getTwitterStream();
        twitterStream.setOAuthAccessToken(mPreferences.getAccessToken());
        twitterStream.addListener(streamListener);
        twitterStream.user();
    }
    
    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                              activeNetwork.isConnectedOrConnecting();
        return isConnected;
    }
    
    private void registerForConnectivityChangeBroadcasts() {
        registerReceiver(mConnectivityBroadcastReceiver, mConnectivityBroadcastFilter);
    }
    
    private void unregisterForConnectivityChangeBroadcasts() {
        unregisterReceiver(mConnectivityBroadcastReceiver);        
    }

}
