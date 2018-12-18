package com.weexbox.core.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.weexbox.core.R;
import com.weexbox.core.util.BitmapUtil;

import org.jetbrains.annotations.NotNull;

public class SimpleToolbar extends LinearLayout {

    private TextView mBackBtn;
    private TextView mBackBtn2;
    private TextView mNextBtn2;
    private TextView mNextBtn;
    private TextView mTitle;
    private LinearLayout action_bar_center_layout;
    private RelativeLayout actionbar_layout;
    private View statusbar_layout, bottom_line;

    private boolean refreshPadding = false;

    public SimpleToolbar(Context context) {
        super(context);
    }

    public SimpleToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mBackBtn    = findViewById(R.id.btnBack);
        mBackBtn2 = findViewById(R.id.btnBack2);
        mNextBtn2 = findViewById(R.id.btnNext2);
        mNextBtn = findViewById(R.id.btnNext);
        mTitle = findViewById(R.id.title);
        action_bar_center_layout = (LinearLayout) findViewById(R.id.action_bar_center_layout);
        actionbar_layout = findViewById(R.id.actionbar_layout);
        statusbar_layout = findViewById(R.id.statusbar_layout);
        bottom_line = findViewById(R.id.bottom_line);

        ViewGroup.LayoutParams layoutParams = statusbar_layout.getLayoutParams();
        layoutParams.height = getStatusBarHeight(getContext());
        statusbar_layout.setLayoutParams(layoutParams);
    }

    public void setStatusbarLayoutGone(){
        statusbar_layout.setVisibility(GONE);
    }

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        int width = r - l;
        if (mBackBtn.getWidth() + mNextBtn.getWidth() + mTitle.getWidth() > width) {
            int w = 0;
            if (mBackBtn.getVisibility() == VISIBLE) {
                if (mBackBtn2.getVisibility() == VISIBLE) {
                    w = Math.max(w, mBackBtn.getWidth() + mBackBtn2.getWidth());
                } else {
                    w = Math.max(w, mBackBtn.getWidth());
                }
            }
            if (mNextBtn.getVisibility() == VISIBLE) {
                if (mNextBtn2.getVisibility() == VISIBLE) {
                    w = Math.max(w, mNextBtn.getWidth() + mNextBtn2.getWidth());
                } else {
                    w = Math.max(w, mNextBtn.getWidth());
                }
            }
            mTitle.setPadding(w + getPaddingLeft(), getPaddingTop(),
                    w + getPaddingRight(), getPaddingBottom());
        }

        if (refreshPadding) {
            layoutTitleBarPadding();
        }
    }


    //actionbar显示与否
    public void setAcitionbarVisibility(int visibility){
        if (actionbar_layout != null){
            actionbar_layout.setVisibility(visibility);
        }
        if (bottom_line != null){
            bottom_line.setVisibility(visibility);
        }
    }

    //actionbar和statusbar显示与否
    public void setAcitionbarAndStatusbarVisibility(int visibility) {
        setVisibility(visibility);
    }

    //actionbar和statusbar的背景
    public void setAcitionbarAndStatusbarBackground(String color){
         setBackgroundColor(Color.parseColor(color));
    }


    //中间title设置
    public void setTitleText(int text) {
        if (mTitle != null && text > 0){
            mTitle.setText(text);
        }
    }
    public void setTitleText(String text) {
        if (mTitle != null && text!= null){
            mTitle.setText(text);
        }
    }
    public void setTitleText(String text, Drawable icon) {
        if (mTitle == null || text == null || icon == null){
            return;
        }
        mTitle.setText(text);
        icon.setBounds(0, 0, getHeight() / 2, getHeight() / 2);
        mTitle.setCompoundDrawables(icon, null, null, null);
    }
    public void setTitleText(OnClickListener listener, String text) {
        if (mTitle == null || text == null){
            return;
        }
        mTitle.setText(text);
        mTitle.setOnClickListener(listener);
    }
    public void setTitleText(OnClickListener listener, Drawable icon) {
        if (mTitle == null || icon == null){
            return;
        }
        icon.setBounds(0, 0, getHeight() / 2, getHeight() / 2);
        mTitle.setCompoundDrawables(icon, null, null, null);
        mTitle.setOnClickListener(listener);
    }
    public void setTitleText(OnClickListener listener, String text, Drawable icon) {
        if (mTitle == null || text == null || icon == null){
            return;
        }
        mTitle.setText(text);
        icon.setBounds(0, 0, getHeight() / 2, getHeight() / 2);
        mTitle.setCompoundDrawables(icon, null, null, null);
        mTitle.setOnClickListener(listener);
    }
    public void setTitleTextColor(int color) {
        if (mTitle != null && color > 0){
            mTitle.setTextColor(color);
        }
    }
    public void setTitleTextColor(String color) {
        if (mTitle != null && !TextUtils.isEmpty(color)){
            mTitle.setTextColor(Color.parseColor(color));
        }
    }
    public void setTitleTextListener(OnClickListener listener) {
        if (mTitle != null){
            mTitle.setOnClickListener(listener);
        }
    }


    //左边第一个按钮
    public void setBackButton(OnClickListener listener) {
        if (mBackBtn != null){
            mBackBtn.setOnClickListener(listener);
        }
    }
    public void setBackButton(OnClickListener listener, String text) {
        if (mBackBtn == null || text == null){
            return;
        }
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setText(text);
        mBackBtn.setOnClickListener(listener);
    }
    public void setBackButton(OnClickListener listener, String text, String color) {
        if (mBackBtn == null || text == null || TextUtils.isEmpty(color)){
            return;
        }
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setCompoundDrawables(null, null, null, null);
        mBackBtn.setText(text);
        mBackBtn.setTextColor(Color.parseColor(color));
        mBackBtn.setOnClickListener(listener);
    }
    public void setBackButton(OnClickListener listener, String text, Drawable icon) {
        if (mBackBtn == null || text == null){
            return;
        }
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setText(text);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        mBackBtn.setCompoundDrawables(icon, null, null, null);
        mBackBtn.setOnClickListener(listener);
    }
    public void setBackButtonDrawable(OnClickListener listener, String url) {
        if (mBackBtn == null || TextUtils.isEmpty(url)){
            return;
        }
        mBackBtn.setVisibility(View.VISIBLE);
        mBackBtn.setOnClickListener(listener);
        BitmapUtil.displayImage(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                if (resource != null) {
                    resource.setBounds(0, 0, resource.getMinimumWidth(), resource.getMinimumHeight());
                }
                mBackBtn.setCompoundDrawables(resource, null, null, null);
            }
        }, url);
    }


    //左边第二个按钮
    public void setBackButton2(OnClickListener listener, String text, Drawable icon) {
        if (mBackBtn2 == null || text == null || icon == null){
            return;
        }
        mBackBtn2.setVisibility(View.VISIBLE);
        mBackBtn2.setText(text);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        mBackBtn2.setCompoundDrawables(icon, null, null, null);
        mBackBtn2.setOnClickListener(listener);
    }
    public void setBackButton2(OnClickListener listener, String text, String color) {
        if (mBackBtn2 == null || text == null || TextUtils.isEmpty(color)){
            return;
        }
        mBackBtn2.setVisibility(View.VISIBLE);
        mBackBtn2.setText(text);
        mBackBtn2.setTextColor(Color.parseColor(color));
        mBackBtn2.setOnClickListener(listener);
    }
    public void setBackButton2(OnClickListener listener, String text) {
        if (mBackBtn2 == null || text == null){
            return;
        }
        mBackBtn2.setVisibility(View.VISIBLE);
        mBackBtn2.setText(text);
        mBackBtn2.setOnClickListener(listener);
    }
    public void setBackButton2Drawable(OnClickListener listener, String url) {
        if (mBackBtn2 == null || TextUtils.isEmpty(url)){
            return;
        }
        mBackBtn2.setVisibility(View.VISIBLE);
        mBackBtn2.setOnClickListener(listener);
        BitmapUtil.displayImage(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                if (resource != null) {
                    resource.setBounds(0, 0, resource.getMinimumWidth(), resource.getMinimumHeight());
                }
                mBackBtn2.setCompoundDrawables(resource, null, null, null);
            }
        }, url);
    }


    //右边第一个按钮
    public void setRightButton(OnClickListener listener, String text) {
        if (mNextBtn == null || text == null){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setText(text);
        mNextBtn.setOnClickListener(listener);
    }
    public void setRightButtonText(String text) {
        if (mNextBtn == null || text == null){
            return;
        }
        mNextBtn.setText(text);
    }
    public void setRightButton(Drawable icon) {
        if (mNextBtn == null){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        }
        mNextBtn.setCompoundDrawables(icon, null, null, null);
    }
    public void setRightButton(OnClickListener listener, String text, int color) {
        if (mNextBtn == null || text == null){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setText(text);
        mNextBtn.setTextColor(color);
        mNextBtn.setOnClickListener(listener);
    }
    public void setRightButton(OnClickListener listener, String text, String color) {
        if (mNextBtn == null || text == null || TextUtils.isEmpty(color)){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setText(text);
        mNextBtn.setTextColor(Color.parseColor(color));
        mNextBtn.setOnClickListener(listener);
    }
    public void setRightButton(OnClickListener listener, Drawable icon) {
        if (mNextBtn == null || icon == null){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        }
        mNextBtn.setCompoundDrawables(icon, null, null, null);
        mNextBtn.setOnClickListener(listener);
    }
    public void setRightButton(OnClickListener listener, String text, Drawable icon) {
        if (mNextBtn == null || text == null || icon == null){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setText(text);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        }
        mNextBtn.setCompoundDrawables(icon, null, null, null);
        mNextBtn.setOnClickListener(listener);
    }
    public void setRightButtonDrawable(OnClickListener listener, String url) {
        if (mNextBtn == null || TextUtils.isEmpty(url)){
            return;
        }
        mNextBtn.setVisibility(View.VISIBLE);
        mNextBtn.setOnClickListener(listener);
        BitmapUtil.displayImage(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                if (resource != null) {
                    resource.setBounds(0, 0, resource.getMinimumWidth(), resource.getMinimumHeight());
                }
                mNextBtn.setCompoundDrawables(resource, null, null, null);
            }
        }, url);
    }


    //右边第二个按钮
    public void setRightButton2(OnClickListener listener, String text) {
        if (mNextBtn2 == null || text == null){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setText(text);
        mNextBtn2.setOnClickListener(listener);
    }
    public void setRightButtonText2(String text) {
        if (mNextBtn2 == null || text == null){
            return;
        }
        mNextBtn2.setText(text);
    }
    public void setRightButton2(OnClickListener listener, String text, int color) {
        if (mNextBtn2 == null || text == null){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setText(text);
        mNextBtn2.setTextColor(color);
        mNextBtn2.setOnClickListener(listener);
    }
    public void setRightButton2(OnClickListener listener, String text, Drawable icon) {
        if (mNextBtn2 == null || text == null || icon == null){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setText(text);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        mNextBtn2.setCompoundDrawables(icon, null, null, null);
        mNextBtn2.setOnClickListener(listener);
    }
    public void setRightButton2Listener(OnClickListener listener) {
        if (mNextBtn2 != null){
            mNextBtn2.setOnClickListener(listener);
        }
    }
    public void setRightButton2TextAndIcon(String text, Drawable icon, Drawable bg) {
        if (mNextBtn2 == null || text == null || icon == null || bg == null){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setText(text);
        if (icon != null) {
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        }
        mNextBtn2.setCompoundDrawables(icon, null, null, null);
        mNextBtn2.setBackgroundDrawable(bg);
    }
    public void setRightButton2(OnClickListener listener, String text, String color) {
        if (mNextBtn2 == null || text == null || TextUtils.isEmpty(color)){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setText(text);
        mNextBtn2.setTextColor(Color.parseColor(color));
        mNextBtn2.setOnClickListener(listener);
    }
    public void setRightButton2Drawable(OnClickListener listener, String url) {
        if (mNextBtn2 == null || TextUtils.isEmpty(url)){
            return;
        }
        mNextBtn2.setVisibility(View.VISIBLE);
        mNextBtn2.setOnClickListener(listener);
        BitmapUtil.displayImage(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition transition) {
                if (resource != null) {
                    resource.setBounds(0, 0, resource.getMinimumWidth(), resource.getMinimumHeight());
                }
                mNextBtn2.setCompoundDrawables(resource, null, null, null);
            }
        }, url);
    }



    public void setRefreshPadding(boolean refreshPadding) {
        this.refreshPadding = refreshPadding;
    }

    public void layoutTitleBarPadding() {
        int w = 0;
        MarginLayoutParams marginLayoutParams;
        if (mBackBtn.getVisibility() == VISIBLE) {
            marginLayoutParams = (MarginLayoutParams) mBackBtn.getLayoutParams();
            int mBackMargin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            if (mBackBtn2.getVisibility() == VISIBLE) {
                marginLayoutParams = (MarginLayoutParams) mBackBtn2.getLayoutParams();
                int mBack2Margin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                w = Math.max(w, mBackBtn.getWidth() + mBackBtn2.getWidth() + mBackMargin + mBack2Margin);
            } else {
                w = Math.max(w, mBackBtn.getWidth() + mBackMargin);
            }
        }
        if (mNextBtn.getVisibility() == VISIBLE) {
            marginLayoutParams = (MarginLayoutParams) mNextBtn.getLayoutParams();
            int mNextMargin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            if (mNextBtn2.getVisibility() == VISIBLE) {
                marginLayoutParams = (MarginLayoutParams) mNextBtn2.getLayoutParams();
                int mNext2Margin = marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                w = Math.max(w, mNextBtn.getWidth() + mNextBtn2.getWidth() + mNextMargin + mNext2Margin);
            } else {
                w = Math.max(w, mNextBtn.getWidth() + mNextMargin);
            }
        }

        action_bar_center_layout.setPadding(w + getPaddingLeft(), getPaddingTop(),
                w + getPaddingRight(), getPaddingBottom());
    }

}

