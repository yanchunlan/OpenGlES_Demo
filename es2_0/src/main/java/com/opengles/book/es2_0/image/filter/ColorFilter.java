package com.opengles.book.es2_0.image.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * author: ycl
 * date: 2018-11-05 23:45
 * desc:
 */
public class ColorFilter extends AFilter {

    private Filter filter;
    private int vChangeColor;
    private int vChangeType;


    public ColorFilter(Context context, Filter filter) {
        super(context,
                "filter/default_vertex.sh",
                "filter/color_fragment.sh");
        this.filter = filter;
    }

    @Override
    protected void onDrawSet() {
        // type color是可变的
        GLES20.glUniform1f(vChangeType, filter.getvChangeType());
        GLES20.glUniform3fv(vChangeColor, 1, filter.getData(), 0);
    }

    @Override
    protected void onDrawCreatedSet(int program) {
        vChangeType = GLES20.glGetUniformLocation(program, "vChangeType");
        vChangeColor = GLES20.glGetUniformLocation(program, "vChangeColor");
    }
}

