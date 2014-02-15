package nz.net.speakman.android.dreamintweets;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Application;

public class DreamApplication extends Application {

    private TwitterStream mTwitterStream;
    private Twitter mTwitter;

    @Override
    public void onCreate() {
        super.onCreate();
        mTwitter = new TwitterFactory(getConf()).getInstance();
        mTwitterStream = new TwitterStreamFactory(getConf()).getInstance();
    }

    public Twitter getTwitter() {
        return mTwitter;
    }

    public TwitterStream getTwitterStream() {
        return mTwitterStream;
    }

    private static Configuration getConf() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        // Create a DreamTwitterConstants class with these fields and your
        // Consumer Key & Secret.
        // This file is excluded in the .gitignore in order to prevent
        // accidentally checking it in.
        cb.setOAuthConsumerKey(DreamTwitterConstants.CONSUMER_KEY);
        cb.setOAuthConsumerSecret(DreamTwitterConstants.CONSUMER_SECRET);
        return cb.build();
    }
}
