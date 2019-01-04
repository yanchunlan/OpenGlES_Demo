package com.opengles.book.es2_0_test2.muti.surface2;

import android.content.Context;
import android.util.AttributeSet;

import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.muti.surface.MyRender;

/**
 * author: ycl
 * date: 2019-01-04 10:56
 * desc:
 */
public class MutiSurfaceView extends EglSurfaceView {
    private MutiRender mMyRender;

    public MutiSurfaceView(Context context) {
        this(context, null);
    }

    public MutiSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MutiSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mMyRender = new MutiRender(context);
        setRenderer(mMyRender);
        setRenderMode(EglSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    public void setTextureId(int textureId, int index) {
        if (mMyRender != null) {
            mMyRender.setTextureId(textureId, index);
        }
    }
}
