package com.weexbox.example;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.weexbox.core.controller.WBWeexFragment;

import org.jetbrains.annotations.Nullable;

public class MainFragment extends WBWeexFragment {

    // 容器
    // 避免多次创建
    protected FrameLayout mRootView;

    private boolean mOnCreate = false;
    protected boolean mOnResume = false;

    public MainFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null == mRootView) {
            mRootView = new FrameLayout(container.getContext());
        }

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mRootView) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
    }

    @Override
    public void onAddWeexView(View view) {
        if (mRootView != null){
            if (view.getParent() == null) {
                mRootView.addView(view);
            }
            mRootView.requestLayout();
        }
    }
}
