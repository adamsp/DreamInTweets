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

package nz.net.speakman.android.dreamintweets.twitterstream;

import java.util.LinkedList;

import nz.net.speakman.android.dreamintweets.DreamApplication;
import nz.net.speakman.android.dreamintweets.R;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class TwitterStreamAdapter extends BaseAdapter {
    
    private class TwitterStreamViewHolder {
        TextView author;
        TextView content;
        NetworkImageView authorImage;
    }

    private static final int MAX_NUMBER_OF_TWEETS = 50;

    LinkedList<Status> mTweets = new LinkedList<Status>();
    private Context mContext;

    public TwitterStreamAdapter(Context context) {
        mContext = context;

    }

    public void onNewStatus(Status status) {
        synchronized (mTweets) {
            mTweets.addFirst(status);
            if (mTweets.size() > MAX_NUMBER_OF_TWEETS) {
                mTweets.removeLast();
            }
        }
        notifyDataSetChanged();
    }

    /*
     * Adapter methods.
     */

    @Override
    public int getCount() {
        return mTweets.size();
    }

    @Override
    public Status getItem(int position) {
        return mTweets.get(position);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Status tweet = getItem(position);
        
        TwitterStreamViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.tweet_row, parent, false);
            holder = new TwitterStreamViewHolder();
            holder.author = (TextView) convertView
                    .findViewById(R.id.tweet_author);
            holder.content = (TextView) convertView
                    .findViewById(R.id.tweet_content);
            holder.authorImage = (NetworkImageView) convertView
                    .findViewById(R.id.tweet_author_image);
            convertView.setTag(holder);
        } else {
            holder = (TwitterStreamViewHolder) convertView.getTag();
        }
        holder.author.setText(tweet.getUser().getName() + " / "
                + tweet.getUser().getScreenName());
        holder.content.setText(getTweetText(tweet));
        holder.authorImage.setImageUrl(tweet.getUser().getBiggerProfileImageURLHttps(), getImageLoader());
        return convertView;
    }
    
    private String getTweetText(Status tweet) {
        String text = tweet.getText();

        for(URLEntity urlEntity : tweet.getURLEntities()) {
            // TODO Make clickable
            text = text.replace(urlEntity.getURL(), urlEntity.getDisplayURL());
        }
        
        for(MediaEntity mediaEntity : tweet.getMediaEntities()) {
            // TODO Make clickable
            text = text.replace(mediaEntity.getURL(), mediaEntity.getDisplayURL());
        }
        
        for(HashtagEntity hashtagEntity : tweet.getHashtagEntities()) {
            // TODO Make clickable
        }
        return text;
    }
    
    private ImageLoader getImageLoader() {
        return ((DreamApplication)mContext.getApplicationContext()).getImageLoader();
    }

}
