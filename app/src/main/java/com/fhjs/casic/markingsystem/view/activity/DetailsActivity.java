package com.fhjs.casic.markingsystem.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.controller.adapter.DetailsAdapter;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.ScoringBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.BottomPopup;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.lxj.xpopup.XPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * 详情页
 */
public class DetailsActivity extends BaseActivity implements View.OnClickListener {
    private ScoringBean scoringBean;
    private List<ScoringBean> list;
    private List<ObjectBean> compareList;
    private RecyclerView detailsRV;
    private SwipeRefreshLayout refreshLayout;
    private DetailsAdapter adapter = null;
    private RadarChart radarChart;
    private static final String TAG = "DetailsActivity";
    private static List<String> xList;
    private ObjectBean interviewBean;
    private final static int LOAD_DATA_INFO=10001;
    private final static int LOAD_DATA_COMPARE=10002;
    private final static int LOAD_DATA_OTHER=10003;
    private NestedScrollView nestedscrollview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        initView();
        initDatas();
    }

    static {
        xList = new ArrayList<>();//X轴数据
        xList.add("A");
        xList.add("B");
        xList.add("C");
        xList.add("D");
        xList.add("E");
    }
     @SuppressLint("HandlerLeak")

    Handler handler = new Handler(new Handler.Callback() {
         @Override
         public boolean handleMessage(Message msg) {
             switch (msg.what){
                case LOAD_DATA_INFO:

                    getCompareData();
                    break;
            }
             return false;
         }
     });
    private void initView() {
        detailsRV = $(R.id.details_recyclerView);
        refreshLayout = $(R.id.details_swipeRefresh);

        radarChart = $(R.id.details_radarChart);
        nestedscrollview=$(R.id.details_nestedscrollview);

    }

    private void initDatas() {
        refreshLayout.setColorSchemeResources(R.color.colorBlue2);
        scoringBean = (ScoringBean) getIntent().getSerializableExtra("ScoringBean");
        ((TextView) $(R.id.title_right)).setText("比较");
        ((TextView) $(R.id.title_text)).setText("打分详情");

        $(R.id.title_image).setOnClickListener(this);
        $(R.id.title_right).setOnClickListener(this);
        initInterview();
        adapter = new DetailsAdapter(list);
        detailsRV.setLayoutManager(new LinearLayoutManager(this));
        detailsRV.setAdapter(adapter);
        getListData();
        initRadarChart();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                detailsRV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                        getListData();
                        refreshLayout.setRefreshing(false);
                    }
                }, 1000);
            }
        });

        if (getUser().getAdmin()) {//管理员 可查看平均分以及面试人的打分情况
            getAvgrageData();
        } else {
            getMarkingData();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_image:
                ActivityUtil.getInstance().finishActivity(this);
                break;
            case R.id.title_right:
                if (!getUser().getAdmin()){
                    showToast("非管理员，无权限！");
                    return;
                }
               new  XPopup.Builder(this).asCustom(new BottomPopup(this,compareList,interviewBean)).show();
                break;
        }
    }


    @Override
    protected void init() {

    }

    //获取列表的数据
    private void getListData() {
        BmobQuery<ScoringBean> query = new BmobQuery<>();
        query.setLimit(30).addWhereEqualTo("evaluationID", scoringBean.getEvaluationID());
        if (!getUser().getAdmin()) {
            query.addWhereEqualTo("userID", getUser().getObjectId());
        }
        query.findObjects(new FindListener<ScoringBean>() {
            @Override
            public void done(List<ScoringBean> data, BmobException e) {
                if (e == null) {
                    list = data;
                    adapter.setNewData(list);
                    nestedscrollview.fling(0);
                    nestedscrollview.smoothScrollTo(0, 0);
                    Log.e(TAG, "done: " + data.size() + "   evaluationID==>" + scoringBean.getEvaluationID());
                } else {
                    showToast("获取数据失败：" + e.getErrorCode());
                }
            }
        });

    }

    //获取当前用户打分的详情
    private void getMarkingData() {
        BmobQuery<ScoringBean> query = new BmobQuery<>();
        query.addWhereEqualTo("evaluationID", scoringBean.getEvaluationID())
                .addWhereEqualTo("userID", getUser().getObjectId())
                .findObjects(new FindListener<ScoringBean>() {
                    @Override
                    public void done(List<ScoringBean> list, BmobException e) {
                        if (e == null) {
                            ScoringBean bean = list.get(0);
                            setDataRadarChart(bean.getScore_A(), bean.getScore_B(), bean.getScore_C(), bean.getScore_D(), bean.getScore_E());
                        } else {
                            showToast("数据异常：" + e.getErrorCode());
                        }
                    }
                });
    }

    //获取当前用户得分的平均分情况
    private void getAvgrageData() {
        BmobQuery<ScoringBean> query = new BmobQuery<>();
        query.average(new String[]{"s_image", "s_express", "s_attitude", "s_ability", "s_comprehensive"});
        query.addWhereEqualTo("evaluationID", scoringBean.getEvaluationID()).findStatistics(ScoringBean.class, new QueryListener<JSONArray>() {
            @Override
            public void done(JSONArray jsonArray, BmobException e) {
                if (e == null) {
                    if (jsonArray != null) {
                        try {
                            Double scoreA = null, scoreB = null, scoreC = null, scoreD = null, scoreE = null;
                            JSONObject obj = jsonArray.getJSONObject(0);
                            scoreA = obj.getDouble("_avgS_image");
                            scoreB = obj.getDouble("_avgS_express");
                            scoreC = obj.getDouble("_avgS_attitude");
                            scoreD = obj.getDouble("_avgS_ability");
                            scoreE = obj.getDouble("_avgS_comprehensive");
                            setDataRadarChart(scoreA, scoreB, scoreC, scoreD, scoreE);
                            String str = "各项平均值：    A:" + getFloatToString(scoreA) + "    B:" + getFloatToString(scoreB) +
                                    "    C:" + getFloatToString(scoreC) + "    D:" + getFloatToString(scoreD) + "    E:" + getFloatToString(scoreE);

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                } else {
                    showToast("错误代码：" + e.getErrorCode());
                }
            }
        });
    }

    public String getFloatToString(Double d) {
        DecimalFormat format = new DecimalFormat(".00");
        return format.format(d);
    }



    //加载比较人员的数据
    private void getCompareData() {
        BmobQuery<ObjectBean> query = new BmobQuery<ObjectBean>();
        if (interviewBean==null){
            showToast("暂无数据，请稍后再试。");
            return;
        }
        query.setLimit(50).addWhereEqualTo("post", interviewBean.getPost()).addWhereNotEqualTo("objectId",interviewBean.getObjectId());
        Log.e(TAG, "getCompareData: "+interviewBean.getPost() );
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<ObjectBean>() {
            @Override
            public void done(List<ObjectBean> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        compareList= list;
                        Log.e(TAG, "getCompareData: "+list.size() );
                    }

                } else {
                    Log.e(TAG, "done: 获取数据失败：" + e);
                }
            }
        });
    }

    //查询当前面试者的基本信息
    private void initInterview() {
        BmobQuery<ObjectBean> query = new BmobQuery<>();
        Log.e(TAG, "initInterview: "+scoringBean.getEvaluationID() );
        query.getObject(scoringBean.getEvaluationID(), new QueryListener<ObjectBean>() {
            @Override
            public void done(ObjectBean b, BmobException e) {
                if (e == null) {
                    interviewBean=b;
                    Log.e(TAG, "done: "+b.toString() );
                    handler.sendEmptyMessage(LOAD_DATA_INFO);
                    ((TextView) $(R.id.cardview_name)).setText(  b.getName());
                    ((TextView) $(R.id.cardview_sex)).setText(  b.getSex());
                    ((TextView) $(R.id.cardview_age)).setText(b.getAge().toString());
                    ((TextView) $(R.id.cardview_post)).setText("面试岗位：" +  BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_POST,b.getPost()));
                    ((TextView) $(R.id.cardview_department)).setText("面试部门：" + BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_DEPART,b.getDepartment()));
                    ((TextView) $(R.id.cardview_phone)).setText( b.getPhone());
                    ((TextView) $(R.id.cardview_date)).setText( b.getDate());

                } else {

                    Log.e(TAG, "done: 查询异常"+e );
                }
            }
        });
    }

    private void initRadarChart() {

        radarChart.setSkipWebLineCount(5); // 允许不绘制从中心发出的线，当线多时较有用。默认为0
        radarChart.setWebLineWidth(1.5f);  // 绘制线条宽度，圆形向外辐射的线条
        radarChart.setWebLineWidthInner(1f);    // 内部线条宽度，外面的环状线条
        radarChart.setWebAlpha(100); // 所有线条WebLine透明度
        radarChart.setTouchEnabled(false);//允许启用/禁用与图表的所有可能的触摸交互。
        radarChart.animateXY(//设置绘制动画
                1400, 1400,
                Easing.EasingOption.EaseOutBack,
                Easing.EasingOption.EaseOutBack);
        radarChart.setExtraOffsets(0,20f,0,0);
        //隐藏x轴描述
        Description description = new Description();
        description.setEnabled(false);
        radarChart.setDescription(description);
        XAxis xAxis = radarChart.getXAxis();//X轴对象
        xAxis.setTextSize(16f);  // X坐标值字体大小
        xAxis.setTextColor(getResources().getColor(R.color.ColorGray2));// X坐标值字体颜色
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xList.get((int) value % xList.size());
            }
        });
        YAxis yAxis = radarChart.getYAxis();//Y轴对象
        yAxis.setEnabled(false);
        yAxis.setTextColor(getResources().getColor(R.color.colorBlue2));
        yAxis.setAxisMinimum(0f);
        Legend l = radarChart.getLegend();// 图例
        l.setEnabled(false);

