package nz.net.speakman.android.dreamintweets;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Application;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.Volley;

public class DreamApplication extends Application {

    private TwitterStream mTwitterStream;
    private Twitter mTwitter;
    private ImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();
        mImageLoader = new ImageLoader(Volley.newRequestQueue(this),
                new LruBitmapCache(getCacheSize()));
        mTwitter = new TwitterFactory(getConf()).getInstance();
        mTwitterStream = new TwitterStreamFactory(getConf()).getInstance();
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public Twitter getTwitter() {
        return mTwitter;
    }

    public TwitterStream getTwitterStream() {
        return mTwitterStream;
    }

    private int getCacheSize() {
        final DisplayMetrics displayMetrics = getResources()
                .getDisplayMetrics();
        final int screenWidth = displayMetrics.widthPixels;
        final int screenHeight = displayMetrics.heightPixels;
        final int screenBytes = screenWidth * screenHeight * 4; // 4 bytes per
                                                                // pixel

        return screenBytes * 3;
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

    // https://gist.github.com/ficusk/5614325
    public class LruBitmapCache extends LruCache<String, Bitmap> implements
            ImageCache {

        public LruBitmapCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }

        @Override
        public Bitmap getBitmap(String url) {
            return get(url);
        }

        @Override
        public void putBitmap(String url, Bitmap bitmap) {
            put(url, bitmap);
        }

    }

}
