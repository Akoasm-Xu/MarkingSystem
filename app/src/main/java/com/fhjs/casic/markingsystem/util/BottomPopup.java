package com.fhjs.casic.markingsystem.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.controller.adapter.CompareAdapter;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.view.activity.CompareActivity;
import com.lxj.xpopup.core.BottomPopupView;
import com.lxj.xpopup.util.XPopupUtils;
import com.lxj.xpopup.widget.VerticalRecyclerView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BottomPopup extends BottomPopupView {
    VerticalRecyclerView recyclerView;
    CompareAdapter adapter;
    private static final String TAG = "BottomPopup";

    public BottomPopup(@NonNull Context context, List<ObjectBean> list, ObjectBean bean) {
        super(context);
        recyclerView = findViewById(R.id.popup_recycleView);
        init(list, bean);

    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.popup_bottom;
    }

    private void init(List<ObjectBean> list, ObjectBean bean) {
        adapter = new CompareAdapter(list);
        adapter.setEmptyView(R.layout.pub_empty_text, recyclerView);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (list.get(position).getMarkType()){
                    dismissWith(new Runnable() {
                        @Override
                        public void run() {
                            List<ObjectBean> beans = new ArrayList<>();
                            beans.add(bean);
                            beans.add(list.get(position));
                            Intent intent = new Intent(getContext(), CompareActivity.class);
                            intent.putExtra("compareArray", (Serializable) beans);
                            getContext().startActivity(intent);
                        }
                    });
                }else {
                    Toast.makeText(view.getContext(),"未评分，无法比较！",Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onShow() {
        super.onShow();
    }

    @Override
    protected void onDismiss() {
        super.onDismiss();
    }

    // 最大高度为Window的0.85
    @Override
    protected int getMaxHeight() {
        return (int) (XPopupUtils.getWindowHeight(getContext()) * .85f);
    }
}
