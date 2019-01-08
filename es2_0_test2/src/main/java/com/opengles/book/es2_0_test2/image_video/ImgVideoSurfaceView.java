package com.opengles.book.es2_0_test2.image_video;

import android.content.Context;
import android.util.AttributeSet;

import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.muti.surface.MyRender;

/**
 * author:  ycl
 * date:  2019/1/7 17:35
 * desc:
 */
public class ImgVideoSurfaceView extends EglSurfaceView {
    private ImgVideoRender mImgVideoRender;
    private int fbotextureid;


    public ImgVideoSurfaceView(Context context) {
        this(context, null);
    }

    public ImgVideoSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImgVideoSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mImgVideoRender = new ImgVideoRender(context);
        setRenderer(mImgVideoRender);
        setRenderMode(EglSurfaceView.RENDERMODE_WHEN_DIRTY);
        mImgVideoRender.setOnRenderCreateListener(new MyRender.OnRenderCreateListener() {
            @Override
            public void onCreate(int textureId) {
                fbotextureid = textureId;
            }
        });
    }

    /**
     * 一张图片一张图片的去绘制
     */
    public void setCurrentImg(int imgsr) {
        if (mImgVideoRender != null) {
            mImgVideoRender.setCurrentSrcImg(imgsr);
            requestRender();
        }
    }

    public int getFbotextureid() {
        return fbotextureid;
    }
}
