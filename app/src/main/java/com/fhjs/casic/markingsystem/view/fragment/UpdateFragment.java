package com.fhjs.casic.markingsystem.view.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseFragment;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;

import org.w3c.dom.Text;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class UpdateFragment extends BaseFragment {
    private EditText oldET, newET, againNewET;
    private SettingFragment settingFragment;
    @Override
    public void initView(View view) {
        ((TextView) view.findViewById(R.id.title_text)).setText("修改账户密码");
        oldET = view.findViewById(R.id.update_old_pwd_edit);
        newET = view.findViewById(R.id.update_new_pwd_edit);
        againNewET = view.findViewById(R.id.update_again_new_pwd_edit);
        view.findViewById(R.id.title_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        view.findViewById(R.id.title_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (settingFragment == null) {
                    settingFragment = new SettingFragment();
                }
                ((MainActivity) getActivity()).showFragment(MainActivity.FRAGMENT_TAG_RIGHT);
            }
        });
    }

    private void updatePassword(){
        String oldPwd = oldET.getText().toString();
        String newPwd = newET.getText().toString();
        String againNewPwd = againNewET.getText().toString();
        if (oldPwd.equals("") || newPwd.equals("") || againNewPwd.equals("")) {
            showToast("请输入完整信息");
            return;
        }
        if (!newPwd.equals(againNewPwd)){
            showToast("两次输入的新密码不一致");
            return;
        }
        UserBean.updateCurrentUserPassword(oldPwd, againNewPwd, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    oldET.setText("");
                    newET.setText("");
                    againNewET.setText("");
                    showToast("修改成功");
                }    else {
                    showToast("修改失败："+e.getMessage());
                }
            }
        });
    }
    @Override
    public int FragmentLayoutId() {
        return R.layout.fragment_update;
    }
}
