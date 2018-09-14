package com.weexbox.core.util;

import android.app.Activity;
import android.content.Intent;


import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.tools.PictureFileUtils;

import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Author:leon.wen
 * Time:2018/8/7   11:10
 * Description:This is MultipleImage
 */
public class SelectImageUtil {

    private static MultipleImageCompleteListener mCompleteListener;
    private static final int SingleImgType = 51557;
    public static boolean mIsMultiple;
    private static int imgType = 0;//0原图，1为剪切图，2为压缩图
   // private static Activity activity;

    /**
     * 从相册多选获取图片
     * count:最多选几张
     * picked：已经选择了几张
     * isCamera：是否开启相机
     */
    public static void startImagePickActivity(Activity context,int count, int picked, boolean isCamera, MultipleImageCompleteListener callback) {
        imgType = 0;
        if (picked >= 9) {
            return;
        }
        Activity activity = context;

        if (activity == null) {
            ToastUtil.showLongToast(activity, "未知错误，请退出重试");
            return;
        }
        mCompleteListener = callback;
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .maxSelectNum(count - picked)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .previewImage(true)
                .isCamera(false)
                .sizeMultiplier(0.5f)
                .isGif(false)
                .previewEggs(true)
                .isCamera(isCamera)
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .forResult(PictureConfig.CHOOSE_REQUEST);
    }

    /**
     * 单选  w剪裁宽 h剪裁高 isCamera 是否拍照，enableCrop是否剪裁，是否圆形剪裁，否则矩形剪裁
     */
    public static void startImagePickActivity(Activity context, int w, int h, boolean isCamera, boolean enableCrop, boolean isCircle, MultipleImageCompleteListener callback) {
        imgType = 1;
        Activity activity = context;
        if (activity == null) {
            ToastUtil.showLongToast(activity, "未知错误，请退出重试");
            return;
        }
        mCompleteListener = callback;
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .imageSpanCount(4)
                .selectionMode(PictureConfig.SINGLE)
                .enableCrop(enableCrop)
                .sizeMultiplier(0.9f)
                .compress(false)
                .cropCompressQuality(100)
                .withAspectRatio(w,h)
                .circleDimmedLayer(isCircle)
                .showCropFrame(!isCircle)
                .isCamera(isCamera)
                .previewImage(false)
                .rotateEnabled(true)
                .scaleEnabled(true)
                .isGif(false)
                .forResult(SingleImgType);

    }

    public static void onActivityResult(int requestCode, int resultCode, Intent data, Activity activity) {
        switch (requestCode) {
            case PictureConfig.CHOOSE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (selectList.size() > 1) {
                        mIsMultiple = true;
                    } else {
                        mIsMultiple = false;
                    }
                    handleCropResult(selectList,activity);
                }
                break;
            case SingleImgType:
                if (resultCode == Activity.RESULT_OK) {
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    handleCropResult(selectList,activity);
                }
                break;
        }
    }

    private static void handleCropResult(final List<LocalMedia> list,final Activity activity) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int count = list.size();
                String [] imgs = new String[count];
                for (int i = 0; i < count; i++) {
                    if(imgType == 0){
                        String path = list.get(i).getPath();
                        imgs[i] = path;
                    }else{
                        String path = list.get(i).getCutPath();
                        imgs[i] = path;
                    }
                }
                PictureFileUtils.deleteCacheDirFile(activity);
                mCompleteListener.onComplete(imgs);
            }
        });
        thread.start();
    }

    public interface MultipleImageCompleteListener {
        void onComplete(String[] imgs);
    }
}
