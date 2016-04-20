package com.laohudada.yingxin.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.laohudada.yingxin.R;


/**
 * Created by tao on 2016/1/27.
 * RecyclerView的HeaderView，
 */
public class HeaderLayout extends RelativeLayout {
    public HeaderLayout(Context context) {
        super(context);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        inflate(context, R.layout.sample_header, this);
    }
}
