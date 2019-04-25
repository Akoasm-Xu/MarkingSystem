package com.fhjs.casic.markingsystem.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.fhjs.casic.markingsystem.R;
import com.lxj.xpopup.core.CenterPopupView;

public class LongCenterPopup extends CenterPopupView {
    public LongCenterPopup(@NonNull Context context) {
        super(context);
    }

    private OnClickCurrentTypeListener listener;

    @Override
    protected int getImplLayoutId() {
        return R.layout.pop_center_long;
    }


    @Override
    protected void onCreate() {
        super.onCreate();
        findViewById(R.id.popup_long_current_type).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            dismissWith(new Runnable() {
                @Override
                public void run() {
                    if (listener!=null){
                        listener.onListener(v);
                    }
                }
            });

            }
        });
        findViewById(R.id.popup_long_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setListener(OnClickCurrentTypeListener listener) {
        this.listener = listener;
    }

    public interface OnClickCurrentTypeListener {
        void onListener(View view);
    }
}
