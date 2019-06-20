package com.weexbox.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weexbox.core.R;
import com.weexbox.core.adapter.PageAdapter;
import com.weexbox.core.adapter.PhotoViewPager;
import com.weexbox.core.controller.WBBaseActivity;
import com.weexbox.core.util.ImageUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Author:leon.wen
 * Time:2018/8/2   18:45
 * Description:This is PhotoActivity
 */
public class PhotoActivity extends WBBaseActivity {
    TextView page;
    RelativeLayout top;
    PhotoViewPager viewPager;
    TextView save;
    ImageView back;
    ArrayList<String> imagesUrl;
    int current;
    PageAdapter pagerAdapter;
    public static OnItemClickListener onItemClickListener;
    public static View rightView;


    public void initData() {
        if (getIntent() != null) {
            Intent intent = getIntent();
            imagesUrl = intent.getStringArrayListExtra("images");
            current = intent.getIntExtra("position", 0);
        }
        pagerAdapter = new PageAdapter(imagesUrl, this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(current);
        page.setText(current + 1 + "/" + imagesUrl.size());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                current = position;
                page.setText(current + 1 + "/" + imagesUrl.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    /**
     * 保存图片
     */
    private void saveImage() {
        final String url = imagesUrl.get(current);
        getLoadDialogHelper().showLoad(PhotoActivity.this, true);
        ImageUtil.insertImageToSystemGallery(this, url, new ImageUtil.OnSaveImageListener() {
            @Override
            public void onSuccess(File file, int requestId) {
                getLoadDialogHelper().clear();
            }

            @Override
            public void onFail(int requestId, int errorMyCode, int errorBackCode, String errorMessage, String data) {
                getLoadDialogHelper().clear();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getRouter().setNavBarHidden(true);
        setContentView(R.layout.activity_photoview);
        page = findViewById(R.id.page);
        top = findViewById(R.id.top);
        viewPager = findViewById(R.id.viewPager);
        back = findViewById(R.id.back);
        save = findViewById(R.id.save);
        LinearLayout right_view = findViewById(R.id.right_view);
        if(rightView!=null){
            save.setVisibility(View.GONE);
            right_view.addView(rightView);
            rightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.rightBtnClick(v);
                    } else {
                        saveImage();
                    }
                }
            });
        }
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.rightBtnClick(v);
                } else {
                    saveImage();
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.leftBtnClick(v);
                }
                finish();
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });
        initData();
    }

    public interface OnItemClickListener {
        void leftBtnClick(View v);

        void rightBtnClick(View v);
    }
}
