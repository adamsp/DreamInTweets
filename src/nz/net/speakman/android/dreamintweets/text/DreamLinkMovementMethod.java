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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Browser;
import android.service.dreams.DreamService;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * A LinkMovementMethod extension that handles clicking on links in a DreamService.
 */
public class DreamLinkMovementMethod extends LinkMovementMethod {
    
    private DreamService mDream;

    public DreamLinkMovementMethod(DreamService dream) {
        mDream = dream;
    }
    
    // Sourced from https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/text/method/LinkMovementMethod.java
    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
            action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);

            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);

            if (link.length != 0) {
                if (action == MotionEvent.ACTION_UP) {
                    onClick(link[0], widget);
                } else if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                                           buffer.getSpanStart(link[0]),
                                           buffer.getSpanEnd(link[0]));
                }

                return true;
            } else {
                Selection.removeSelection(buffer);
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }
    
    // Behaviour sourced from: https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/text/style/URLSpan.java
    private void onClick(ClickableSpan link, View widget) {
        if (link instanceof URLSpan) {
            Uri uri = Uri.parse(((URLSpan) link).getURL());
            Context context = widget.getContext();
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            String packageName = context.getPackageName();
            intent.putExtra(Browser.EXTRA_APPLICATION_ID, packageName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            mDream.finish();
        } else {
            link.onClick(widget);
        }
    }
}
