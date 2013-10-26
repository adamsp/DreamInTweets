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

package nz.net.speakman.android.dreamintweets.twitterprovider;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class DreamTwitter {



    private static Twitter mTwitter;
    
    private static Configuration getConf() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        // Create a DreamTwitterConstants class with these fields and your Consumer Key & Secret.
        // This file is excluded in the .gitignore in order to prevent accidentally checking it in.
        cb.setOAuthConsumerKey(DreamTwitterConstants.CONSUMER_KEY);
        cb.setOAuthConsumerSecret(DreamTwitterConstants.CONSUMER_SECRET);
        return cb.build();
    }

    public static Twitter getTwitter() {
        if (mTwitter == null) {
            mTwitter = new TwitterFactory(getConf()).getInstance();
        }
        return mTwitter;
    }

    private static TwitterStream mTwitterStream;

    public static TwitterStream getTwitterStream() {
        if (mTwitterStream == null) {
            mTwitterStream = new TwitterStreamFactory(getConf()).getInstance();
        }
        return mTwitterStream;
    }
}
