package com.opengles.book.es2_0_test2.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.opengles.book.es2_0_test2.camera.render.CameraRender;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;

/**
 * author:  ycl
 * date:  2019/1/4 17:10
 * desc:
 */
public class CameraSurfaceView extends EglSurfaceView {
    private static final String TAG = "CameraSurfaceView";
    private Camera mCamera;
    private CameraRender mCameraRender;

    private int cameraId = android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK;

    public CameraSurfaceView(Context context) {
        this(context, null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mCameraRender = new CameraRender(context);
        mCamera = new Camera(context);
        setRenderer(mCameraRender);
        setRenderMode(EglSurfaceView.RENDERMODE_WHEN_DIRTY);
        previewAngle(context);
        mCameraRender.setOnSurfaceCreateListener(new CameraRender.OnSurfaceCreateListener() {
            @Override
            public void onSurfaceCreate(SurfaceTexture surfaceTexture) {
                surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                    @Override
                    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                        requestRender();
                    }
                });
                // 在创建成功之后render之后在创建camera
                mCamera.initCamera(surfaceTexture, cameraId);
            }
        });
    }


    public void onDestroy() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    // 角度适配
    public void previewAngle(Context ctx) {
        int angle = ((WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        mCameraRender.resetMatrix();
        switch (angle)
        {
            case Surface.ROTATION_0:
                Log.d(TAG, "0");
                if(cameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                {
                    mCameraRender.setAngle(90, 0, 0, 1);
                    mCameraRender.setAngle(180, 1, 0, 0);
                }
                else
                {
                    mCameraRender.setAngle(90f, 0f, 0f, 1f);
                }

                break;
            case Surface.ROTATION_90:
                Log.d(TAG, "90");
                if(cameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                {
                    mCameraRender.setAngle(180, 0, 0, 1);
                    mCameraRender.setAngle(180, 0, 1, 0);
                }
                else
                {
                    mCameraRender.setAngle(90f, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_180:
                Log.d(TAG, "180");
                if(cameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                {
                    mCameraRender.setAngle(90f, 0.0f, 0f, 1f);
                    mCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }
                else
                {
                    mCameraRender.setAngle(-90, 0f, 0f, 1f);
                }
                break;
            case Surface.ROTATION_270:
                Log.d(TAG, "270");
                if(cameraId == android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK)
                {
                    mCameraRender.setAngle(180f, 0.0f, 1f, 0f);
                }
                else
                {
                    mCameraRender.setAngle(0f, 0f, 0f, 1f);
                }
                break;
        }
    }

    public int getTextureId() {
        if (mCameraRender != null) {
            return mCameraRender.getFboTextureId();
        }
        return -1;
    }
}
