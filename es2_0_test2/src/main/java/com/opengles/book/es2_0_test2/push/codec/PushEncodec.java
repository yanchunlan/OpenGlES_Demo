package com.opengles.book.es2_0_test2.push.codec;

import android.content.Context;

import com.opengles.book.es2_0_test2.push.render.PushRender;

/**
 * author:  ycl
 * date:  2019/1/7 16:47
 * desc:
 */
public class PushEncodec extends BasePushEncoder {
    public PushEncodec(Context context, int textureId) {
        setRenderer(new PushRender(context, textureId));
        setRenderMode(BasePushEncoder.RENDERMODE_CONTINUOUSLY);
    }
}
