package com.mapotempo.lib.view.action;

import android.content.Context;
import android.content.res.TypedArray;
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
    private boolean mShowLabel = true;

    public MissionActionPanel(Context context) {
        super(context);
        init(context);
    }

    public MissionActionPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MissionActionPanel,
                0, 0);

        try {
            mShowLabel = a.getBoolean(R.styleable.MissionActionPanel_showLabel, true);
        } finally {
            a.recycle();
        }

        init(context);
    }

    private void init(Context context) {
        setClickable(true);
        setOrientation(VERTICAL);
        rootView = inflate(context, R.layout.view_mission_action_panel, this);

        mActionImageView = rootView.findViewById(R.id.icon_view);
        mActionTextView = rootView.findViewById(R.id.description_view);

        if (mShowLabel)
            mActionTextView.setVisibility(VISIBLE);
        else
            mActionTextView.setVisibility(GONE);
    }

    public final void setText(CharSequence text) {
        mActionTextView.setText(text);
    }

    public void setImageDrawable(Drawable drawable) {
        mActionImageView.setImageDrawable(drawable);
    }
}

