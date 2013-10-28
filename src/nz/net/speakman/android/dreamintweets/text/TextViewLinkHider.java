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
package nz.net.speakman.android.dreamintweets.text;

import android.text.Spannable;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.widget.TextView;

/**
 * Hides links within a text view.
 */
public class TextViewLinkHider {
    
    class HiddenURLSpan extends URLSpan {

        public HiddenURLSpan(URLSpan span) {
            super(span.getURL());
        }
        
        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }
    
    class NonUnderlinedURLSpan extends URLSpan {

        public NonUnderlinedURLSpan(URLSpan span) {
            super(span.getURL());
        }
        
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
    
    /**
     * Removes underlining from any URLSpans (links) within the supplied TextView.
     */
    public void hideLinkUnderlines(TextView textView) {
        Spannable text = (Spannable) textView.getText();
        URLSpan[] spansToHide = text.getSpans(0, text.length(), URLSpan.class);
        for (URLSpan spanToHide : spansToHide) {
            hideSpan(text, spanToHide, new NonUnderlinedURLSpan(spanToHide));
        }
    }

    /**
     * Removes underlining and coloring from any URLSpans (links) within the supplied TextView.
     */
    public void hideLinks(TextView textView) {
        Spannable text = (Spannable) textView.getText();
        URLSpan[] spansToHide = text.getSpans(0, text.length(), URLSpan.class);
        for (URLSpan spanToHide : spansToHide) {
            hideSpan(text, spanToHide, new HiddenURLSpan(spanToHide));
        }
    }
    
    private void hideSpan(Spannable text, URLSpan spanToHide, URLSpan hiddenSpan) {
        int start = text.getSpanStart(spanToHide);
        int end = text.getSpanEnd(spanToHide);
        int flags = text.getSpanFlags(spanToHide);
        text.removeSpan(spanToHide);
        text.setSpan(hiddenSpan, start, end, flags);
    }

}
