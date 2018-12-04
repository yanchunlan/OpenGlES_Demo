package com.opengles.book.es2_0.beautiful;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.opengles.book.es2_0.R;

import java.io.IOException;


public class BeautyActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    private GLSurfaceView glSurfaceView;
    private BeautyRenderer mRenderer;
    private ImageView mImageView;
    private Button startBtn,saveBtn;
    private boolean isStart=true;


    private SeekBar sb_step,sb_tone,sb_beauty,sb_bright;
    private static float minstepoffset= -10;
    private static float maxstepoffset= 10;
    private static float minToneValue= -5;
    private static float maxToneValue= 5;
    private static float minbeautyValue= 0;
    private static float maxbeautyValue= 2.5f;
    private static float minbrightValue= 0;
    private static float maxbrightValue= 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty);

        glSurfaceView= (GLSurfaceView) findViewById(R.id.glView);
        glSurfaceView.setEGLContextClientVersion(2);
        mRenderer =new BeautyRenderer(this);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mImageView= (ImageView) findViewById(R.id.image);
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(getResources().getAssets().open("texture/fengj.png"));
            mImageView.setImageBitmap(bitmap);
            mRenderer.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startBtn = (Button) findViewById(R.id.startbtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isStart){
                    mImageView.setVisibility(View.GONE);

                }else {
                    mImageView.setVisibility(View.VISIBLE);
                }
                isStart = !isStart;
            }
        });

        saveBtn  = (Button) findViewById(R.id.picBtn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRenderer.saveImage();
                glSurfaceView.requestRender();
            }
        });


        initView();
    }

    private void initView() {
        sb_step = (SeekBar) findViewById(R.id.sb_step);
        sb_step.setOnSeekBarChangeListener(this);
        sb_tone = (SeekBar) findViewById(R.id.sb_tone);
        sb_tone.setOnSeekBarChangeListener(this);
        sb_beauty = (SeekBar) findViewById(R.id.sb_beauty);
        sb_beauty.setOnSeekBarChangeListener(this);
        sb_bright = (SeekBar) findViewById(R.id.sb_bright);
        sb_bright.setOnSeekBarChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        glSurfaceView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        glSurfaceView.onResume();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        int i = seekBar.getId();
        if (i == R.id.sb_step) {
            Log.e("wangshuo", "sb_step = " + range(progress, minstepoffset, maxstepoffset));
            mRenderer.setTexelOffset(range(progress, minstepoffset, maxstepoffset));

        } else if (i == R.id.sb_tone) {
            Log.e("wangshuo", "sb_tone = " + range(progress, minToneValue, maxToneValue));
            //mRenderer.setParams(range(sb_beauty.getProgress(),minbeautyValue,maxbeautyValue) , range(progress,minToneValue,maxToneValue));
            mRenderer.setToneLevel(range(progress, minToneValue, maxToneValue));

        } else if (i == R.id.sb_beauty) {
            Log.e("wangshuo", "sb_beauty = " + range(progress, minbeautyValue, maxbeautyValue));
            //mRenderer.setParams(range(progress,minbeautyValue,maxbeautyValue),range(sb_tone.getProgress(),minToneValue,maxToneValue));
            mRenderer.setBeautyLevel(range(progress, minbeautyValue, maxbeautyValue));

        } else if (i == R.id.sb_bright) {
            Log.e("wangshuo", "sb_bright = " + range(progress, minbrightValue, maxbrightValue));
            mRenderer.setBrightLevel(range(progress, minbrightValue, maxbrightValue));

        }
              glSurfaceView.requestRender();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    protected float range(final int percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }
}
