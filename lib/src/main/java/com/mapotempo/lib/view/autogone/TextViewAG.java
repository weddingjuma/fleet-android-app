package com.mapotempo.lib.view.autogone;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * TextViewAG
 * This TextViewAG automatically gone after setText calling, if string is empty.
 */
public class TextViewAG extends AppCompatTextView {

    TextViewAG(Context context) {
        super(context);
    }

    public TextViewAG(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.textViewStyle);
    }

    public TextViewAG(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (isEmptyTextView())
            setVisibility(GONE);
        else
            setVisibility(VISIBLE);
    }

    private boolean isEmptyTextView() {
        return !(getText().toString().trim().length() > 0);
    }
}
