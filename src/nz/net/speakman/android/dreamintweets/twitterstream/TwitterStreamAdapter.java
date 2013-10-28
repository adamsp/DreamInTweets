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

import java.util.Date;
import java.util.LinkedList;

import nz.net.speakman.android.dreamintweets.DreamApplication;
import nz.net.speakman.android.dreamintweets.R;
import nz.net.speakman.android.dreamintweets.text.DreamLinkMovementMethod;
import nz.net.speakman.android.dreamintweets.text.TextViewLinkHider;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.User;
import android.service.dreams.DreamService;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
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
        TextView username;
        TextView timestamp;
        TextView content;
        TextView retweetAuthor;
        View retweetIcon;
        NetworkImageView authorImage;
    }

    private static final int MAX_NUMBER_OF_TWEETS = 50;

    private static final String CLICKABLE_URL_FORMAT_STRING = "<a href=\"%1$s\">%2$s</a>";

    private static final String URL_FORMAT_TWEET_LINK = "https://twitter.com/%1$s/status/%2$s";

    private static final String URL_FORMAT_USER_LINK = "https://twitter.com/%s";

    LinkedList<Status> mTweets = new LinkedList<Status>();

    private DreamService mDream;

    public TwitterStreamAdapter(DreamService dream) {
        mDream = dream;
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
            LinkMovementMethod lmm = new DreamLinkMovementMethod(mDream);
            convertView = LayoutInflater.from(mDream).inflate(
                    R.layout.tweet_row, parent, false);
            holder = new TwitterStreamViewHolder();

            holder.authorImage = (NetworkImageView) convertView
                    .findViewById(R.id.tweet_author_image);

            holder.author = (TextView) convertView
                    .findViewById(R.id.tweet_author);
            holder.author.setMovementMethod(lmm);

            holder.username = (TextView) convertView
                    .findViewById(R.id.tweet_username);
            holder.username.setMovementMethod(lmm);

            holder.timestamp = (TextView) convertView
                    .findViewById(R.id.tweet_timestamp);
            holder.timestamp.setMovementMethod(lmm);

            holder.content = (TextView) convertView
                    .findViewById(R.id.tweet_content);
            holder.content.setMovementMethod(lmm);

            holder.retweetIcon = convertView
                    .findViewById(R.id.tweet_retweet_icon);

            holder.retweetAuthor = (TextView) convertView
                    .findViewById(R.id.tweet_retweet_author);
            holder.retweetAuthor.setMovementMethod(lmm);

            convertView.setTag(holder);
        } else {
            holder = (TwitterStreamViewHolder) convertView.getTag();
        }

        if (tweet.isRetweet()) {
            // We have a retweet - setup the retweeting users details & retweet
            // icon.
            holder.retweetAuthor.setVisibility(View.VISIBLE);
            holder.retweetAuthor.setText(getTweetAuthor(tweet));
            holder.retweetIcon.setVisibility(View.VISIBLE);

            // Then populate the rest of the view with the retweeted status.
            tweet = tweet.getRetweetedStatus();
        } else {
            holder.retweetAuthor.setVisibility(View.GONE);
            holder.retweetIcon.setVisibility(View.GONE);
        }
        
        holder.authorImage.setImageUrl(tweet.getUser()
                .getBiggerProfileImageURLHttps(), getImageLoader());
        holder.author.setText(getTweetAuthor(tweet));
        holder.username.setText(getTweetUsername(tweet));
        holder.timestamp.setText(getTweetTimestamp(tweet));
        holder.content.setText(getTweetText(tweet));

        // Hide underlining/coloring of non-content links.
        hideLinks(holder);
        
        // Hide underlining of content links
        hideLinkUnderlines(holder);

        return convertView;
    }

    private Spanned getTweetAuthor(Status tweet) {
        User user = tweet.getUser();
        return Html.fromHtml(getClickableUrl(getUserUrl(user), user.getName()));
    }

    private Spanned getTweetUsername(Status tweet) {
        User user = tweet.getUser();
        return Html.fromHtml(getClickableUrl(getUserUrl(user),
                "@" + user.getScreenName()));
    }

    private Spanned getTweetTimestamp(Status tweet) {
        Date tweetTime = tweet.getCreatedAt();
        Date now = new Date();
        Date difference = new Date(now.getTime() - tweetTime.getTime());
        long diff = difference.getTime() / 1000;
        String timestamp;
        if (diff <= 0) {
            timestamp = mDream.getString(R.string.tweet_timestamp_just_now);
        } else if (diff / (24 * 60 * 60) > 0) {
            diff = diff / (24 * 60 * 60);
            if (diff > 1) timestamp = mDream.getString(R.string.tweet_timestamp_x_days_ago, diff);
            else timestamp = mDream.getString(R.string.tweet_timestamp_1_day_ago);
        } else if (diff / (60 * 60) > 0) {
            diff = diff / (60 * 60);
            if (diff > 1) timestamp = mDream.getString(R.string.tweet_timestamp_x_hours_ago, diff);
            else timestamp = mDream.getString(R.string.tweet_timestamp_1_hour_ago);
        } else if (diff / 60 > 0) {
            diff = diff / 60;
            if (diff > 1) timestamp = mDream.getString(R.string.tweet_timestamp_x_minutes_ago, diff);
            else timestamp = mDream.getString(R.string.tweet_timestamp_1_minute_ago);
        } else {
            if (diff > 1) timestamp = mDream.getString(R.string.tweet_timestamp_x_seconds_ago, diff);
            else timestamp = mDream.getString(R.string.tweet_timestamp_1_second_ago);
        }
        
        return Html.fromHtml(getClickableUrl(getTweetUrl(tweet), timestamp));
    }

    private Spanned getTweetText(Status tweet) {
        String text = tweet.getText();

        for (URLEntity urlEntity : tweet.getURLEntities()) {
            text = text.replace(urlEntity.getURL(), getClickableUrl(urlEntity));
        }

        for (MediaEntity mediaEntity : tweet.getMediaEntities()) {
            // TODO Optionally load images into stream
            text = text.replace(mediaEntity.getURL(),
                    getClickableMedia(mediaEntity));
        }

        for (HashtagEntity hashtagEntity : tweet.getHashtagEntities()) {
            // TODO Make clickable
        }
        return Html.fromHtml(text);
    }

    private ImageLoader getImageLoader() {
        return ((DreamApplication) mDream.getApplicationContext())
                .getImageLoader();
    }

    private String getUserUrl(User user) {
        return String.format(URL_FORMAT_USER_LINK, user.getScreenName());
    }

    private String getTweetUrl(Status tweet) {
        return String.format(URL_FORMAT_TWEET_LINK, tweet.getUser()
                .getScreenName(), tweet.getId());
    }

    private String getClickableUrl(String link, String display) {
        return String.format(CLICKABLE_URL_FORMAT_STRING, link, display);
    }

    private String getClickableUrl(URLEntity entity) {
        return getClickableUrl(entity.getURL(), entity.getDisplayURL());
    }

    private String getClickableMedia(MediaEntity entity) {
        return getClickableUrl(entity);
    }

    private String getClickableHashtag(HashtagEntity entity) {
        return "";
    }
    
    private void hideLinkUnderlines(TwitterStreamViewHolder holder) {
        TextViewLinkHider linkHider = new TextViewLinkHider();
        linkHider.hideLinkUnderlines(holder.content);
    }

    private void hideLinks(TwitterStreamViewHolder holder) {
        TextViewLinkHider linkHider = new TextViewLinkHider();
        linkHider.hideLinks(holder.retweetAuthor);
        linkHider.hideLinks(holder.author);
        linkHider.hideLinks(holder.username);
        linkHider.hideLinks(holder.timestamp);
    }

}
