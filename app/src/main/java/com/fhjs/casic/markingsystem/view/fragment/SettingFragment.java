package com.fhjs.casic.markingsystem.view.fragment;


import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.base.BaseFragment;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.view.activity.LoginActivity;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnConfirmListener;

import cn.bmob.v3.BmobQuery;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private TextView userNameTV, registerTV;
    private boolean isAdmin = false;

    @Override
    public void initView(View view) {
        userNameTV = view.findViewById(R.id.setting_user_name);
        registerTV = view.findViewById(R.id.setting_user_add);
        registerTV.setOnClickListener(this);
        view.findViewById(R.id.setting_user_about).setOnClickListener(this);
        view.findViewById(R.id.setting_user_out).setOnClickListener(this);
        view.findViewById(R.id.setting_user_update).setOnClickListener(this);
        view.findViewById(R.id.setting_user_cache).setOnClickListener(this);
        view.findViewById(R.id.setting_user_version).setOnClickListener(this);

        UserBean userBean = BaseActivity.getUser();
        if (userBean != null) {
            isAdmin = userBean.getAdmin();
            registerTV.setVisibility(View.GONE);
            String useName = userBean.getUserName_CHS();
            if (isAdmin) {
                useName = "管理员：" + useName;
                registerTV.setVisibility(View.VISIBLE);
            }
            userNameTV.setText(useName);
        }

    }


    @Override
    public int FragmentLayoutId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_user_update:

                ((MainActivity) getActivity()).showFragment(MainActivity.FRAGMENT_TAG_UPDATE);

                break;
            case R.id.setting_user_add:
                if (!isAdmin) {
                    showToast("非管理员用户无法新建账号！");
                    return;
                }

                ((MainActivity) getActivity()).showFragment(MainActivity.FRAGMENT_TAG_ADD);
                break;
            case R.id.setting_user_about:
                showToast("暂无数据");
                break;
            case R.id.setting_user_out:
                //弹窗！！
               new XPopup.Builder(getContext()).asConfirm("提示", "确认退出？",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                UserBean.logOut();
                                ActivityUtil.getInstance().clearActivity();
                                getActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        }).show();

                break;
            case R.id.setting_user_cache:

               new XPopup.Builder(getContext()).asConfirm("提示", "是否清除缓存？",
                        new OnConfirmListener() {
                            @Override
                            public void onConfirm() {
                                BmobQuery.clearAllCachedResults();
                                showToast("已清除缓存");
                                BmobUtil.getInstance().initData();//再次初始化
                            }
                        }).show();

                break;
            case R.id.setting_user_version:
//                BmobUpdateAgent.initAppVersion();
//                BmobUpdateAgent.forceUpdate(getContext());
                break;
        }
    }
}
