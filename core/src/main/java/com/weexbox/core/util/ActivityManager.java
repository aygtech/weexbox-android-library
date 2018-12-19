package com.weexbox.core.util;

import android.app.Activity;

import java.util.Stack;

public final class ActivityManager {

    /**
     * Activity记录栈
     */
    private static Stack<Activity> activityStack = new Stack<Activity>();
    /**
     * AppManager单例
     */
    private static ActivityManager singleton = new ActivityManager();

    /**
     * 单例
     */
    private ActivityManager() {
    }

    /**
     * 获取AppManager单一实例
     */
    public static ActivityManager getInstance() {
        return singleton;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        Activity activity = activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定类名的Activity
     */
    public Activity getFirstActivity() {
        if (activityStack.isEmpty()) {
            return null;
        }
        return activityStack.firstElement();
    }

    /**
     * 结束指定类名的Activity
     */
    public Activity getActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return null;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                return activity;
            }
        }
        return null;
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 移除指定的Activity
     */
    public void removeActivity(Activity activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        if (activity != null) {
            activityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        if (activityStack.isEmpty()) {
            return;
        }
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity除了
     */
    public void finishAllActivityExcept(Activity activity) {
        if (activityStack.isEmpty()) {
            return;
        }
        for (Activity temp : activityStack) {
            if (null != temp && temp != activity) {
                temp.finish();
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack.isEmpty()) {
            return;
        }
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    public Stack<Activity> getAllActivities() {
        return activityStack;
    }

    /**
     * 退出应用程序
     */
    public void exitApplication() {
//        try {
//            ReleaseResourcesUtil.releaseResources();
//            finishAllActivity();
//        } catch (Exception e) {
//            Log.e("ActivityManager", "退出应用失败");
//        } finally {
//            System.exit(0);
//        }
    }
}
