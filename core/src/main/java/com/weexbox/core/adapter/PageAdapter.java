package com.weexbox.core.adapter;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

/**
 * Author:leon.wen
 * Time:2018/8/2   18:42
 * Description:This is PageAdapter
 */
public class PageAdapter extends PagerAdapter {
    List<String> imagesUrl;
    Activity context;

    public PageAdapter(List<String> imagesUrl, Activity context) {
        this.imagesUrl = imagesUrl;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (imagesUrl == null || imagesUrl.size() == 0) ? 0 : imagesUrl.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String url = imagesUrl.get(position);

        PhotoView photoView = new PhotoView(context);
        photoView.setMaximumScale(8);
        photoView.setMinimumScale(0.5f);
        Glide.with(context)
                .load(url)
                .into(photoView);
        container.addView(photoView);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float v, float v1) {
                context.finish();
                context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        return photoView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
