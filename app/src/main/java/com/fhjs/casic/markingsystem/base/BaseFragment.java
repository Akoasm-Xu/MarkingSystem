package com.fhjs.casic.markingsystem.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fhjs.casic.markingsystem.view.activity.MainActivity;

public abstract class BaseFragment extends Fragment {
    //这个activity就是MainActivity
    public Activity mActivity;

    // Fragment被创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();// 获取所在的activity对象
    }

    // 初始化Fragment布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(FragmentLayoutId(), container, false);
        initView(view);
        return view;
    }

    // activity创建结束
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    protected void showToast(String msg) {
        ((MainActivity) mActivity).showToast(msg);
    }

    /**
     * 初始化布局, 子类必须实现
     */
    public abstract void initView(View view);


    public abstract int FragmentLayoutId();

    /**
     * 初始化数据, 子类可以不实现
     */
    public void initData() {

    }

}
