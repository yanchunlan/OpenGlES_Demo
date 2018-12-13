package com.opengles.book.es2_0.camera.camera4;

import android.graphics.BitmapFactory;

import com.opengles.book.es2_0.R;
import com.opengles.book.es2_0.camera.camera2.Camera2Activity;
import com.opengles.book.es2_0.camera.camera2.Camera2Renderer;
import com.opengles.book.es2_0.filter.camera2.WaterMarkFilter;

public class Camera4Activity extends Camera2Activity {

    @Override
    protected void addFilter(Camera2Renderer controller) {
        if (controller != null) {
            // 水印
            WaterMarkFilter filter = new WaterMarkFilter(getResources());
            filter.setBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.blend_bg));
            filter.setPosition(200, 200, 180, 180);
            controller.addFilter(filter);
        }
    }
}
