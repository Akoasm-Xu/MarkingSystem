package com.fhjs.casic.markingsystem.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.AutoBreakViewGroup;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.CalculateProportionUtil;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.fhjs.casic.markingsystem.util.DateTimeUtil;
import com.fhjs.casic.markingsystem.util.Utils;
import com.fhjs.casic.markingsystem.util.WeightLayout;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * 录入页面
 */
public class InputActivity extends BaseActivity implements View.OnClickListener {
    private boolean isInterview = true;
    private EditText name, sex, age, phone, date, postET, departmentET, k1ET, k2ET, k3ET,
            PA_nameTv, PA_sexTv, PA_ageTv;
    private LinearLayout interviewLy, pxLy;
    private String[] departStr, postStr, typeArray;
    private AutoBreakViewGroup userVG;
    private GridLayout weightVG;
    private int deleteCount = 0;
    private int cDP5 = 0, cDP10 = 0, cDP20 = 0;
    private Map<String, UserBean> selectMap = null;
    private List<UserBean> userList = null;
    private String[] userArray = null, weightArray = null;
    private TextView PA_dateTv, typeTv, PA_postTv, PA_departTv;
    private Integer[] selectArray = null;
    private static final int ROLE_TYPE_INTERVIEW = 1;//面试者（对象）
    private static final int ROLE_TYPE_ASSESS = 2;//考核者（对象）
    private int PA_type = 0;//考核类型
    private String userIDs = null;//评价人id
    private WeightLayout weightLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        initView();
        ininDatas();

    }

    private void initView() {
        name = $(R.id.input_name_edit);
        sex = $(R.id.input_sex_edit);
        age = $(R.id.input_age_edit);
        phone = $(R.id.input_phone_edit);
        date = $(R.id.input_date_edit);
        postET = $(R.id.input_post_text);
        departmentET = $(R.id.input_department_text);
        userVG = $(R.id.userViewGroup);
        weightVG = $(R.id.weightViewGroup);
        interviewLy = $(R.id.input_interview_layout);
        weightLayout=$(R.id.include_weight);
        pxLy = $(R.id.input_px_layout);
        k1ET = $(R.id.input_k1_edit);
        k2ET = $(R.id.input_k2_edit);
        k3ET = $(R.id.input_k3_edit);
        typeTv = $(R.id.input_type_text);
        PA_nameTv = $(R.id.include_name_edit);
        PA_sexTv = $(R.id.include_sex_edit);
        PA_ageTv = $(R.id.include_age_edit);
        PA_postTv = $(R.id.include_post_text);
        PA_departTv = $(R.id.include_department_text);
        PA_dateTv = $(R.id.include_date_text);
    }

    private void ininDatas() {
        cDP5 = Utils.dip2px(this, 5);
        cDP10 = Utils.dip2px(this, 10);
        cDP20 = Utils.dip2px(this, 20);
        selectArray = new Integer[3];
        k1ET.setOnClickListener(this);
        k2ET.setOnClickListener(this);
        k3ET.setOnClickListener(this);
        initdata();
        weightArray = new String[]{};
        userVG.setSpacing(cDP20, cDP20);
        $(R.id.input_title_image).setOnClickListener(this);
        $(R.id.input_title_submit).setOnClickListener(this);
        $(R.id.weight_confirm_text).setOnClickListener(this);
        sex.setOnClickListener(this);
        postET.setOnClickListener(this);
        departmentET.setOnClickListener(this);
        PA_postTv.setOnClickListener(this);
        PA_departTv.setOnClickListener(this);
        PA_sexTv.setOnClickListener(this);
        date.setText(DateTimeUtil.gainCurrentDateString());
        PA_dateTv.setText(DateTimeUtil.gainCurrentDateString());
        ((RadioButton) $(R.id.radio_left_but)).setChecked(true);
        userList = BmobUtil.getInstance().getUserList();
        getSelectData();
        weightLayout.setListener(new WeightLayout.OnGetWeightText() {
            @Override
            public void getWeight(String weight) {
                userIDs=weight;
            }
        });
        ((RadioGroup) $(R.id.input_radioGroup)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_left_but) {
                    isInterview = true;
                    interviewLy.setVisibility(View.VISIBLE);
                    pxLy.setVisibility(View.GONE);
                } else {
                    isInterview = false;
                    interviewLy.setVisibility(View.GONE);
                    pxLy.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initdata() {

    }

    @Override
    protected void init() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.input_title_image:
                ActivityUtil.getInstance().finishActivity(this);
                break;
            case R.id.input_title_submit:
                saveInput();
                break;
            case R.id.input_sex_edit:
                selectText(sex, new String[]{"男", "女"});
                break;
            case R.id.include_sex_edit:
                selectText(PA_sexTv, new String[]{"男", "女"});
                break;
            case R.id.input_post_text:
                selectText(postET, postStr);
                break;
            case R.id.input_department_text:
                selectText(departmentET, departStr);
                break;
            case R.id.include_post_text:
                selectText(PA_postTv, postStr);
                break;
            case R.id.include_department_text:
                selectText(PA_departTv, departStr);
                break;
            case R.id.input_k1_edit:
                selectText(k1ET, BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_ASSESS));
                break;
            case R.id.input_k2_edit:
                selectText(k2ET, BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_ASSESS));
                break;
            case R.id.input_k3_edit:
                selectText(k3ET, BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_ASSESS));

                break;

        }
    }

    /**
     * 保存输入的内容 提交到数据库
     */
    private void saveInput() {
        if (isInterview) {
            if (name.getText().toString().equals("")) {
                showToast("请录入姓名");
                return;
            }
            ObjectBean isb = new ObjectBean();
            isb.setName(name.getText().toString());
            isb.setAge(Integer.parseInt(age.getText().toString()));
            isb.setSex(sex.getText().toString());
            isb.setPhone(phone.getText().toString());
            isb.setDate(date.getText().toString());
            isb.setPost(Integer.parseInt(postET.getTag().toString()));
            isb.setDepartment(Integer.parseInt(departmentET.getTag().toString()));
            isb.setRemarks(null);//备注
            isb.setRole(ROLE_TYPE_INTERVIEW);
            isb.setMarkType(false);
            Log.e(TAG, "inputInfo: " + isb.toString());
            isb.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        name.setText("");
                        age.setText("");
                        sex.setText("");
                        phone.setText("");
                        postET.setText("");
                        departmentET.setText("");
                        showToast("发布成功！");
                    } else {
                        showToast("发布失败：" + e.getErrorCode());
                    }
                }
            });
        } else {
            if (TextUtils.isEmpty(PA_nameTv.getText()) || TextUtils.isEmpty(PA_ageTv.getText()) || TextUtils.isEmpty(PA_sexTv.getText())) {
                showToast("请输入完整信息！");
                return;
            }

            if (PA_type == 0 || userIDs == null || userIDs.equals("")) {
                Log.e(TAG, "saveInput: type:" + PA_type + "=====IDs:" + userIDs);
                return;
            }
            ObjectBean isb = new ObjectBean();
            isb.setName(PA_nameTv.getText().toString());
            isb.setAge(Integer.parseInt(PA_ageTv.getText().toString()));
            isb.setSex(PA_sexTv.getText().toString());
            isb.setPhone(null);
            isb.setDate(PA_dateTv.getText().toString());
            isb.setPost(Integer.parseInt(PA_postTv.getTag().toString()));
            isb.setDepartment(Integer.parseInt(PA_departTv.getTag().toString()));
            isb.setRemarks(null);//备注
            isb.setRole(ROLE_TYPE_ASSESS);
            isb.setMarkType(false);
            isb.setPa_type(PA_type);
            isb.setObject_userIDs(userIDs);
            Log.e(TAG, "inputInfo: " + isb.toString());
            isb.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        PA_nameTv.setText("");
                        PA_ageTv.setText("");
                        PA_sexTv.setText("");
                        PA_postTv.setText("");
                        PA_departTv.setText("");
                        showToast("发布成功！");
                    } else {
                        showToast("发布失败：" + e.getErrorCode());
                    }
                }
            });
        }
    }


    //底部弹窗 选择框
    private void selectText(final TextView editText, String[] data) {
        Utils.hideInput(this);
       new  XPopup.Builder(this).asBottomList("请选择一项", data,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        editText.setTag((position + 1));
                        editText.setText(text);
                        setType(editText);
                        Log.e(TAG, "onSelect: " + position + "  ==>" + text);
                    }
                }).show();

    }





    /**
     * 根据已选的考核内容设置考核类型
     *
     * @param editText
     */
    private void setType(TextView editText) {
        if (TextUtils.isEmpty(k1ET.getText()) || TextUtils.isEmpty(k2ET.getText()) || TextUtils.isEmpty(k3ET.getText())) {
            return;
        }
        int id = editText.getId();
        if (id == k1ET.getId() || id == k2ET.getId() || id == k3ET.getId()) {
            String k1 = k1ET.getTag().toString();
            String k2 = k2ET.getTag().toString();
            String k3 = k3ET.getTag().toString();
            typeArray = BmobUtil.getInstance().getTypeToArray("assess_type");
            for (int i = 0; i < typeArray.length; i++) {
                if (typeArray[i].contains(k1) && typeArray[i].contains(k2) && typeArray[i].contains(k3)) {
                    typeTv.setText("考核类型为：" + Utils.getIntToNumber(i + 1) + "类");
                    PA_type = (i + 1);
                }
            }
        }
    }



    //查询数据
    private void getSelectData() {
        departStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_DEPART);
        postStr = BmobUtil.getInstance().getTypeToArray(ConstantUtil.VALUE_TYPE_POST);
    }



}
