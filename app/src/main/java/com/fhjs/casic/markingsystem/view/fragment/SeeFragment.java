package com.fhjs.casic.markingsystem.view.fragment;


import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fhjs.casic.markingsystem.R;
import com.fhjs.casic.markingsystem.base.BaseFragment;
import com.fhjs.casic.markingsystem.controller.adapter.CompareAdapter;
import com.fhjs.casic.markingsystem.controller.adapter.SeeAdapter;
import com.fhjs.casic.markingsystem.model.ScoringBean;
import com.fhjs.casic.markingsystem.model.UserBean;
import com.fhjs.casic.markingsystem.view.activity.DetailsActivity;
import com.fhjs.casic.markingsystem.view.activity.MainActivity;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SeeFragment extends BaseFragment {

    private RecyclerView seeRV;
    private SwipeRefreshLayout refreshLayout;
    private SeeAdapter adapter = null;
    private List<ScoringBean> listBeans = null;
    private UserBean userBean;
    private static final String TAG = "SeeFragment";
    private static int PAGE = 1;
    private static int PAGE_COUNT = 15;
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;

    @Override
    public void initView(View view) {
        seeRV = view.findViewById(R.id.see_recyclerView);
        refreshLayout = view.findViewById(R.id.see_swipeRefresh);
        initDatas();

    }


    private void initDatas() {
        userBean = ((MainActivity) getActivity()).getUsers();
        adapter = new SeeAdapter(listBeans, getContext());
        getRefreshData();
        linearLayoutManager = new LinearLayoutManager(getContext());
        seeRV.setLayoutManager(linearLayoutManager);
        seeRV.setAdapter(adapter);
        adapter.setEmptyView(R.layout.pub_empty_text, seeRV);
        adapter.disableLoadMoreIfNotFullPage(seeRV);

        refreshLayout.setColorSchemeResources(R.color.colorBlue2);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                ScoringBean bean = listBeans.get(position);
                intent.putExtra("ScoringBean", bean);
                startActivity(intent);
            }
        });
        //刷新
        refresh();
        //加载
        seeRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(refreshLayout.isRefreshing()) return;
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem == adapter.getItemCount() - 1) {
                    load();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            }
        });
    }

    //异步加载 滑动最后一个Item的时候
    private void load() {
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                seeRV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getLoadData();
                    }
                }, 1000);
            }
        }, seeRV);
    }

    private void refresh() {
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                seeRV.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.setRefreshing(true);
                        adapter.setEnableLoadMore(false);
                        getRefreshData();
                    }
                }, 1000);
            }
        });
    }


    //刷新填充数据
    private void getRefreshData() {
        BmobQuery<ScoringBean> query = new BmobQuery<ScoringBean>();
        query.setLimit(PAGE_COUNT).order("-updatedAt").addWhereEqualTo("userID", userBean.getObjectId());
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<ScoringBean>() {
            @Override
            public void done(List<ScoringBean> list, BmobException e) {
                if (e == null) {
                    listBeans = list;
                    adapter.setNewData(listBeans);
                    refreshLayout.setRefreshing(false);
                } else {
                    showToast("获取数据失败：" + e.getErrorCode());
                }
            }
        });
    }

    //加载填充数据
    private void getLoadData() {
        BmobQuery<ScoringBean> query = new BmobQuery<ScoringBean>();
        query.setLimit(PAGE_COUNT).setSkip(PAGE * PAGE_COUNT).addWhereEqualTo("userID", userBean.getObjectId());
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);
        query.findObjects(new FindListener<ScoringBean>() {
            @Override
            public void done(List<ScoringBean> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        adapter.addData(list);
                        adapter.loadMoreComplete();
                        PAGE++;
                    } else {
                        adapter.loadMoreEnd();
                        showToast("没有更多数据了");
                    }

                } else {
                    showToast("获取数据失败：" + e.getErrorCode());
                }
            }
        });
    }


    @Override
    public int FragmentLayoutId() {
        return R.layout.fragment_see;
    }


}
