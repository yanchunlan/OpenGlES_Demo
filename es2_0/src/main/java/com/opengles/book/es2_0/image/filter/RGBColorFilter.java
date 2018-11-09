package com.opengles.book.es2_0.image.filter;

import android.content.Context;
import android.opengl.GLES20;

/**
 * @Author: ycl
 * @Date: 2018/11/8 21:37
 * @Desc:
 */
public class RGBColorFilter extends AFilter {
    private int vChangeColor;
    private int vChangeType;

    private float r=0.5f;
    private float g=0.5f;
    private float b=0.5f;
    private int type=2;

    public RGBColorFilter(Context context) {
        super(context, "filter/half_color_vertex.sh", "filter/half_color_fragment.sh");
    }

    @Override
    protected void onDrawSet() {
        GLES20.glUniform1i(vChangeType, type);
        GLES20.glUniform3fv(vChangeColor, 1, new float[]{r, g, b}, 0);
    }

    @Override
    protected void onDrawCreatedSet(int program) {
        vChangeType = GLES20.glGetUniformLocation(program, "vChangeType");
        vChangeColor = GLES20.glGetUniformLocation(program, "vChangeColor");
    }


    public void setR(float r) {
        this.r = r;
    }

    public void setG(float g) {
        this.g = g;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setType(int type) {
        this.type = type;
    }
}

