package com.weexbox.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.weexbox.core.controller.WBWeexFragment;


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
        mOnCreate = true;
        if (mOnResume) {
            mOnResume = false;
            doFragmentResume();
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
        if (mRootView != null) {
            if (view.getParent() == null) {
                mRootView.addView(view);
            }
            mRootView.requestLayout();
        }
    }

    @Override
    public void doFragmentResume() {
        super.doFragmentResume();
        if (!mOnResume) {
            mOnResume = true;
            if (mOnCreate) {
                onFragmentResume();
            }
        }
    }

    @Override
    public void doFragmentPause() {
        super.doFragmentPause();
        if (mOnResume) {
            mOnResume = false;
            if (mOnCreate) {
                onFragmentPause();
            }
        }
    }
}
