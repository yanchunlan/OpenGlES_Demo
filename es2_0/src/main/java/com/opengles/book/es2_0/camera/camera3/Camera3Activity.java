package com.opengles.book.es2_0.camera.camera3;

import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.opengles.book.es2_0.camera.camera2.Camera2Activity;
import com.opengles.book.es2_0.camera.camera2.Camera2Renderer;
import com.opengles.book.es2_0.filter.camera3.BeautyFilter;
import com.opengles.book.es2_0.filter.camera3.LookupFilter;

public class Camera3Activity extends Camera2Activity {

    private String asstesName = "lookup/purity.png";

    private LookupFilter mLookupFilter; // 面具图片
    private BeautyFilter mBeautyFilter; // 美颜

    @Override
    protected void addContentView(FrameLayout f) {
        AppCompatSeekBar seekBar = new AppCompatSeekBar(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 50);
        params.topMargin = 50;
        seekBar.setLayoutParams(params);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d("onProgressChanged", "progress: " + progress+" intensity: "+progress / 100f+" flag: "+(progress / 20 + 1));
                mLookupFilter.setIntensity(progress / 100f);
                mBeautyFilter.setFlag(progress / 20 + 1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        f.addView(seekBar);
    }

    @Override
    protected void addFilter(Camera2Renderer controller) {
        mLookupFilter = new LookupFilter(getResources());
        mLookupFilter.setIntensity(0.0f);
        mLookupFilter.setMaskImageAssets(asstesName);
        controller.addFilter(mLookupFilter);

        mBeautyFilter = new BeautyFilter(getResources());
        controller.addFilter(mBeautyFilter);
    }
}
