package com.opengles.book.es2_0.blend;

import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.opengles.book.es2_0.R;
import com.opengles.book.es2_0.weight.WheelView;

public class BlendActivity extends AppCompatActivity {

    private GLSurfaceView mMGLView;
    private WheelView mMSrcParam;
    private WheelView mMDstParam;
    private TextView mMEqua;

    private BlendRender mRender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blend);
        initView();
        initEGL();
        initTv();
        initWheel();
    }

    private void initWheel() {
        // 滚动控件初始化
        for (int i = 0; i < mRender.getParamStr().length; i++) {
            mMSrcParam.addData(mRender.getParamStr()[i], mRender.getParamInt()[i]);
            mMDstParam.addData(mRender.getParamStr()[i], mRender.getParamInt()[i]);
        }
        mMSrcParam.setCenterItem(mRender.getnSrcPar());
        mMDstParam.setCenterItem(mRender.getnDstPar());
        mMSrcParam.setTextSize(10);
        mMDstParam.setTextSize(10);
        mMSrcParam.setOnCenterItemChangedListener(new WheelView.OnCenterItemChangedListener() {
            @Override
            public void onItemChange(Object now) {
                mRender.setnSrcPar((int) now);
            }
        });
        mMDstParam.setOnCenterItemChangedListener(new WheelView.OnCenterItemChangedListener() {
            @Override
            public void onItemChange(Object now) {
                mRender.setnDstPar((int) now);
            }
        });
    }

    private void initTv() {
        mMEqua.setText(mRender.equaStr());
        mMEqua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRender.addEquaStr();
                mMEqua.setText(mRender.equaStr());
            }
        });
    }

    private void initEGL() {
        mMGLView.setEGLContextClientVersion(2);
        mMGLView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        mMGLView.getHolder().setFormat(PixelFormat.TRANSPARENT);
        mMGLView.setZOrderOnTop(true);
        mRender = new BlendRender(this);
        mMGLView.setRenderer(mRender);
        mMGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void initView() {
        mMGLView = (GLSurfaceView) findViewById(R.id.mGLView);
        mMSrcParam = (WheelView) findViewById(R.id.mSrcParam);
        mMDstParam = (WheelView) findViewById(R.id.mDstParam);
        mMEqua = (TextView) findViewById(R.id.mEqua);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMGLView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMGLView.onPause();
    }
}
