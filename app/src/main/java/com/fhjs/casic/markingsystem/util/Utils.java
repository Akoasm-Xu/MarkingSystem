package com.fhjs.casic.markingsystem.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.fhjs.casic.markingsystem.model.ObjectBean;

import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        // 去掉"-"符号
        String temp = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
        return temp;
    }


    public static String getFloatToString(Double d) {
        DecimalFormat format = new DecimalFormat(".00");
        return format.format(d);
    }



    public static String getIntToNumber(int i) {
        if (i == 2) {
            return "Ⅱ";
        } else if (i == 3) {
            return "Ⅲ";
        } else if (i == 4) {
            return "Ⅳ";
        } else if (i == 5) {
            return "Ⅴ";
        } else if (i == 6) {
            return "Ⅵ";
        } else if (i == 7) {
            return "Ⅶ";
        } else if (i == 8) {
            return "Ⅷ";
        } else if (i == 9) {
            return "Ⅸ";
        } else if (i == 10) {
            return "Ⅹ";
        } else {
            return "Ⅰ";
        }
    }


    public static void showInput(Context context, final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Activity context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        View v = context.getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static <T extends List<ObjectBean>> Map<String, T> sortMapByKey(Map<String, T> oriMap, final boolean isRise) {
        if (oriMap == null || oriMap.isEmpty()) {
            return null;
        }

        Map<String, T> sortMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (isRise) {
                    // 升序排序
                    return o1.compareTo(o2);
                } else {
                    // 降序排序
                    return o2.compareTo(o1);
                }
            }
        });
        sortMap.putAll(oriMap);
        return sortMap;
    }
    public static ColorStateList getColorStateListTest(Context context,int colorRes) {
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled}, // enabled
                new int[]{-android.R.attr.state_enabled}, // disabled
                new int[]{-android.R.attr.state_checked}, // unchecked
                new int[]{android.R.attr.state_pressed}  // pressed
        };
        int color = ContextCompat.getColor(context, colorRes);
        int[] colors = new int[]{color, color, color, color};
        return new ColorStateList(states, colors);
    }
    public static ColorStateList createSelector(int color) {
        int statePressed = android.R.attr.state_pressed;
        int stateFocesed = android.R.attr.state_focused;
        int[][] state = {{statePressed},{-statePressed},{stateFocesed},{-stateFocesed}};
        int[] array = {color,color,color,color};
        ColorStateList colorStateList = new ColorStateList(state,array);
        return colorStateList;
    }
}
