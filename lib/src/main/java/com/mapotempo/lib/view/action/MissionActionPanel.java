package com.mapotempo.lib.view.action;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapotempo.lib.R;

public class MissionActionPanel extends LinearLayout {

    private View rootView;
    private ImageView mActionImageView;
    private TextView mActionTextView;

    public MissionActionPanel(Context context) {
        super(context);
        init(context);
    }

    public MissionActionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        rootView = inflate(context, R.layout.view_mission_action_button, this);
        mActionImageView = rootView.findViewById(R.id.icon_view);
        mActionTextView = rootView.findViewById(R.id.description_view);
    }

    public final void setText(CharSequence text) {
        mActionTextView.setText(text);
    }

    public void setImageDrawable(Drawable drawable) {
        mActionImageView.setImageDrawable(drawable);
    }
}

