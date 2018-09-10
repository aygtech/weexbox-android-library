package com.weexbox.core.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DrawableTextView extends android.support.v7.widget.AppCompatTextView {

    public DrawableTextView(Context context) {
        super(context);
    }

    public DrawableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables != null) {
            Drawable drawableLeft = drawables[0];
            if (drawableLeft != null) {
                float textWidth = getPaint().measureText(getText().toString());
                int drawablePadding = getCompoundDrawablePadding();
                int drawableWidth = drawableLeft.getIntrinsicWidth();
                int viewPadding = getPaddingLeft() + getPaddingRight();
                float bodyWidth = textWidth + drawableWidth + drawablePadding + viewPadding;
                canvas.translate((getWidth() - bodyWidth) / 2, 0);
            }
        }
        super.onDraw(canvas);
    }
}
