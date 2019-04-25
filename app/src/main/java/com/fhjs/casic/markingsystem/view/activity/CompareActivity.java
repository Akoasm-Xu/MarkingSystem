package com.fhjs.casic.markingsystem.view.activity;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseActivity;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.ScoringBean;
import com.fhjs.casic.markingsystem.util.ActivityUtil;
import com.fhjs.casic.markingsystem.util.Utils;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class CompareActivity extends BaseActivity {
    private List<ObjectBean> compareArray = null;
    private DecimalFormat mFormat;
    private RadarChart radarChart;
    private List<String> xList;
    private List<IRadarDataSet> dataSetList;
    private List<Integer> colors;
    private TextView bottomLeftTV, bottomRightTV, sumLeftTV, sumRightTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);
        initView();
    }

    @Override
    protected void init() {
        compareArray = (List<ObjectBean>) getIntent().getSerializableExtra("compareArray");
        colors = new ArrayList<>();//颜色集合
        colors.add(this.getResources().getColor(R.color.colorBlue2));
        colors.add(this.getResources().getColor(R.color.ColorYellow1));

    }

    @SuppressLint("NewApi")
    private void initView() {
        radarChart = $(R.id.compare_radarchart);
        bottomLeftTV = $(R.id.compare_bottom_name_left);
        bottomRightTV = $(R.id.compare_bottom_name_right);
        sumLeftTV = $(R.id.compare_bottom_sum_left);
        sumRightTV = $(R.id.compare_bottom_sum_right);
        bottomLeftTV.setText(compareArray.get(0).getName());
        bottomRightTV.setText(compareArray.get(1).getName());
        ((TextView) $(R.id.title_text)).setText("比较");
        ((ImageView) $(R.id.title_image)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityUtil.getInstance().finishActivity(CompareActivity.this);
            }
        });
        ((TextView) $(R.id.title_right)).setText(null);
        ((TextView) $(R.id.compare_top_name_left)).setText(compareArray.get(0).getName());
        ((TextView) $(R.id.compare_top_name_right)).setText(compareArray.get(1).getName());
        initRadarChart();
        getAvgrageData(true);

    }

    public static Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    private void initRadarChart() {
        Log.e(TAG, "init: " + radarChart);
        xList = new ArrayList<>();//X轴数据
        xList.add("A");
        xList.add("B");
        xList.add("C");
        xList.add("D");
        xList.add("E");
        dataSetList = new ArrayList<>();
        radarChart.setSkipWebLineCount(5); // 允许不绘制从中心发出的线，当线多时较有用。默认为0
        radarChart.setWebLineWidth(1.5f);  // 绘制线条宽度，圆形向外辐射的线条
        radarChart.setWebLineWidthInner(1f);    // 内部线条宽度，外面的环状线条
        radarChart.setWebAlpha(100); // 所有线条WebLine透明度
        radarChart.setTouchEnabled(false);//允许启用/禁用与图表的所有可能的触摸交互。
        radarChart.setExtraTopOffset(20f);
        radarChart.animateXY(//设置绘制动画
                1400, 1400,
                Easing.EasingOption.EaseOutBack,
                Easing.EasingOption.EaseOutBack);

        //隐藏x轴描述
        Description description = new Description();
        description.setEnabled(false);
        radarChart.setDescription(description);
        XAxis xAxis = radarChart.getXAxis();//X轴对象
        xAxis.setTextSize(16f);  // X坐标值字体大小
        xAxis.setTextColor(getResources().getColor(R.color.ColorBlack1));// X坐标值字体颜色
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xList.get((int) (value % xList.size()));
            }
        });
        YAxis yAxis = radarChart.getYAxis();//Y轴对象
        yAxis.setEnabled(true);
        yAxis.setTextColor(getResources().getColor(R.color.ColorBlack1));
        yAxis.setAxisMinimum(0f); // 设置坐标轴从0开始
