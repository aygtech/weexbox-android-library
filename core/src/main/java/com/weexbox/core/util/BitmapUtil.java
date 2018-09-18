package com.weexbox.core.util;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.target.Target;
import com.weexbox.core.adapter.GlideApp;
import com.weexbox.core.adapter.GlideRequest;

import java.io.File;

/**
 * Created by freeson on 16/8/1.
 */
public class BitmapUtil {

    public static final int FIT_CENTER = 0;
    public static final int CENTER_INSIDE = 1;
    public static final int CENTER_CROP = 2;
    public static final int CIRCLE = 3;
    public static final int NO_TRANSFORMATION = 4;
    /**
     * 默认不可用的id
     ****/
    public static final int NON_EXISTENT_ID = 0;
    public static int DEFAULT = CENTER_INSIDE;
    public static Context sContext;

    public static void setContext(final Context context) {
        BitmapUtil.sContext = context;
    }

    public static void configDefaultDisplayType(final int displayType) {
        DEFAULT = displayType;
    }

    public static GlideRequest buildImageDisplayRequest(final Context context, final String url) {
        return buildGlideRequest(context, url);
    }

    public static GlideRequest buildImageDisplayRequest(final String url) {
        return buildGlideRequest(sContext, url);
    }

    public static void displayImage(final Target target, final String url) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Target target, final File file) {
        display(sContext, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final ImageView target, final String url) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final ImageView target, final File file) {
        display(sContext, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Target target, final String url, final int displayType) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, null);
    }

    public static void displayImage(final ImageView target, final String url, final int displayType) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, null);
    }

    public static void displayImage(final Target target, final String url, final
    Transformation transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Target target, final File file, final
    Transformation transformation) {
        display(sContext, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Target target, final int id, final
    Transformation transformation) {
        display(sContext, target, id, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final ImageView target, final String url, final
    Transformation transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final ImageView target, final File file, final
    Transformation transformation) {
        display(sContext, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final ImageView target, final int id, final
    Transformation transformation) {
        display(sContext, target, id, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, null);
    }

    public static void displayImage(final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, null);
    }

    public static void displayImage(final Target target, final String url, final int displayType, final Transformation
            transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, transformation);
    }

    public static void displayImage(final ImageView target, final String url, final int displayType, final Transformation
            transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, transformation);
    }

    public static void displayImage(final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, transformation);
    }

    public static void displayImage(final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, transformation);
    }

    public static void displayImage(final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId, final int displayType, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, displayType, transformation);
    }

    public static void displayImage(final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId, final int displayType, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, displayType, transformation);
    }

    /**
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Target target, final String url) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, null);
    }

    /**
     * @param target
     * @param url
     */
    public static void displayCircleImage(final ImageView target, final String url) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, null);
    }

    /**
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Target target, final String url, final Transformation transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final ImageView target, final String url, final Transformation transformation) {
        display(sContext, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, transformation);
    }


    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, null);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, null);
    }

    /**
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(sContext, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, transformation);
    }

    /**
     * @param context 可以是Activity或者Fragment
     * @param target
     * @param url
     */
    public static void displayImage(final Context context, final Target target, final String url) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Context context, final Target target, final File file) {
        display(context, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Context context, final ImageView target, final String url) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Context context, final ImageView target, final File file) {
        display(context, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, null);
    }

    public static void displayImage(final Context context, final Target target, final String url, final int displayType) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, null);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final int displayType) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, null);
    }

    public static void displayImage(final Context context, final Target target, final String url, final
    Transformation transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final Target target, final File file, final
    Transformation transformation) {
        display(context, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final
    Transformation transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final File file, final
    Transformation transformation) {
        display(context, target, "", file, NON_EXISTENT_ID, NON_EXISTENT_ID, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final Target target, final String url, final int displayType, final
    Transformation transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final int displayType, final
    Transformation transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, displayType, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, null);
    }

    public static void displayImage(final Context context, final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, null);
    }

    public static void displayImage(final Context context, final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId, final Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, DEFAULT, transformation);
    }

    public static void displayImage(final Context context, final Target target, final String url, final int defaultLoadId, final int
            defaultErrorId, final int displayType, final
                                    Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, displayType, transformation);
    }

    public static void displayImage(final Context context, final ImageView target, final String url, final int defaultLoadId, final int
            defaultErrorId, final int displayType, final
                                    Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, displayType, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Context context, final Target target, final String url) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, null);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Context context, final ImageView target, final String url) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, null);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Context context, final Target target, final String url, final Transformation
            transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Context context, final ImageView target, final String url, final Transformation
            transformation) {
        display(context, target, url, null, NON_EXISTENT_ID, NON_EXISTENT_ID, CIRCLE, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Context context, final Target target, final String url, final int defaultLoadId,
                                          final int
                                                  defaultErrorId) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, null);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     */
    public static void displayCircleImage(final Context context, final ImageView target, final String url, final int defaultLoadId,
                                          final int
                                                  defaultErrorId) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, null);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Context context, final Target target, final String url, final int defaultLoadId,
                                          final int
                                                  defaultErrorId, final Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, transformation);
    }

    /**
     * Context 用ApplicationContext
     *
     * @param target
     * @param url
     * @param transformation 如果为空，则用Glide默认的circleCrop
     */
    public static void displayCircleImage(final Context context, final ImageView target, final String url, final int defaultLoadId,
                                          final int
                                                  defaultErrorId, final Transformation transformation) {
        display(context, target, url, null, defaultLoadId, defaultErrorId, CIRCLE, transformation);
    }

    private static void display(final Context context, final Target target, final String url, File file, final int defaultLoadId, final int
            defaultErrorId,
                                final int displayType, final Transformation transformation) {
        GlideRequest request = configDisplay(context, url, file,0, displayType, transformation);
        if (defaultLoadId != NON_EXISTENT_ID) {
            request = request.placeholder(defaultLoadId);
        }
        if (defaultErrorId != NON_EXISTENT_ID) {
            request = request.error(defaultErrorId);
        }
        request.into(target);
    }

    private static void display(final Context context, final ImageView target, final String url, File file, final int defaultLoadId, final int
            defaultErrorId,
                                final int displayType, final Transformation transformation) {
        GlideRequest request = configDisplay(context, url, file,0, displayType, transformation);
        if (defaultLoadId != NON_EXISTENT_ID) {
            request = request.placeholder(defaultLoadId);
        }
        if (defaultErrorId != NON_EXISTENT_ID) {
            request = request.error(defaultErrorId);
        }
        request.into(target);
    }

    private static void display(final Context context, final ImageView target, final int id, final int defaultLoadId, final int
            defaultErrorId,
                                final int displayType, final Transformation transformation) {
        GlideRequest request = configDisplay(context, "", null,id, displayType, transformation);
        if (defaultLoadId != NON_EXISTENT_ID) {
            request = request.placeholder(defaultLoadId);
        }
        if (defaultErrorId != NON_EXISTENT_ID) {
            request = request.error(defaultErrorId);
        }
        request.into(target);
    }

    private static void display(final Context context, final Target target, final int id,  final int defaultLoadId, final int
            defaultErrorId,
                                final int displayType, final Transformation transformation) {
        GlideRequest request = configDisplay(context, "", null,id, displayType, transformation);
        if (defaultLoadId != NON_EXISTENT_ID) {
            request = request.placeholder(defaultLoadId);
        }
        if (defaultErrorId != NON_EXISTENT_ID) {
            request = request.error(defaultErrorId);
        }
        request.into(target);
    }

    private static GlideRequest configDisplay(final Context context, final String url, File file,int id, final int displayType, final Transformation
            transformation) {
        GlideRequest request;
        if (!TextUtils.isEmpty(url)) {
            request = buildGlideRequest(context, url);
        } else if(file!=null){
            request = buildGlideRequest(context, file);
        }else {
            request = buildGlideRequest(context, id);
        }
        if (displayType == CIRCLE) {
            if (transformation == null) {
                request = request.circleCrop();
            } else {
                request = request.transform(transformation);
            }
        } else {
            switch (displayType) {
                case FIT_CENTER:
                    request = request.fitCenter();
                    break;
                case CENTER_INSIDE:
                    request = request.centerInside();
                    break;
                case CENTER_CROP:
                    request = request.centerCrop();
                    break;
                case NO_TRANSFORMATION:
                    request = request.dontTransform();
                    break;
            }
            if (transformation != null) {
                request = request.transform(transformation);
            }
        }
        return request;
    }

    private static GlideRequest buildGlideRequest(final Object context, final String url) {
        if (context instanceof android.support.v4.app.Fragment) {
            final android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) context;
            return GlideApp.with(fragment).load(url);
        } else if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return GlideApp.with(activity).load(url);
        } else {
            return GlideApp.with((Context) context).load(url);
        }
    }


    private static GlideRequest buildGlideRequest(final Object context, final File file) {
        if (context instanceof android.support.v4.app.Fragment) {
            final android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) context;
            return GlideApp.with(fragment).load(file);
        } else if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return GlideApp.with(activity).load(file);
        } else {
            return GlideApp.with((Context) context).load(file);
        }
    }


    private static GlideRequest buildGlideRequest(final Object context, final int id) {
        if (context instanceof android.support.v4.app.Fragment) {
            final android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) context;
            return GlideApp.with(fragment).load(id);
        } else if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return GlideApp.with(activity).load(id);
        } else {
            return GlideApp.with((Context) context).load(id);
        }
    }
}
