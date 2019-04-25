package com.fhjs.casic.markingsystem.controller.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.model.ObjectBean;
import com.fhjs.casic.markingsystem.model.MainListBean;
import com.fhjs.casic.markingsystem.util.BmobUtil;
import com.fhjs.casic.markingsystem.util.ConstantUtil;
import com.fhjs.casic.markingsystem.util.LongCenterPopup;
import com.fhjs.casic.markingsystem.util.Utils;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;
import com.fhjs.casic.markingsystem.view.activity.MarkingActivity;
import com.fhjs.casic.markingsystem.view.activity.PAMarkingActivity;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnSelectListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.util.V;

public class MainAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int TYPE_Head = 0;
    public static final int TYPE_ITEM = 1;
    private Context context;
    private OnLongClickListeners longClickListeners = null;//长按item的监听
    private Map<Integer, Boolean> selectMap = null;
    private boolean currentAllState = false;
    private static final String TAG = "MainAdapter";

    private List<ObjectBean> paxList = null;
    private int lastTag = 0;
    private List<ObjectBean> objectBeans = null;

    public MainAdapter(List<MultiItemEntity> data, Context context) {
        super(data);
        this.context = context;
        addItemType(TYPE_Head, R.layout.item_main_head);
        addItemType(TYPE_ITEM, R.layout.item_list);


    }

    @Override
    public void setNewData(@Nullable List<MultiItemEntity> data) {
        super.setNewData(data);

    }

    public void setSelectedState(boolean selectedState) {
        notifyDataSetChanged();
    }

    @Override
    protected void convert(final BaseViewHolder helper, final MultiItemEntity item) {
        if (helper.getItemViewType() == TYPE_Head) {//一级布局
            MainListBean bean = (MainListBean) item;
            if (bean.getRole().equals("绩效考核")) {
                paxList = bean.getLists();
            }
            helper.setText(R.id.item_main_head_type, bean.getRole())//父布局标题
                    .setText(R.id.item_main_head_date, bean.getDate())
                    .setText(R.id.item_main_head_count, setHeadData(bean.getLists()));//下标
            if (bean.isExpanded()) {
                helper.setImageResource(R.id.item_main_head_more, R.drawable.icon_more_below);
            } else {//
                helper.setImageResource(R.id.item_main_head_more, R.drawable.icon_more_right);
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firstItemClick(helper, bean);
                }
            });

        } else {//二级布局 子布局
            lastTag = helper.getLayoutPosition();
            ObjectBean objectBean = (ObjectBean) item;
            String left = "面试";
            String right = "面试岗位：" + BmobUtil.getInstance().getIndexToValue(ConstantUtil.VALUE_TYPE_POST, objectBean.getPost());
            helper.setVisible(R.id.item_list_center_check, false);
            if (objectBean.getRole().equals(ConstantUtil.ROLE_PAX)) {//考核
                left = "考核";
                right = "考核类型：" + Utils.getIntToNumber(objectBean.getPa_type());
            }
            helper.setText(R.id.item_list_top_left, objectBean.getName())
                    .setText(R.id.item_list_top_center, objectBean.getDate())
                    .setText(R.id.item_list_below_left, left)
                    .setText(R.id.item_list_below_right, right);


            //item的单击时间
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemClick(objectBean, view, helper);
                }
            });

            //item的长按事件及相关逻辑
            XPopup.Builder builder = new XPopup.Builder(context).watchView(helper.itemView);
            helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (objectBean.getRole().equals(ConstantUtil.ROLE_PAX)) {
                        showLongPopup(builder, objectBean);
                    }
                    return true;
                }
            });

        }
    }



    /**
     * item单击事件
     *
     * @param objectBean
     * @param view
     * @param helper
     */
    private void itemClick(ObjectBean objectBean, View view, BaseViewHolder helper) {

        String user = objectBean.getObject_userIDs();
        MainActivity activity = (MainActivity) context;
        if (user == null) {
            return;
        }
        if (!user.contains(activity.getUsers().getObjectId())) {
            activity.showToast("您无权限考核此人。");
            return;
        }
        Intent intent=null;
        if (objectBean.getRole()==ConstantUtil.ROLE_PAX){
            intent = new Intent(view.getContext(), PAMarkingActivity.class);
            intent.putExtra("dataType",ConstantUtil.INTENT_SINGLE_DATA);
            intent.putExtra("bean", objectBean);
        }else {
            intent = new Intent(view.getContext(), MarkingActivity.class);
            intent.putExtra("bean", objectBean);
        }
        view.getContext().startActivity(intent);


    }

    /**
     * 一级布局的单击事件
     *
     * @param helper
     * @param item
     */
    private void firstItemClick(BaseViewHolder helper, MainListBean item) {
        int pos = helper.getLayoutPosition();

        if (item.getRole().equals(ConstantUtil.ROLE_INTERVIEW)) {//若在多选操作时 面试类将收起
            collapse(helper.getLayoutPosition());//收起
            return;
        }
        if (item.isExpanded()) {//展开状态
            collapse(pos);//收起
            helper.setImageResource(R.id.item_main_head_more, R.drawable.icon_more_below);
        } else {//
            expand(pos);//展开
            helper.setImageResource(R.id.item_main_head_more, R.drawable.icon_more_right);
        }
    }

    private int getSelectMapCount() {
        int count = 0;
        if (selectMap == null) {
            return count;
        }
        for (boolean state : selectMap.values()) {
            if (state) {
                count++;
            }
        }
        return count;
    }

    /**
     * 显示长按操作时的popupWindow和相关逻辑处理
     *
     * @param builder
     * @param objectBean
     */
    private void showLongPopup(XPopup.Builder builder, ObjectBean objectBean) {
        LongCenterPopup pp = new LongCenterPopup(context);
        builder.autoDismiss(true).asCustom(pp).show();

        pp.setListener(new LongCenterPopup.OnClickCurrentTypeListener() {
            @Override
            public void onListener(View view) {
                Intent intent = new Intent(context, PAMarkingActivity.class);
                intent.putExtra("dataType",ConstantUtil.INTENT_MULTI_DATA);
                intent.putExtra("sameTypeData", (Serializable) getTypeData(objectBean.getPa_type(), objectBeans));
                Log.e(TAG, "onListener: ==>>"+objectBean.getPa_type() );
                context.startActivity(intent);

            }
        });


    }

    /**
     * 根据考核的类型选择同类型的对象集合
     *
     * @param type
     * @param list
     * @return
     */
    private List<ObjectBean> getTypeData(int type, List<ObjectBean> list) {
        List<ObjectBean> beans = new ArrayList<>();
        for (ObjectBean bean : list) {
            if (bean.getRole() == ConstantUtil.ROLE_PAX && bean.getPa_type() == type) {
                beans.add(bean);
            }
        }
        return beans;
    }


    private String setHeadData(List<ObjectBean> list) {
        String type = "面试";
        StringBuffer buffer = new StringBuffer();
        //包含张三李四...等4人  包含张三李四2人面试
        int count = list.size();
        buffer.append("此项包含：");

        for (int i = 0; i < count; i++) {
            buffer.append(list.get(i).getName());
            if (i >= 2) {//大于三人将省略
                buffer.append("等");
                break;
            }
            if (count > 1 && i != count - 1) {
                buffer.append("、");
            }
            if (list.get(i).getRole().equals(ConstantUtil.ROLE_PAX)) {
                objectBeans = list;
                type = "考核";
            }
        }
        buffer.append(count + "人的" + type);
        return buffer.toString();
    }


    public void setLongClickListeners(OnLongClickListeners longClickListeners) {
        this.longClickListeners = longClickListeners;
    }


    public interface OnLongClickListeners {
        void onSelectedType(boolean isAll, Integer type);
    }

}
