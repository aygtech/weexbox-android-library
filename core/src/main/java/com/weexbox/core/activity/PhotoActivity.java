package com.weexbox.core.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.weexbox.core.R;
import com.weexbox.core.adapter.PageAdapter;
import com.weexbox.core.adapter.PhotoViewPager;
import com.weexbox.core.util.ImageUtil;

import java.util.ArrayList;

/**
 * Author:leon.wen
 * Time:2018/8/2   18:45
 * Description:This is PhotoActivity
 */
public class PhotoActivity extends AppCompatActivity {
    TextView page;
    RelativeLayout top;
    PhotoViewPager viewPager;
    TextView save;
    ImageView back;
    ArrayList<String> imagesUrl;
    int current;
    PageAdapter pagerAdapter;

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
        ImageUtil.insertImageToSystemGallery(this, url);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        setContentView(R.layout.activity_photoview);
        page = (TextView) findViewById(R.id.page);
        top = (RelativeLayout) findViewById(R.id.top);
        viewPager = (PhotoViewPager) findViewById(R.id.viewPager);
//        back = findViewById(R.id.back);
        save = (TextView) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        initData();
    }
}