//        l.setXEntrySpace(2f);        // 图例X间距
//        l.setYEntrySpace(1f);    // 图例Y间距
//        l.setForm(Legend.LegendForm.NONE);//图例的图形为无
//        l.setOrientation(Legend.LegendOrientation.VERTICAL);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);// 垂直：居上

    }

    //设置蜘蛛网图的数据
    private void setDataRadarChart(double scoreA, double scoreB, double scoreC, double scoreD, double scoreE) {
        List<RadarEntry> radarEntries = new ArrayList<>();//Y轴数据
        radarEntries.add(new RadarEntry((float) scoreA, "A"));
        radarEntries.add(new RadarEntry((float) scoreB, "B"));
        radarEntries.add(new RadarEntry((float) scoreC, "C"));
        radarEntries.add(new RadarEntry((float) scoreD, "D"));
        radarEntries.add(new RadarEntry((float) scoreE, "E"));
        RadarDataSet iRadarDataSet = new RadarDataSet(radarEntries, null);//绑定Y轴数据
        iRadarDataSet.setValueFormatter(new MyValueFormatter());
        iRadarDataSet.setColor(this.getResources().getColor(R.color.colorBlue2));

        iRadarDataSet.setFillColor(this.getResources().getColor(R.color.colorBlue2));
        iRadarDataSet.setDrawFilled(true);//填充
        iRadarDataSet.setLineWidth(1f);//线宽
        iRadarDataSet.setFillAlpha(20);
        iRadarDataSet.setValueTextSize(14f);
        iRadarDataSet.setValueTextColor(this.getResources().getColor(R.color.ColorBlack2));
//        iRadarDataSet.setValueTextColors(colors);
        RadarData radarData = new RadarData(iRadarDataSet);
        radarData.setLabels(xList);//绑定X轴数据

        radarChart.setData(radarData);
        radarChart.invalidate();

    }

    class MyValueFormatter implements IValueFormatter {

        protected DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat(".0");
        }

        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return mFormat.format(value);
        }

    }
//    private void getDetails(){
//        BmobQuery<PostBean> query = new BmobQuery();
//        query.setLimit(50).findObjects(new FindListener<PostBean>() {
//            @Override
//            public void done(List<PostBean> l, BmobException e) {
//                if (e==null){
//
//                    postList=l;
//                }
//            }
//        });
//        BmobQuery<DepartmentBean> query1 = new BmobQuery();
//        query1.setLimit(50).findObjects(new FindListener<DepartmentBean>() {
//            @Override
//            public void done(List<DepartmentBean> l, BmobException e) {
//                if (e==null){
//                    departmenList=l;
//                }
//            }
//        });
//    }
//
//    private String  getIndexToPost(Integer key){
//        if (postList!=null){
//            for (PostBean bean:postList){
//                if (bean.getId_key()==key){
//                    return bean.getValue();
//                }
//            }
//        }
//
//        return "";
//    }
//    private String  getIndexToDepartment(Integer key){
//      if (departmenList!=null){
//          for (DepartmentBean bean:departmenList){
//              if (bean.getId_key()==key){
//                  return bean.getValue();
//              }
//          }
//      }
//        return "";
//    }
}
