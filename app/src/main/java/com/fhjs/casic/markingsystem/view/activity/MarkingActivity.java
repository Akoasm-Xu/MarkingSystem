package com.fhjs.casic.markingsystem.view.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.ScoringBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.fhjs.casic.markingsystem.util.DateTimeUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 打分页
 */
public class MarkingActivity extends BaseActivity implements View.OnClickListener, RatingBar.OnRatingBarChangeListener {
    private TextView titliText, titleRelease, interviewNameTV, userNameTV;
    private TextView scorTV1, scorTV2, scorTV3, scorTV4, scorTV5;
    private float RBValue1, RBValue2, RBValue3, RBValue4, RBValue5;
    private ObjectBean interview;
    private EditText conclusionET;
    private final MyHandler mHandler = new MyHandler(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marking);
        initView();
        initDatas();
    }

    private void initView() {
        titliText = $(R.id.title_text);
        titleRelease = $(R.id.title_right);
        interviewNameTV = $(R.id.cardview_name);
        conclusionET = $(R.id.marking_conclusion_edit);
        userNameTV = $(R.id.marking_user_name);
        titleRelease.setOnClickListener(this);
        $(R.id.title_image).setOnClickListener(this);
        ((RatingBar) $(R.id.marking_rb_1)).setOnRatingBarChangeListener(this);
        ((RatingBar) $(R.id.marking_rb_2)).setOnRatingBarChangeListener(this);
        ((RatingBar) $(R.id.marking_rb_3)).setOnRatingBarChangeListener(this);
        ((RatingBar) $(R.id.marking_rb_4)).setOnRatingBarChangeListener(this);
        ((RatingBar) $(R.id.marking_rb_5)).setOnRatingBarChangeListener(this);
        scorTV1 = $(R.id.marking_text_1);
        scorTV2 = $(R.id.marking_text_2);
        scorTV3 = $(R.id.marking_text_3);
        scorTV4 = $(R.id.marking_text_4);
        scorTV5 = $(R.id.marking_text_5);
    }

    private void initDatas() {

        interview = (ObjectBean) getIntent().getSerializableExtra("bean");
        initInterview();
        titliText.setText("打分");
        titleRelease.setText("发布");
        userNameTV.setText("面试人：" + getUser().getUserName_CHS());
        interviewNameTV.setText(interview.getName());
    }

    @Override
    protected void init() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image:
                ActivityUtil.getInstance().finishActivity(this);
                break;
            case R.id.title_right://发布
                markingRating();
                break;
        }
    }

    @Override
    public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
        switch (ratingBar.getId()) {
            case R.id.marking_rb_1:
                RBValue1 = rating;
                scorTV1.setText(RBValue1 + "");
                break;
            case R.id.marking_rb_2:
                RBValue2 = rating;
                scorTV2.setText(RBValue2 + "");
                break;
            case R.id.marking_rb_3:
                RBValue3 = rating;
                scorTV3.setText(RBValue3 + "");
                break;
            case R.id.marking_rb_4:
                RBValue4 = rating;
                scorTV4.setText(RBValue4 + "");
                break;
            case R.id.marking_rb_5:
                RBValue5 = rating;
                scorTV5.setText(RBValue5 + "");
                break;
        }

    }


    private void markingRating() {
        BmobQuery<ScoringBean> query = new BmobQuery<>();
        //查询当前用户是否已经打过分了
        query.addWhereEqualTo("userID", getUser().getObjectId()).addWhereEqualTo("evaluationID", interview.getObjectId()).findObjects(new FindListener<ScoringBean>() {
            @Override
            public void done(List<ScoringBean> list, BmobException e) {
                if (e == null) {
                    if (list.size() == 0) {
                        addMark();
                    } else {//打过分再次提交则覆盖上次数据
                        updateMark(list.get(0));
                    }
                    Log.e(TAG, "done: " + list.size() + getUser().getObjectId() + "===>" + interview.getObjectId());
                } else {
                    addMark();
                    showToast("发布失败" + e.getErrorCode());
                }
            }
        });

    }

    //查询当前面试者的基本信息
    private void initInterview() {
        ((TextView) $(R.id.cardview_sex)).setText(interview.getSex());
        ((TextView) $(R.id.cardview_age)).setText(interview.getAge() + "");
        ((TextView) $(R.id.cardview_post)).setText("岗位：" + BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_POST,interview.getPost()));
        ((TextView) $(R.id.cardview_department)).setText("部门：" + BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_DEPART,interview.getDepartment()));
        ((TextView) $(R.id.cardview_phone)).setText(interview.getPhone());
//        String str = interview.getRole()==1? "面试日期：":"考核日期：";
        ((TextView) $(R.id.cardview_date)).setText("日期：" + interview.getDate());
    }

    //新建打分
    private void addMark() {
        ScoringBean scoringBean = new ScoringBean();
        scoringBean.setUserID(getUser().getObjectId());
        scoringBean.setUserName(getUser().getUserName_CHS());
        scoringBean.setEvaluationID(interview.getObjectId());
        scoringBean.setEvaluationName(interview.getName());
        scoringBean.setMarkType(1);
        scoringBean.setConclusion(conclusionET.getText().toString());
        scoringBean.setScore_A(RBValue1);
        scoringBean.setScore_B(RBValue2);
        scoringBean.setScore_C(RBValue3);
        scoringBean.setScore_D(RBValue4);
        scoringBean.setScore_E(RBValue5);
        scoringBean.setDate(DateTimeUtil.gainCurrentDateString());
        Log.e(TAG, "onRatingChanged: " + scoringBean.toString());
        scoringBean.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    setMarkType(scoringBean.getEvaluationID());
                    Log.e(TAG, "done: 新建成功" + scoringBean.getEvaluationID());
                } else {
                    showToast("发布失败：" + e.getErrorCode());
                    Log.e(TAG, "done: " + e);
                }
            }
        });
    }

    //修改打分（前提：已经在该面试者上打过分）
    private void updateMark(ScoringBean bean) {
        ScoringBean scoringBean = new ScoringBean();
        scoringBean.setScore_A(RBValue1);
        scoringBean.setScore_B(RBValue2);
        scoringBean.setScore_C(RBValue3);
        scoringBean.setScore_D(RBValue4);
        scoringBean.setScore_E(RBValue5);
        scoringBean.setDate(DateTimeUtil.gainCurrentDateString());
        scoringBean.update(bean.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("修改成功");
                } else {
                    showToast("修改失败：" + e.getErrorCode());
                    Log.e(TAG, "done: " + e.getMessage());
                }
            }
        });
    }

    private void setMarkType(String objId) {
        ObjectBean bean = new ObjectBean();
        bean.setMarkType(true);
        bean.update(objId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("提交成功");
                } else {
                    showToast("发布失败：" + e.getErrorCode());
                    Log.e(TAG, "done: " + e);
                }
            }
        });
    }

    private static class MyHandler extends Handler {
        private final WeakReference<MarkingActivity> mActivity;

        public MyHandler(MarkingActivity activity) {
            mActivity = new WeakReference<MarkingActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MarkingActivity activity = mActivity.get();
            if (activity != null) {

            }
        }

    }
}
