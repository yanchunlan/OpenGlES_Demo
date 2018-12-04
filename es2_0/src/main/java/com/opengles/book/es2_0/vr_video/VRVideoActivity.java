package com.opengles.book.es2_0.vr_video;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.opengles.book.es2_0.R;

import java.util.List;

public class VRVideoActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {
    private static final String TAG = "VRActivity";

    private static final int REQUEST_CODE = 1;
    private GLSurfaceView mGLSurfaceView;
    private Button mSelectVideo;

    private VRVideoRenderer mRenderer;

    private SensorManager mSensorManager;
    private Sensor mSensor;


    private float[] mMatrix = new float[16];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vr_video);
        initView();
        initListener();
    }

    private void initView() {
        mSelectVideo = (Button) findViewById(R.id.selectVideo);
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.mGLView);

        mSelectVideo.setOnClickListener(this);
    }

    private void initListener() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = activityManager.getDeviceConfigurationInfo();
        if (info.reqGlEsVersion >= 0x30000) {
            Log.d(TAG, "onCreate: enable 3.0");
        }

        mRenderer = new VRVideoRenderer(this);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRenderer.destroy();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.selectVideo) {
            Intent it = new Intent(Intent.ACTION_GET_CONTENT);
            it.setType("video/*");//选取所有的视频类型 *有mp4、3gp、avi等
            startActivityForResult(it, REQUEST_CODE);//以识别编号来启动外部程序
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK && data != null) {
            Uri image = data.getData();
            String[] projection = {MediaStore.Video.Media.DATA};
            Cursor cursor = getContentResolver().query(image, projection,
                    null, null, null);
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(projection[0]);
            String videoPath = cursor.getString(index);
            Log.i(TAG, "onActivityResult:   --> videoPath: " + videoPath);
            mRenderer.setPath(videoPath);
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    // -------------- 传感器监听 ------------------

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mRenderer != null) {
            SensorManager.getRotationMatrixFromVector(mMatrix, event.values);
            mRenderer.setMatrix(mMatrix);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
