package com.fhjs.casic.markingsystem.controller.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.model.ScoringBean;

import java.util.Collection;
import java.util.List;

public class SeeAdapter extends BaseQuickAdapter<ScoringBean, BaseViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    private Context context;


    public SeeAdapter(List<ScoringBean> datas, Context context) {
        super(R.layout.item_list, datas);
    }

    @Override
    public void setNewData(@Nullable List<ScoringBean> data) {
        super.setNewData(data);
    }

    @Override
    public void addData(@NonNull ScoringBean data) {
        super.addData(data);
    }


    @Override
    public void addData(@NonNull Collection<? extends ScoringBean> newData) {
        super.addData(newData);
    }


    @Override
    protected int getDefItemViewType(int position) {
        return super.getDefItemViewType(position);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScoringBean item) {
//        private Float s_image;//仪表形象得分
//        private Float s_express;//表达能力得分
//        private Float s_attitude;//工作态度得分
//        private Float s_ability;//岗位能力得分
//        private Float s_comprehensive;//综合素质得分
        String markString = "仪表形象得分:" + item.getScore_A()
                + "  表达能力得分:" + item.getScore_B()
                + "  工作态度得分" + item.getScore_C()
                + "  岗位能力得分" + item.getScore_D()
                + "  综合素质得分" + item.getScore_E();
        helper.setText(R.id.item_list_below_left, markString)
                .setText(R.id.item_list_below_right, null)
                .setText(R.id.item_list_top_left, item.getEvaluationName())
                .setText(R.id.item_list_top_center, item.getDate());
    }

}
