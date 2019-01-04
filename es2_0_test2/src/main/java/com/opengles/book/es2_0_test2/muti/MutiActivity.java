package com.opengles.book.es2_0_test2.muti;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.muti.surface.MyGLSurfaceView;
import com.opengles.book.es2_0_test2.muti.surface.MyRender;
import com.opengles.book.es2_0_test2.muti.surface2.MutiSurfaceView;

public class MutiActivity extends AppCompatActivity {

    private MyGLSurfaceView mMyglsurfaceviw;
    private LinearLayout mMyContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muti);
        initView();
        initData();
    }

    private void initData() {
        mMyglsurfaceviw.setOnRenderCreateListener(new MyRender.OnRenderCreateListener() {
            @Override
            public void onCreate(final int textureId) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mMyContent.getChildCount() > 0) {
                            mMyContent.removeAllViews();
                        }
                        for(int i = 0; i <3; i ++)
                        {
                            MutiSurfaceView mutiSurfaceView = new MutiSurfaceView(MutiActivity.this);
                            mutiSurfaceView.setTextureId(textureId, i);
                            mutiSurfaceView.setSurfaceAndEglContext(null, mMyglsurfaceviw.getEglContext()); //传入Context 共享GL环境

                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                            lp.width = 200;
                            lp.height = 300;
                            mutiSurfaceView.setLayoutParams(lp);

                            mMyContent.addView(mutiSurfaceView);
                        }

                    }
                });
            }
        });
    }

    private void initView() {
        mMyglsurfaceviw = (MyGLSurfaceView) findViewById(R.id.myglsurfaceviw);
        mMyContent = (LinearLayout) findViewById(R.id.my_content);
    }
}
