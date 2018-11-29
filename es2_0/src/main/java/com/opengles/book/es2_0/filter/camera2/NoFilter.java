package com.opengles.book.es2_0.filter.camera2;

import android.content.res.Resources;

import com.opengles.book.es2_0.filter.AFilter;

/**
 * author:  ycl
 * date:  2018/11/29 11:44
 * desc:
 */
public class NoFilter extends AFilter {
    public NoFilter(Resources mRes) {
        super(mRes);
    }

    @Override
    protected void onCreate() {
        createProgramByAssetsFile("shader/base_vertex.sh","shader/base_fragment.sh");
    }

    @Override
    protected void onSizeChanged(int width, int height) {

    }
}
