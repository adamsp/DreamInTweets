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

import java.lang.ref.WeakReference;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;

public class TwitterStreamListener extends Handler implements UserStreamListener {
    
    private static final int NEW_STATUS = 0;
    private WeakReference<TwitterStreamAdapter> mAdapter;
    
    public TwitterStreamListener(TwitterStreamAdapter adapter) {
        super(Looper.getMainLooper());
        mAdapter = new WeakReference<TwitterStreamAdapter>(adapter);
    }
    
    @Override
    public void handleMessage(Message msg) {
        TwitterStreamAdapter adapter = mAdapter.get();
        if (adapter == null) return;
        
        switch(msg.what) {
        case NEW_STATUS:
            adapter.onNewStatus((Status)msg.obj);
            break;
        default:
            break;
        }
    }    

    @Override
    public void onDeletionNotice(StatusDeletionNotice arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onScrubGeo(long arg0, long arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStallWarning(StallWarning arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onStatus(Status status) {
        Message m = obtainMessage(NEW_STATUS, status);
        m.sendToTarget();
    }

    @Override
    public void onTrackLimitationNotice(int arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onException(Exception arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onBlock(User arg0, User arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDeletionNotice(long arg0, long arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onDirectMessage(DirectMessage arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFavorite(User arg0, User arg1, Status arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFollow(User arg0, User arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onFriendList(long[] arg0) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUnblock(User arg0, User arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUnfavorite(User arg0, User arg1, Status arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListCreation(User arg0, UserList arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListDeletion(User arg0, UserList arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListMemberAddition(User arg0, User arg1, UserList arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListMemberDeletion(User arg0, User arg1, UserList arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListSubscription(User arg0, User arg1, UserList arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListUnsubscription(User arg0, User arg1, UserList arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserListUpdate(User arg0, UserList arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUserProfileUpdate(User arg0) {
        // TODO Auto-generated method stub
        
    }

}
