package com.fhjs.casic.markingsystem.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import com.fhjs.casic.markingsystem.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ActivityUtil {

    private static Stack<Activity> activityList;
    private static ActivityUtil instance;

    // 单例模式中获取唯一的ExitApplication实例
    public static synchronized ActivityUtil getInstance() {
        if (null == instance) {
            instance = new ActivityUtil();
        }
        return instance;
    }

    public int getActivityCount() {
        return activityList.size();
    }

    // 添加Activity到容器中
    public void addActivity(Activity activity) {
        if (activityList == null)
            activityList = new Stack<Activity>();
        activityList.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的） 先进后出
     */
    public Activity currentActivity() {
        Activity activity = activityList.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityList.lastElement();
        finishActivity(activity);
    }

    // 移除Activity并finish
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityList.remove(activity);
            activity.finish();
        }
    }

    public void clearActivity() {
        if (activityList != null) {
            for (Activity ac : activityList) {
                if (ac != null)
                    ac.finish();
            }
            activityList.clear();
        }


    }
    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityList.size(); i < size; i++) {
            if (null != activityList.get(i)) {
                activityList.get(i).finish();
            }
        }
        activityList.clear();

    }
    //退出程序
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}