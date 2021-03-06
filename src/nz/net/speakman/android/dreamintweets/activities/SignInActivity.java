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

import nz.net.speakman.android.dreamintweets.DreamApplication;
import nz.net.speakman.android.dreamintweets.R;
import nz.net.speakman.android.dreamintweets.preferences.DreamPreferences;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Browser;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SignInActivity extends DreamActivity {
    
    private WebView mWebView;
    private RequestToken mRequestToken;
    private AccessToken mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mWebView = (WebView) findViewById(R.id.sign_in_web_view);
    }
    
    public void onSignInButtonClick(View view) {
        mWebView.setVisibility(View.VISIBLE);
        new FetchRequestToken().execute();
    }
    
    public void onSignUpButtonClick(View view) {
        // For now, we launch into another app to do sign up. This is unlikely to be a common use case.
        Uri uri = Uri.parse("https://twitter.com/signup");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        String packageName = getPackageName();
        intent.putExtra(Browser.EXTRA_APPLICATION_ID, packageName);
        startActivity(intent);
    }
    
    private void onRequestTokenAcquired() {
        // TODO Handle Twitter being down in a nicer way than this.
        if (mRequestToken == null) {
            mWebView.setVisibility(View.INVISIBLE);
            return;
        }
        mWebView.setWebViewClient(new WebViewClient() {
            
            @Override
            public void onPageStarted(WebView view, String url,
                    Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.startsWith("http://speakman.net.nz/dreamintweets/android/callback")) {
                    view.stopLoading();
                    Uri uri = Uri.parse(url);
                    new FetchAccessToken().execute(uri.getQueryParameter("oauth_verifier"));
                }
            }
            
        });
        mWebView.loadUrl(mRequestToken.getAuthorizationURL());
    }
    
    private void onAccessTokenAcquired() {
        DreamPreferences prefs = new DreamPreferences(this);
        prefs.putAccessToken(mAccessToken);
        ((DreamApplication)getApplication()).getTwitter().setOAuthAccessToken(mAccessToken);
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
    
    class FetchRequestToken extends AsyncTask<Void, Void, RequestToken> {

        @Override
        protected RequestToken doInBackground(Void... params) {
            try {
                RequestToken requestToken = ((DreamApplication)getApplication()).getTwitter().getOAuthRequestToken();
                return requestToken;
            } catch (TwitterException e) {
                // TODO Handle Twitter being down in a nicer way than this.
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(RequestToken requestToken) {
            super.onPostExecute(requestToken);
            mRequestToken = requestToken;
            onRequestTokenAcquired();
        }
        
    }
    
    class FetchAccessToken extends AsyncTask<String, Void, AccessToken> {
        
        @Override
        protected AccessToken doInBackground(String... params) {
            try {
                AccessToken at = ((DreamApplication)getApplication()).getTwitter().getOAuthAccessToken(mRequestToken, params[0]);
                return at;
            } catch (TwitterException e) {
                // TODO Handle Twitter being down in a nicer way than this.
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(AccessToken accessToken) {
            super.onPostExecute(accessToken);
            mAccessToken = accessToken;
            onAccessTokenAcquired();
        }
    }

}
