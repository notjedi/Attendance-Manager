package com.attendancemanager.view;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.attendancemanager.R;

import org.jetbrains.annotations.NotNull;

public class AboutActivity extends AppCompatActivity {

    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            /* Delayed removal of status and navigation bar */
            /* https://developer.android.com/training/system-ui/immersive */
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        mContentView = findViewById(R.id.fullscreen_content);
        TextView projectText = findViewById(R.id.about_project);
        TextView iconCreditsText = findViewById(R.id.icon_credits_text);

        /* Removing underline for projectText */
        Spannable spannable = (Spannable) Html.fromHtml(getString(R.string.open_source_project), Html.FROM_HTML_MODE_LEGACY);
        URLSpan urlSpan = spannable.getSpans(0, spannable.length(), URLSpan.class)[0];
        spannable.setSpan(new UnderlineSpan() {
            public void updateDrawState(@NotNull TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        }, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0);
        projectText.setText(spannable);
        projectText.setMovementMethod(LinkMovementMethod.getInstance());

        /* Removing underline for iconCreditsText */
        spannable = (Spannable) Html.fromHtml(getString(R.string.icon_credits), Html.FROM_HTML_MODE_LEGACY);
        urlSpan = spannable.getSpans(0, spannable.length(), URLSpan.class)[0];
        spannable.setSpan(new UnderlineSpan() {
            public void updateDrawState(@NotNull TextPaint textPaint) {
                textPaint.setUnderlineText(false);
            }
        }, spannable.getSpanStart(urlSpan), spannable.getSpanEnd(urlSpan), 0);
        iconCreditsText.setText(spannable);
        iconCreditsText.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        delayedHide();
    }

    private void delayedHide() {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, 1000);
    }
}