package com.opengles.book.es2_0_test.glsv;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.opengles.book.es2_0_test.base.BaseGLSurfaceView;
import com.opengles.book.es2_0_test.image.ImageRenderer;

import java.io.IOException;

/**
 * 展示图片的 GLSurfaceView
 */
public class ImageGLSurfaceView extends BaseGLSurfaceView {

    public ImageGLSurfaceView(Context context)  {
        super(context);

        try {
            setRenderer(new ImageRenderer(context));  // 展示图片渲染器
        } catch (IOException e) {
            e.printStackTrace();
        }

        // setRenderer(new ImageTransformRenderer(context, ImageTransformRenderer.Filter.MAGN));  // 展示图片处理渲染器

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        requestRender();
    }
}
