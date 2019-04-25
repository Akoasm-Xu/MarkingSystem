package com.fhjs.casic.markingsystem.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.view.fragment.MainListFragment;
import com.fhjs.casic.markingsystem.view.fragment.SeeFragment;
import com.fhjs.casic.markingsystem.view.fragment.SettingFragment;
import com.fhjs.casic.markingsystem.view.fragment.UpdateFragment;
import com.fhjs.casic.markingsystem.view.fragment.UserAddFragment;


public class MainActivity extends BaseActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private FrameLayout main_layout;
    private FragmentManager fm = null;
    private FragmentTransaction transaction = null;
    //    private SettingFragment settingFM;
//    private MainListFragment mainListFM;
//    private SeeFragment seeFM;
//    private UpdateFragment updateFragment;
//    private UserAddFragment userAddFragment;
    private Fragment currentFragment = null;
    private static final String TAG = "MainActivity";
    public static final String FRAGMENT_TAG_LEFT = "mainListFM";
    public static final String FRAGMENT_TAG_CENTER = "seeFM";
    public static final String FRAGMENT_TAG_RIGHT = "settingFM";
    public static final String FRAGMENT_TAG_UPDATE = "updateFragment";
    public static final String FRAGMENT_TAG_ADD = "userAddFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
    }


    private void initView() {
        main_layout = $(R.id.main_layout);

        ((RadioGroup) $(R.id.main_navigation)).setOnCheckedChangeListener(this);
        fm = getSupportFragmentManager();

    }


    public UserBean getUsers() {
        return getUser();
    }


    public void showFragment(String fragmentTag) {
        Fragment fragment = fm.findFragmentByTag(fragmentTag);
        transaction = fm.beginTransaction();

        if (fragment == null) {//add
            if (fragmentTag.equals(FRAGMENT_TAG_LEFT)) {
                fragment = new MainListFragment();
            } else if (fragmentTag.equals(FRAGMENT_TAG_CENTER)) {
                fragment = new SeeFragment();
            } else if (fragmentTag.equals(FRAGMENT_TAG_RIGHT)) {
                fragment = new SettingFragment();
            } else if (fragmentTag.equals(FRAGMENT_TAG_UPDATE)) {
                fragment = new UpdateFragment();
            } else if (fragmentTag.equals(FRAGMENT_TAG_ADD)) {
                fragment = new UserAddFragment();
            }
            if (currentFragment != null) {
                transaction.hide(currentFragment);
            }
            transaction.add(R.id.main_layout, fragment, fragmentTag).commit();
            currentFragment = fragment;

        } else {//show
            transaction.hide(currentFragment);
            currentFragment = fragment;
            transaction.show(currentFragment).commit();
        }
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        showFragment(FRAGMENT_TAG_LEFT);
        ((RadioButton) findViewById(R.id.main_left_rb)).setChecked(true);
    }

    @Override
    protected void init() {

    }


    // 处理网络状态结果
    @Override
    public void onNetChange(boolean netWorkState) {
        super.onNetChange(netWorkState);
    }

    @Override
    public void onClick(View v) {


    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.main_left_rb:
                showFragment(FRAGMENT_TAG_LEFT);
                break;
            case R.id.main_center_rb:
                showFragment(FRAGMENT_TAG_CENTER);

                break;
            case R.id.main_right_rb:
                showFragment(FRAGMENT_TAG_RIGHT);
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK && event.getAction() == event.ACTION_DOWN) {
            if (currentFragment instanceof UpdateFragment) {
                showFragment(FRAGMENT_TAG_RIGHT);
                return true;
            }
            if (currentFragment instanceof UserAddFragment) {
                showFragment(FRAGMENT_TAG_RIGHT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
