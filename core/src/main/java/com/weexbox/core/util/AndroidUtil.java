package com.weexbox.core.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings.Secure;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.weexbox.core.controller.WBBaseActivity;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * 该类是开发过程中经常会用到获取一些和android属性相关的类
 *
 * @author freeson
 */
public final class AndroidUtil {

    private AndroidUtil() {

    }

    /**
     * 获取标题栏的高度
     *
     * @param activity
     * @return
     */
    public static int getTitleHeight(WBBaseActivity activity) {
        int actionBarHeight = activity.getActionbar().getActionbarHeight();
        return actionBarHeight;
    }

    /**
     * 获取状态栏高度
     *
     * @param activity
     * @return
     */
    public static int getStateBarHeight(Activity activity) {
        Rect rect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        return rect.top;
    }

    /**
     * 判断导航栏是否显示
     *
     * @param context
     * @return
     */
    public static boolean whetherNavigationBarIsShow(Context context) {
        final Resources resources = context.getResources();
        int rid = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        if (rid > 0) {
            return resources.getBoolean(rid);
        } else {
            return false;
        }

    }

    /***
     * 获取导航栏的高度
     *
     * @param context
     * @return
     */
    public static int getaNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    /**
     * 模拟home键
     *
     * @param context
     */
    public static void goToDesktop(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    /**
     * 判断应用是否是前台运行 加上权限 < uses-permission
     * android:name=“android.permission.GET_TASKS” />
     */

    public static boolean isRunningForeground(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        String currentPackageName = cn.getPackageName();
        return !TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(context.getPackageName());

    }

    /**
     * 获得屏幕高度
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕宽度
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 给应用评分
     *
     * @param context <br>
     *                id为包名
     */
    public static void evaluateApplication(Context context) {
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 打开浏览器，网址必须是合法的，不能打开本地网页
     *
     * @param context
     * @param url     合法的网址
     */
    public static void openBrowser(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        context.startActivity(intent);
    }

    /**
     * 打电话,直接拨打
     *
     * @param context
     * @param phoneNumber 电话号码
     */
    public static void makeCall(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            context.startActivity(intent);
        } catch (Exception e) {
        }
    }

    /**
     * 跳转到拨打电话页面
     *
     * @param context
     * @param phoneNumber 电话号码
     */
    public static void goToCallActivity(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
        context.startActivity(intent);
    }

    /**
     * 发送短信
     *
     * @param context
     * @param phoneNumber 电话号码
     * @param message     短信内容
     */
    public static void sendSmsMessage(Context context, String phoneNumber, String message) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        context.startActivity(intent);
    }

    /**
     * 调用邮件客户端发送邮件
     *
     * @param context
     * @param emailAddress 邮件地址
     * @param emailTitle   邮件标题
     * @param emailBody    邮件正文
     */
    public static void sendEmail(Context context, String emailAddress, String emailTitle, String emailBody) {
        Uri uri = Uri.parse("mailto:" + emailAddress);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "EmailTitle:" + emailTitle);
        intent.putExtra(Intent.EXTRA_TEXT, "EmailBody:" + emailBody);
        context.startActivity(intent);
    }

    /**
     * 播放使用列表来选择程序MP3
     *
     * @param context
     * @param mp3File
     */
    public static void choosePlayMp3List(Context context, File mp3File) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(mp3File), "audio");
        context.startActivity(intent);
    }

    /**
     * 获取版本号
     *
     * @param context
     * @return
     */
    public static int getCurrentVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            return -1;
        }
    }

    /**
     * 获取版本名称
     *
     * @param context
     * @return
     */
    public static String getCurrentVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            return "";
        }
    }

    public static void openSoftKeyboard(EditText et) {
        InputMethodManager inputManager = (InputMethodManager) et.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(et, 0);
    }

    public static void closeSoftKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 获取 AndroidManifest.xml 中的 Meta Data.
     *
     * @param context
     * @param metaName
     * @return
     */
    public static String getMetaData(Context context, String metaName) {
        ApplicationInfo appInfo;
        String value = "";
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(),
                    PackageManager.GET_META_DATA);
            Object tmp = appInfo.metaData.get(metaName);
            value = tmp.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * 安装 APK.
     *
     * @param context context.
     * @param file    apk file Path.
     */
    public static void installApk(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 获取当前的网络类型
     *
     * @return
     */
    public static int getNetWorkType(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo == null ? ConnectivityManager.TYPE_MOBILE : netInfo.getType();
    }


    /**
     * 判断是否为wifi状态
     *
     * @param context
     * @return
     */
    public static boolean isWiftConnecting(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return null != netInfo
                && netInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 是否有网
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static boolean intentActionHasHandle(Context context, Intent intent) {
        final PackageManager packageManager = context.getPackageManager();
        final ComponentName componentName = intent.resolveActivity(packageManager);
        return componentName != null;
    }

    public static void insertImageToSystemGallery(final Context context, final File file) {
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Uri uri = Uri.fromFile(file);
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }

    public static String getCurrentProcessName(final Context context) {
        int pid = android.os.Process.myPid();

        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager
                .getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return appProcess.processName;
            }
        }
        return null;
    }

    public static void copyTextToPasteboard(final Context context, String text) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("simple text", text);
        clipboardManager.setPrimaryClip(data);
    }
}