//        yAxis.setAxisMaximum(6f);
        yAxis.setTextSize(14f);

        yAxis.setGranularity(1f); // 设置间隔为1
        yAxis.setLabelCount(4, true); // 设置标签个数
//... 更多
        radarChart.getLegend().setEnabled(false);// 图例


    }


    //设置蜘蛛网图的数据
    private void addData(double scoreA, double scoreB, double scoreC, double scoreD, double scoreE, int color) {
        List<RadarEntry> radarEntries = new ArrayList<>();//Y轴数据
        radarEntries.add(new RadarEntry((float) scoreA, "A"));
        radarEntries.add(new RadarEntry((float) scoreB, "B"));
        radarEntries.add(new RadarEntry((float) scoreC, "C"));
        radarEntries.add(new RadarEntry((float) scoreD, "D"));
        radarEntries.add(new RadarEntry((float) scoreE, "E"));
        RadarDataSet iRadarDataSet = new RadarDataSet(radarEntries, null);//绑定Y轴数据
        iRadarDataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                mFormat = new DecimalFormat(".0");
                return mFormat.format(value);
            }
        });
//        iRadarDataSet.setDrawFilled(true);//填充
//        iRadarDataSet.setFillColor(color);
        iRadarDataSet.setDrawValues(false);
        iRadarDataSet.setColor(color);
        iRadarDataSet.setLineWidth(2f);//线宽
        iRadarDataSet.setFillAlpha(80);
        iRadarDataSet.setValueTextSize(14f);
        iRadarDataSet.setValueTextColor(color);
        dataSetList.add(iRadarDataSet);

        radarChart.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRadarChartData();
            }
        }, 0);
//        iRadarDataSet.setValueTextColors(colors);


    }

    private void setRadarChartData() {


        RadarData radarData = new RadarData(dataSetList);
        radarData.setLabels(xList);//绑定X轴数据
        radarChart.setData(radarData);
        radarChart.invalidate();
    }

    private void getAvgrageData(boolean isLeft) {
        BmobQuery<ScoringBean> query = new BmobQuery<>();
        query.average(new String[]{"s_image", "s_express", "s_attitude", "s_ability", "s_comprehensive"});
        String evaluationID = isLeft ? compareArray.get(0).getObjectId() : compareArray.get(1).getObjectId();
        query.addWhereEqualTo("evaluationID", evaluationID).findStatistics(ScoringBean.class, new QueryListener<JSONArray>() {
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
                            if (isLeft) {
                                ((TextView) $(R.id.compare_score_left_A)).setText(Utils.getFloatToString(scoreA));
                                ((TextView) $(R.id.compare_score_left_B)).setText(Utils.getFloatToString(scoreB));
                                ((TextView) $(R.id.compare_score_left_C)).setText(Utils.getFloatToString(scoreC));
                                ((TextView) $(R.id.compare_score_left_D)).setText(Utils.getFloatToString(scoreD));
                                ((TextView) $(R.id.compare_score_left_E)).setText(Utils.getFloatToString(scoreE));
                                addData(scoreA, scoreB, scoreC, scoreD, scoreE, colors.get(0));
                                ;
                                sumLeftTV.setText(scoreA + scoreB + scoreC + scoreD + scoreE + "");
                                getAvgrageData(false);
                            } else {
                                ((TextView) $(R.id.compare_score_right_A)).setText(Utils.getFloatToString(scoreA));
                                ((TextView) $(R.id.compare_score_right_B)).setText(Utils.getFloatToString(scoreB));
                                ((TextView) $(R.id.compare_score_right_C)).setText(Utils.getFloatToString(scoreC));
                                ((TextView) $(R.id.compare_score_right_D)).setText(Utils.getFloatToString(scoreD));
                                ((TextView) $(R.id.compare_score_right_E)).setText(Utils.getFloatToString(scoreE));
                                addData(scoreA, scoreB, scoreC, scoreD, scoreE, colors.get(1));
                                sumRightTV.setText(scoreA + scoreB + scoreC + scoreD + scoreE + "");
                            }


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
}
