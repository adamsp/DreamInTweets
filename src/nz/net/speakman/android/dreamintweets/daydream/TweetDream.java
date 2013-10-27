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
import nz.net.speakman.android.dreamintweets.preferences.DreamPreferences;
import nz.net.speakman.android.dreamintweets.twitterstream.TwitterStreamAdapter;
import nz.net.speakman.android.dreamintweets.twitterstream.TwitterStreamListener;
import twitter4j.TwitterStream;
import android.service.dreams.DreamService;
import android.widget.ListView;

public class TweetDream extends DreamService {
    
    private TwitterStream twitterStream;

    @Override
    public void onDreamingStarted() {
        super.onDreamingStarted();
        setInteractive(true);
        
        DreamPreferences prefs = new DreamPreferences(this);
        if (!prefs.userIsLoggedIn()) {
            setContentView(R.layout.dream_not_logged_in);
            return;
        }
        
        setContentView(R.layout.dream_twitter_stream);
        
        ListView lv = (ListView) findViewById(R.id.dream_twitter_stream_list_view);
        TwitterStreamAdapter streamAdapter = new TwitterStreamAdapter(this);
        lv.setAdapter(streamAdapter);

        TwitterStreamListener streamListener = new TwitterStreamListener(streamAdapter);
        twitterStream = ((DreamApplication)getApplication()).getTwitterStream();
        twitterStream.setOAuthAccessToken(prefs.getAccessToken());
        twitterStream.addListener(streamListener);
        twitterStream.user();
    }
    
    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        twitterStream.shutdown();
    }

}
