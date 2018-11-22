package com.opengles.book.es2_0.filter;

import android.content.res.Resources;

/**
 * author:  ycl
 * date:  2018/11/22 14:02
 * desc:
 */
public class GrayFilter extends AFilter {
    public GrayFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh","shader/color/gray_fragment.frag");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
