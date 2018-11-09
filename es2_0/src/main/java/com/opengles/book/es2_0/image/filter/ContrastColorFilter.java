package com.opengles.book.es2_0.image.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * author: ycl
 * date: 2018-11-06 0:42
 * desc:
 */
public class ContrastColorFilter extends AFilter {
    private Filter filter;
    private int vChangeColor;
    private int vChangeType;

    public ContrastColorFilter(Context context, Filter filter) {
        super(context,
                "filter/half_color_vertex.sh",
                "filter/half_color_fragment.sh");
        this.filter = filter;
    }

    @Override
    protected void onDrawSet() {
        // type color是可变的

        // ###############  此处因为是int类型所有应该是i开头
        GLES20.glUniform1i(vChangeType, filter.getvChangeType());
        GLES20.glUniform3fv(vChangeColor, 1, filter.getData(), 0);
    }

    @Override
    protected void onDrawCreatedSet(int program) {
        vChangeType = GLES20.glGetUniformLocation(program, "vChangeType");
        vChangeColor = GLES20.glGetUniformLocation(program, "vChangeColor");
    }

    public Filter getFilter() {
        return filter;
    }
}
