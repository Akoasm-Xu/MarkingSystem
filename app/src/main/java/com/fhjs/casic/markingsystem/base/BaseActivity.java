package com.fhjs.casic.markingsystem.base;

import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.fhjs.casic.markingsystem.util.NetBroadcastReceiver;

import cn.bmob.v3.Bmob;

public abstract class BaseActivity extends AppCompatActivity implements NetBroadcastReceiver.NetChangeListener {
    public static NetBroadcastReceiver.NetChangeListener netEvent;// 网络状态改变监听事件

    private static Toast toast;
    public static UserBean user;
    protected boolean isImmersive = false;
    protected final String TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        // 沉浸效果
        if (isImmersive) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // 透明状态栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                // 透明导航栏
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        Bmob.initialize(this, ConstantUtil.bmobId);
        // 添加到Activity工具类
        ActivityUtil.getInstance().addActivity(this);
        BmobUtil.getInstance().initData();

        // 初始化netEvent
        netEvent = this;

        // 执行初始化方法
        init();
    }

    // 抽象 - 初始化方法，可以对数据进行初始化
    protected abstract void init();

    protected <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Resources resources = this.getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.fontScale = 1;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    @Override
    protected void onDestroy() {
        // Activity销毁时，提示系统回收
        // System.gc();
        netEvent = null;
        // 移除Activity
        ActivityUtil.getInstance().finishActivity(this);
        super.onDestroy();
    }


    // 设置返回按钮的监听事件
    private long exitTime = 0;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 点击手机上的返回键，返回上一层
        if (keyCode == KeyEvent.KEYCODE_BACK && ActivityUtil.getInstance().getActivityCount() == 1) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                showToast("再按一次退出程序");
                exitTime = System.currentTimeMillis();
            } else {
                ActivityUtil.getInstance().AppExit(this);

            }
            return true;
        } else {
            if (keyCode==event.KEYCODE_DEL){
                return true;
            }
            ActivityUtil.getInstance().finishActivity(this);
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 权限检查方法，false代表没有该权限，ture代表有该权限
     */
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static UserBean getUser() {
        return user;
    }

    public static void setUser(UserBean user) {
        BaseActivity.user = user;
    }

    /**
     * 权限请求方法
     */
    public void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    /**
     * 处理请求权限结果事件
     *
     * @param requestCode  请求码
     * @param permissions  权限组
     * @param grantResults 结果集
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doRequestPermissionsResult(requestCode, grantResults);
    }

    /**
     * 处理请求权限结果事件
     *
     * @param requestCode  请求码
     * @param grantResults 结果集
     */
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
    }

    /**
     * 网络状态改变时间监听
     *
     * @param netWorkState true有网络，false无网络
     */
    @Override
    public void onNetChange(boolean netWorkState) {
    }

    /**
     * 显示长toast
     *
     * @param msg
     */
    public void showToastLong(String msg) {
        if (null == toast) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);

        }
        toast.setText(msg);
        toast.show();

    }

    /**
     * 显示短toast
     *
     * @param msg
     */

    public void showToast(String msg) {
        if (null == toast) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);

        }
        toast.setText(msg);
        toast.show();

    }
}
