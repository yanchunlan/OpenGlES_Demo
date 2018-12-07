package com.opengles.book.es2_0.light;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.opengles.book.es2_0.R;

public class LightActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private GLSurfaceView mGlView;
    private LightRenderer mRenderer;
    private Switch mAmbient;
    private Switch mDiffuse;
    private Switch mSpecular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);
        initView();
        initListener();
    }

    private void initListener() {
        mAmbient.setOnCheckedChangeListener(this);
        mDiffuse.setOnCheckedChangeListener(this);
        mSpecular.setOnCheckedChangeListener(this);

        mRenderer = new LightRenderer();
        mGlView.setEGLContextClientVersion(2);
        mGlView.setRenderer(mRenderer);
        mGlView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
    }

    private void initView() {
        mGlView = (GLSurfaceView) findViewById(R.id.glView);
        mAmbient = (Switch) findViewById(R.id.ambient);
        mDiffuse = (Switch) findViewById(R.id.diffuse);
        mSpecular = (Switch) findViewById(R.id.specular);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGlView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGlView.onPause();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int i = buttonView.getId();
        if (i == R.id.ambient) {
            mRenderer.setAmbientStrength(isChecked);
        } else if (i == R.id.diffuse) {
            mRenderer.setDiffuseStrength(isChecked);
        } else if (i == R.id.specular) {
            mRenderer.setSpecularStrength(isChecked);
        }
    }
}
