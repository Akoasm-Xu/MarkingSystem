package com.fhjs.casic.markingsystem.controller.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.model.ObjectBean;

import java.util.List;

public class CompareAdapter extends BaseQuickAdapter<ObjectBean, BaseViewHolder> {
    public CompareAdapter(@Nullable List<ObjectBean> data) {
        super(R.layout.item_compare, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ObjectBean item) {

        String info = "性别：" + item.getSex() + "     年龄：" + item.getAge();
        String type= item.getMarkType()?"已评分":"未评分";
        Integer color = item.getMarkType()?R.color.colorGreen1:R.color.ColorRed1;
        helper.setText(R.id.item_compare_name, item.getName())
                .setText(R.id.item_compare_date, item.getDate())
                .setText(R.id.item_compare_type, type)
                .setTextColor(R.id.item_compare_type,helper.itemView.getContext().getResources().getColor(color))
                .setText(R.id.item_compare_info, info);
//        helper.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
    }
}
