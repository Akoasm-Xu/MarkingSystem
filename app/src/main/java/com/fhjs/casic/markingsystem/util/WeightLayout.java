package com.fhjs.casic.markingsystem.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.view.activity.InputActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeightLayout extends LinearLayout {
    private Map<String, UserBean> selectMap = null;
    private List<UserBean> userList = null;
    private String[] userArray = null, weightArray = null;
    private Context context;
    private int cDP5, cDP20;
    private GridLayout weightVG;
    private AutoBreakViewGroup userVG;
    private int deleteCount = 0;
    private String userIDs = null;//评价人id
    private OnGetWeightText listener;
    private static final String TAG = "WeightLayout";

    public WeightLayout(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public WeightLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.include_weight_layout, this, true);
        userVG = view.findViewById(R.id.add_user_Group);
        weightVG = view.findViewById(R.id.add_weight_layout);
        findViewById(R.id.include_weight_determine).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {//回调权限和评测人员数据
                getWeightEditText();
            }
        });
        cDP20 = Utils.dip2px(context, 20);
        cDP5 = Utils.dip2px(context, 5);
        userVG.setSpacing(cDP20, cDP20);
        userList = BmobUtil.getInstance().getUserList();
        Log.e(TAG, "init: " + userList.size());
        initUserArray();
        initUserAdd();
    }


    //实现评价人 的添加与删除功能
    private void addUserView(String str, String id, View v) {
        TextView view = new TextView(context);
        view.setText(str);
        view.setBackgroundResource(R.drawable.text_frame);
        view.setTag(id);
        view.setTextSize(16f);
        view.setClickable(true);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(context, 35));
        view.setPadding(cDP5, cDP5, cDP5, cDP5);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeSelectMap(v.getTag().toString());
                userVG.removeView(v);
                setWeightView();
                initUserAdd();//判断是否需要显示添加用户的功能
            }
        });
        userVG.addView(view, params);
        setWeightView();
        int childCount = userVG.getChildCount();
        if (childCount < ConstantUtil.INPUT_EVALUATOR_COUNT) {//此处限制 添加最大个数为4人
            userVG.addView(v);
            deleteCount++;
        }


    }

    //设置权重
    @SuppressLint("NewApi")
    private void setWeightView() {
        weightVG.removeAllViews();
        int count = selectMap.size();
        if (count == 0) {
            return;
        }
        String weightStr = CalculateProportionUtil.proportionDouble(1, count);
        int i = 0;
        for (UserBean bean : selectMap.values()) { //key-id  value-user
            View view = LayoutInflater.from(context).inflate(R.layout.input_weight_layout, null);
            TextView nameTv = view.findViewById(R.id.input_weight_layout_name);
            EditText weightTv = view.findViewById(R.id.input_weight_layout_weight);
            weightTv.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            nameTv.setText(bean.getUserName_CHS());
            weightTv.setId(i);
            weightTv.setText(weightStr);
            weightTv.setTag(bean.getObjectId());
//            params.rowSpec = GridLayout.spec(0,1,1f);
            params.columnSpec = GridLayout.spec((i + 2) % 2, 1f);//开始位置  权重
            weightVG.addView(view, params);
            i++;


        }

    }

    /**
     * 获取输入或者自动匹配的权重值（很影响性能 此处待优化）
     */
    private void getWeightEditText() {
        userIDs = "";
        for (int i = 0; i < weightVG.getChildCount(); i++) {
            if (weightVG.getChildAt(i) instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) weightVG.getChildAt(i);
                for (int j = 0; j < layout.getChildCount(); j++) {
                    if (layout.getChildAt(j) instanceof EditText) {
                        EditText editText = (EditText) layout.getChildAt(j);
                        userIDs += editText.getTag().toString() + ":" + editText.getText().toString() + ",";
                    }
                }
            }
        }
        if (userIDs.length() > 0) {//截取最后一个逗号
            userIDs = userIDs.substring(0, userIDs.length() - 1);
//            Log.e(TAG, "getWeightEditText: af" +userIDs);
        }
        Log.e(TAG, "getWeightEditText: isNULL" + (listener == null));
        if (listener != null) {
            listener.getWeight(userIDs);
            Log.e(TAG, "getWeightEditText: 传输数据：" + userIDs);
        }
    }


    //添加评估人的按钮
    private void initUserAdd() {
        ImageView imageView = new ImageView(context);
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.icon_input_add));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, Utils.dip2px(context, 35));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPopup(userArray, v);
            }
        });
        if (deleteCount == 0) {//若没有添加用户 则显示该添加按键
            userVG.addView(imageView, params);
            deleteCount++;
        }
    }

    private void initUserArray() {
        selectMap = new HashMap<>();
        if (userList == null) {
            return;
        }
        userArray = new String[userList.size()];
        for (int i = 0; i < userList.size(); i++) {
            userArray[i] = userList.get(i).getUserName_CHS();
        }
    }


    //添加评估人
    private void putSelectMap(String key, UserBean value) {
        if (selectMap == null) {
            return;
        }
        selectMap.put(key, value);
    }

    //移除评估人
    private void removeSelectMap(String key) {
        if (selectMap == null) {
            return;
        }
        selectMap.remove(key);
    }


    /**
     * 底部选择弹窗
     *
     * @param data
     * @param v
     */
    private void selectPopup(String[] data, View v) {
       new XPopup.Builder(context).asBottomList("请选择一项", data,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        String uid = userList.get(position).getObjectId();
                        if (selectMap.containsKey(uid)) {
                            Toast.makeText(context, "不可重复选择同一人!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        userVG.removeView(v);
                        deleteCount--;
                        putSelectMap(uid, userList.get(position));
                        addUserView(text, uid, v);//text：为选择的文本内容  position：为选中的下标 此处作为参数是为了管理选择的用户
//                        dismiss();
                    }
                }).show();

    }


    public void setListener(OnGetWeightText listener) {
        this.listener = listener;
    }

    public interface OnGetWeightText {
        void getWeight(String weight);
    }
}
