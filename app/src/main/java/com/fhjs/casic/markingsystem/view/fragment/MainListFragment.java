package com.fhjs.casic.markingsystem.view.fragment;


import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseFragment;
import com.fhjs.casic.markingsystem.controller.adapter.MainAdapter;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.MainListBean;
import com.fhjs.casic.markingsystem.util.DateTimeUtil;
import com.fhjs.casic.markingsystem.util.Utils;
import com.fhjs.casic.markingsystem.view.activity.InputActivity;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;
import com.fhjs.casic.markingsystem.view.activity.PAMarkingActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SQLQueryListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainListFragment extends BaseFragment implements View.OnClickListener {
    private RecyclerView mainRV;
    private SwipeRefreshLayout refreshLayout;
    private List<MultiItemEntity> listBeans;
    private MainAdapter adapter = null;
    public TextView dateTv, titleTv;
    private static final String TAG = "MainListFragment";
    private List<ObjectBean> lists;
    private final MyHandler handler = new MyHandler((MainActivity) getActivity());
    private String dateArray[] = null;
    private CheckBox checkAll;
    private static final int WHAT_QUERY_DATE = 1;
    private FloatingActionButton fab;
    private boolean isSelectedState = false;
    private ObjectAnimator rotation = null;

    @Override
    public void initView(View view) {
        mainRV = view.findViewById(R.id.main_recyclerView);
        refreshLayout = view.findViewById(R.id.main_swipeRefresh);
        dateTv = view.findViewById(R.id.include_select_date);
        checkAll = view.findViewById(R.id.include_select_all_check);
        fab = view.findViewById(R.id.main_list_fab);
        titleTv = view.findViewById(R.id.include_select_center);
        getDateCorrespondData();
        adapter = new MainAdapter(listBeans, getContext());
        mainRV.setAdapter(adapter);
        mainRV.setLayoutManager(new LinearLayoutManager(getActivity()));

        initDatas();
        getDate();

    }

    /**
     * 旋转动画
     */
    private void startAnimator(boolean isCancel) {

        if (isCancel) {
            fab.setBackgroundTintList(Utils.getColorStateListTest(getContext(), R.color.ColorWhite1));
            fab.setColorFilter(getContext().getResources().getColor(R.color.ColorRed3));
            rotation = ObjectAnimator.ofFloat(fab, "rotation", 0f, 225f);//
            dateTv.setText("去考核");
        } else {
            titleTv.setText("任务列表");
            dateTv.setText(DateTimeUtil.gainCurrentDateString());
            fab.setBackgroundTintList(Utils.getColorStateListTest(getContext(), R.color.colorBlue3));
            fab.setColorFilter(getContext().getResources().getColor(R.color.ColorWhite1));
            fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#007FFF")));
            rotation = ObjectAnimator.ofFloat(fab, "rotation", 225f, 0f);//
        }
        rotation.setDuration(500);
        rotation.start();
    }


    private void initDatas() {
        dateTv.setOnClickListener(this);
        fab.setOnClickListener(this);
        titleTv.setText("任务列表");
        dateTv.setText(DateTimeUtil.gainCurrentDateString());
        refreshLayout.setColorSchemeResources(R.color.colorBlue2);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mainRV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                        getDateCorrespondData();
                        refreshLayout.setRefreshing(false);

                    }
                }, 1000);
            }
        });
        adapter.disableLoadMoreIfNotFullPage(mainRV);
        adapter.setLongClickListeners(new MainAdapter.OnLongClickListeners() {
            @Override
            public void onSelectedType(boolean isAll, Integer type) {
                isSelectedState = true;
                refreshLayout.setEnabled(!isSelectedState);
                checkAll.setVisibility(View.VISIBLE);
                startAnimator(true);
                if (isAll) {
                    checkAll.setChecked(true);
                }
            }
        });

    }

    @Override
    public int FragmentLayoutId() {
        return R.layout.fragment_main_list;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.include_select_date:
                if (isSelectedState){
                    showToast("去考核...");
                    getContext().startActivity(new Intent(getActivity(),PAMarkingActivity.class));
                }else {
                    selectDate();
                }
                break;
            case R.id.main_list_fab:
                if (isSelectedState) {//判断是否为多选状态
                    checkAll.setVisibility(View.GONE);
                    startAnimator(false);
                    adapter.setSelectedState(false);
                    isSelectedState = false;
                    refreshLayout.setEnabled(true);
                } else {
                    getContext().startActivity(new Intent(getContext(), InputActivity.class));
                }

                break;
        }
    }

    private void selectDate() {
        new XPopup.Builder(getActivity()).asBottomList("请选择一项", dateArray,
                new OnSelectListener() {
                    @Override
                    public void onSelect(int position, String text) {
                        dateTv.setText(text);
                        refreshLayout.setRefreshing(true);
                        getDateCorrespondData();
                        refreshLayout.setRefreshing(false);
                    }
                }).show();
    }

    private void getDate() {


        BmobQuery<ObjectBean> query = new BmobQuery<ObjectBean>();
        query.groupby(new String[]{"date"});// 按照时间分组
        query.order("date");                  // 排列
        query.setHasGroupCount(true);              // 统计每一天有多少个玩家的得分记录，默认不返回分组个数
        query.findStatistics(ObjectBean.class, new QueryListener<JSONArray>() {

            @Override
            public void done(JSONArray ary, BmobException e) {
                if (e == null) {
                    if (ary != null) {
                        int length = ary.length();
                        dateArray = new String[length];
                        try {
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = ary.getJSONObject(i);
                                String date = obj.getString("date");
                                int count = obj.getInt("_count");//setHasGroupCount设置为true时，返回的结果中含有"_count"字段
                                dateArray[i] = date;
                            }
                            dateTv.setText(dateArray[length - 1]);
                            getDateCorrespondData();
                            handler.sendEmptyMessage(WHAT_QUERY_DATE);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else {
                        showToast("查询成功，无数据");
                    }
                } else {
                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());

                }
            }
        });
    }

    //根据时间获取相应的数据
    private void getDateCorrespondData() {
        BmobQuery<ObjectBean> query = new BmobQuery<ObjectBean>();
        query.addWhereEqualTo("date", dateTv.getText().toString());
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<ObjectBean>() {
            @Override
            public void done(List<ObjectBean> list, BmobException e) {
                if (e == null) {
                    lists = list;
                    dataGroupBy(lists);

                } else {
                    Log.e(TAG, "done: " + e);
                }
            }
        });
    }

    //数据根据date分组
    private void dataGroupBy(List<ObjectBean> lists) {
        Map<Integer, List<ObjectBean>> map = new HashMap<>();//先new一个map对象
        for (ObjectBean bean : lists) { //循环得到 ObjectBean 里面的每一个对象
            List<ObjectBean> list = map.get(bean.getRole()); //通过.getDate去判断里面有没有对象
            if (list == null) { //判断如果没有获取到
                list = new ArrayList<>();//把它当做值
                map.put(bean.getRole(), list);
            }
            list.add(bean);
        }
        listBeans = new ArrayList<>();
//        Map<Integer, List<ObjectBean>> map1 = Utils.sortMapByKey(map, false);
        for (Integer key : map.keySet()) {
            MainListBean bean = new MainListBean(key, map.get(key));
            for (int i = 0; i < map.get(key).size(); i++) {
                bean.addSubItem(map.get(key).get(i));
            }
            listBeans.add(bean);
        }
        adapter.setNewData(listBeans);
    }

    private static class MyHandler extends Handler {

        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            MainActivity activity = mActivity.get();
            if (activity != null) {
                switch (msg.what) {
                    case WHAT_QUERY_DATE:
                        Log.e(TAG, "handleMessage: Data loading completed！！！");
                        break;
                }
            }
        }
    }
}
