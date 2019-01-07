package com.opengles.book.es2_0_test2.encodec;

import android.content.Context;

import com.opengles.book.es2_0_test2.encodec.render.EncodecRender;

/**
 * author:  ycl
 * date:  2019/1/7 16:47
 * desc:
 */
public class MediaEncodec extends BaseMediaEncoder {
    public MediaEncodec(Context context, int textureId) {
        setRenderer(new EncodecRender(context, textureId));
        setRenderMode(BaseMediaEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
