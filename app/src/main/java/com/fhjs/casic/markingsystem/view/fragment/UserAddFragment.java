package com.fhjs.casic.markingsystem.view.fragment;


import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseFragment;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.fhjs.casic.markingsystem.util.Utils;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserAddFragment extends BaseFragment implements View.OnClickListener {
    private SettingFragment settingFragment;
    private EditText userET, nameET, pwdET, againPwdET;
    private TextView postTV, deparTV, groupTV, levelTV;
    private String[] postStr, departStr, groupStr;
    private String[] levelStr = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12",};
    private String[] powerStr = {"1", "2", "3"};

    @Override
    public void initView(View view) {
        ((TextView) view.findViewById(R.id.title_text)).setText("新建账号");
        userET = view.findViewById(R.id.register_user_edit);
        nameET = view.findViewById(R.id.register_name_edit);
        pwdET = view.findViewById(R.id.register_pwd_edit);
        againPwdET = view.findViewById(R.id.register_again_pwd_edit);
        postTV = view.findViewById(R.id.register_post_text);
        deparTV = view.findViewById(R.id.register_department_text);
        groupTV = view.findViewById(R.id.register_group_text);
        levelTV = view.findViewById(R.id.register_level_text);
        postTV.setOnClickListener(this);
        deparTV.setOnClickListener(this);
        groupTV.setOnClickListener(this);
        levelTV.setOnClickListener(this);
        view.findViewById(R.id.title_right).setOnClickListener(this);
        view.findViewById(R.id.title_image).setOnClickListener(this);
        getSelectData();
    }

    private void registerUser() {
        String user = userET.getText().toString();
        String name = nameET.getText().toString();
        String pwd = pwdET.getText().toString();
        String againPwd = againPwdET.getText().toString();
        String post = postTV.getText().toString();
        String department = deparTV.getText().toString();
        String group = groupTV.getText().toString();
        String level = levelTV.getText().toString();

        if (user.equals("") || name.equals("") || pwd.equals("") || againPwd.equals("")
                || post.equals("") || department.equals("") || group.equals("") || level.equals("")) {
            showToast("请输入完整信息");
            return;
        }
        if (!pwd.equals(againPwd)) {
            showToast("两次输入的密码不一致");
            return;
        }
        UserBean userBean = new UserBean();
        userBean.setUsername(user);
        userBean.setAdmin(false);
        userBean.setPassword(pwd);
        userBean.setUserName_CHS(name);
        userBean.setDepartment(Integer.parseInt(deparTV.getTag().toString()));
        userBean.setPost(Integer.parseInt(postTV.getTag().toString()));
        userBean.setGroup(Integer.parseInt(groupTV.getTag().toString()));
        userBean.setLevel(Integer.parseInt(levelTV.getTag().toString()));
        userBean.signUp(new SaveListener<UserBean>() {
            @Override
            public void done(UserBean userBean, BmobException e) {
                if (e == null) {
                    userET.setText("");
                    nameET.setText("");
                    pwdET.setText("");
                    againPwdET.setText("");
                    postTV.setText("");
                    deparTV.setText("");
                    groupTV.setText("");
                    levelTV.setText("");

                    showToast("添加成功");
                } else {
                    showToast("添加失败" + e.getMessage());
                }
            }
        });

    }


    @Override
    public int FragmentLayoutId() {
        return R.layout.fragment_user_add;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_right:
                registerUser();
                break;
            case R.id.title_image:

                ((MainActivity) getActivity()).showFragment(MainActivity.FRAGMENT_TAG_RIGHT);
                break;
            case R.id.register_department_text:
                selectText(deparTV, departStr);
                break;
            case R.id.register_post_text:
                selectText(postTV, postStr);
                break;
            case R.id.register_group_text:
                selectText(groupTV, groupStr);
                break;
            case R.id.register_level_text:
                selectText(levelTV, levelStr);
                break;
        }
    }

    private void selectText(TextView view, String[] data) {
        Utils.hideInput(getActivity());
       new XPopup.Builder(getActivity()).asBottomList("请选择一项", data,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        view.setTag(position);
                        view.setText(text);
                    }
                }).show();
    }

    //提前加载选择框的数据
    private void getSelectData() {

        departStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_DEPART);
        postStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_POST);
        groupStr=BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_GROUP);


    }
}
