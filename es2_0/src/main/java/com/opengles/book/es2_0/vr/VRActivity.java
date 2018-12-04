package com.opengles.book.es2_0.vr;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

public class VRActivity extends AppCompatActivity implements SensorEventListener {
    private static final String TAG = "VRActivity";

    private GLSurfaceView mGLSurfaceView;
    private VRRenderer mRenderer;

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private float[] mMatrix = new float[16];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGLSurfaceView = new GLSurfaceView(this);
        setContentView(mGLSurfaceView);


        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion >= 0x30000) {
            Log.d(TAG, "onCreate: enable 3.0");
        }

        mRenderer = new VRRenderer(this);
        mGLSurfaceView.setEGLContextClientVersion(2);
        mGLSurfaceView.setRenderer(mRenderer);
        mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR); // 旋转传感器
        List<Sensor> sensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);
        if (mSensor != null && sensorList.contains(mSensor)) {
            Log.d(TAG, "onCreate: has TYPE_ROTATION_VECTOR");
        } else {
            Log.d(TAG, "onCreate: hasNot TYPE_ROTATION_VECTOR");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGLSurfaceView.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();
        mSensorManager.unregisterListener(this, mSensor);
    }

    // -------------- 传感器监听 ------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorManager.getRotationMatrixFromVector(mMatrix, event.values);
        mRenderer.setMatrix(mMatrix);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
