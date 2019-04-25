package com.fhjs.casic.markingsystem.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.model.ScoringBean;

import java.util.List;

public class DetailsAdapter extends BaseQuickAdapter<ScoringBean, BaseViewHolder> {


    public DetailsAdapter(@Nullable List<ScoringBean> data) {
        super(R.layout.item_details,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ScoringBean item) {
        helper.setText(R.id.details_user_name,item.getUserName())
                .setText(R.id.details_marking_A,item.getScore_A().toString())//仪表形象得分
                .setText(R.id.details_marking_B,item.getScore_B().toString())//表达能力得分
                .setText(R.id.details_marking_C,item.getScore_C().toString())//工作态度得分
                .setText(R.id.details_marking_D,item.getScore_D()==null?"暂无分数":this.toString())//岗位能力得分
                .setText(R.id.details_marking_E,item.getScore_E()==null?"暂无分数":this.toString());//综合素质得分
    }
}
