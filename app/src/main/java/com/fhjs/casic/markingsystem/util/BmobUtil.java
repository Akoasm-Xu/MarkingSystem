package com.fhjs.casic.markingsystem.util;

import android.util.Log;

import com.fhjs.casic.markingsystem.model.AssessmentBean;
import com.fhjs.casic.markingsystem.model.TypeBean;
import com.fhjs.casic.markingsystem.model.UserBean;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

public class BmobUtil {
    private static List<TypeBean> typeList = null;
    private static List<UserBean> userList = null;
    private static BmobUtil instance;
    private static Map<String, List<TypeBean>> map = null;
    private static Map<Integer, List<AssessmentBean>> aMap = null;

    private static final String TAG = "BmobUtil";

    private BmobUtil() {

    }

    public static synchronized BmobUtil getInstance() {
        if (instance == null) {
            instance = new BmobUtil();
        }
        return instance;
    }


    public void initData() {
        BmobQuery<TypeBean> query = new BmobQuery();
        boolean isCache = query.hasCachedResult(TypeBean.class);
        if (isCache) {
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ELSE_NETWORK);    // 如果有缓存的话，则设置策略为CACHE_ELSE_NETWORK
        } else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ELSE_CACHE);    // 如果没有缓存的话，则设置策略为NETWORK_ELSE_CACHE
        }
        query.setLimit(150).findObjects(new FindListener<TypeBean>() {
            @Override
            public void done(List<TypeBean> l, BmobException e) {
                if (e == null) {
                    typeList = l;
                    setMapData(typeList);
                }
            }
        });
        BmobQuery<UserBean> userQuery = new BmobQuery();
        userQuery.setLimit(50).findObjects(new FindListener<UserBean>() {
            @Override
            public void done(List<UserBean> l, BmobException e) {
                if (e == null) {
                    userList = l;
                }
            }
        });

        BmobQuery<AssessmentBean> assessQuery = new BmobQuery();
        assessQuery.setLimit(100).findObjects(new FindListener<AssessmentBean>() {
            @Override
            public void done(List<AssessmentBean> l, BmobException e) {
                if (e == null) {
                    setAssessMap(l);
                }
            }
        });


//        Bmob.getServerTime(new QueryListener<Long>() {
//
//            @Override
//            public void done(Long aLong, BmobException e) {
//                if(e==null){
//                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//                    String times = formatter.format(new Date(aLong * 1000L));
//                    Log.i("bmob","当前服务器时间为:" + times);
//                }else{
//                    Log.i("bmob","获取服务器时间失败:" + e.getMessage());
//                }
//            }
//        });
    }

    public  Map<Integer, List<AssessmentBean>> getaMap() {
        return aMap;
    }

    private void setAssessMap(List<AssessmentBean> list) {
        aMap = new HashMap<>();
        for (AssessmentBean bean : list) {
            if (aMap.containsKey(bean.getType())) {
                aMap.get(bean.getType()).add(bean);
            } else {
                List<AssessmentBean> l = new ArrayList<>();
                l.add(bean);
                aMap.put(bean.getType(), l);
            }
        }

    }

    public List<AssessmentBean> getAssessContent(Integer type){
        if (aMap.containsKey(type)){
          return aMap.get(type);
        }
        return null;
    }

    /**
     * 分类 主要是为了方便后续值得获取
     *
     * @param l
     */
    private void setMapData(List<TypeBean> l) {
        map = new HashMap<>();//key:type   value:content
        for (TypeBean bean : l) {
            if (map.containsKey(bean.getType())) {
                map.get(bean.getType()).add(bean);
            } else {
                List<TypeBean> list = new ArrayList<>();
                list.add(bean);
                map.put(bean.getType(), list);
            }
        }
    }

    public List<UserBean> getUserList() {
        return userList;
    }


    /**
     * 根据具体的类型和键查询相应的值
     *
     * @param type 类型 post：岗位   group：组别    department：部门    assessment：考核内容  assess_type考核类型
     * @param key  键
     * @return
     */
    public String getIndexToValue(String type, Integer key) {

        if (typeList != null) {
            for (TypeBean bean : typeList) {
                if (bean.getType().equals(type)) {
                    if (bean.getId_key().equals(key)) {
                        return bean.getValue();
                    }
                }
            }
        }
        return "";
    }

    /**
     * 根据类型 查询对应的一组数据
     *
     * @param type 类型 post：岗位   group：组别    department：部门   assessment：考核内容  assess_type考核类型
     * @return
     */
    public String[] getTypeToArray(String type) {
        String array[] = null;
        if (typeList != null) {

            if (map.containsKey(type)) {
                List<TypeBean> list = map.get(type);
                array = new String[list.size()];
                for (TypeBean bean : list) {
                    array[bean.getId_key() - 1] = bean.getValue();
                }
                return array;
            }
        }
        return array;
    }

}
