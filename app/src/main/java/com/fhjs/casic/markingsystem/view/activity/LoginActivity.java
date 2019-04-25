package com.fhjs.casic.markingsystem.view.activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private EditText nameET, pwdET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        UserBean userBean = BmobUser.getCurrentUser(UserBean.class);
        if (userBean != null) {
            setUser(BmobUser.getCurrentUser(UserBean.class));
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    private void initView() {
        nameET = $(R.id.login_name_edit);
        pwdET = $(R.id.login_pwd_edit);
        $(R.id.login_but).setOnClickListener(this);
        $(R.id.login_forget_password).setOnClickListener(this);
        $(R.id.login_register).setOnClickListener(this);

    }

    private void login(){
        String name = nameET.getText().toString();
        String pwd = pwdET.getText().toString();
        if (name.equals("") || pwd.equals("")) {
            showToast("账号和密码不能为空");
            return;
        }
        BmobUser.loginByAccount(name, pwd, new LogInListener<UserBean>() {
            @Override
            public void done(UserBean userBean, BmobException e) {
                if (e == null) {
                    setUser(BmobUser.getCurrentUser(UserBean.class));
                    ActivityUtil.getInstance().finishActivity(LoginActivity.this);
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    showToast("登录成功");
                } else {
                    switch (e.getErrorCode()) {
                        case 101:
                            showToast("用户名或密码不正确");
                            break;
                        case 202:
                            showToast("用户名已经存在");
                            break;
                    }
                }
            }
        });
    }
    private void setUserinfo() {
        UserBean userBean = new UserBean();
        userBean.setUsername("xsc");
        userBean.setDepartment(1);
        userBean.setAdmin(true);
        userBean.setPost(1);
        userBean.setUserName_CHS("徐诗超");
        userBean.setPassword("123456");
        userBean.signUp(new SaveListener<UserBean>() {

            @Override
            public void done(UserBean userBean, BmobException e) {
                if (e == null) {

                } else {
                    Log.e(TAG, "done: " + e);
                }
            }
        });
    }


    @Override
    protected void init() {
//        if (!hasPermission(Manifest.permission.READ_PHONE_STATE)) {
//            requestPermission(ConstantUtil.PERMISSIONS_REQUEST_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE);
//        }
    }

    @Override
    public void doRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case ConstantUtil.PERMISSIONS_REQUEST_READ_PHONE_STATE:// 读取手机信息权限
//                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // 权限请求成功\
//                    showToast("权限请求成功");
//                } else {
//                    // 权限请求失败
//                    showToast("权限请求失败");
//                }
//                break;
//        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_but:
                login();
                break;
            case R.id.login_forget_password:
                break;
            case R.id.login_register:
                startActivity(new Intent(this,RegisterActivity.class));
                break;
        }
    }
}
