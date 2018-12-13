package com.opengles.book.es2_0.camera.camera2.camera_render;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * author: ycl
 * date: 2018-11-26 23:44
 * desc:
 *      流程：
 *          1>获取相机大小，并创建相机
 *               CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
 *             //在这里可以通过CameraCharacteristics设置配置图
 *             StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
 *             Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
 *             mPreviewSize = sizes[0];
 *             mCameraManager.openCamera（）
 *          2>设置预览surface，默认大小，并创建createCaptureSession通道
 *            onOpened(@NonNull CameraDevice device)里面执行下面方法
 *                  final CaptureRequest.Builder builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);// 模板预览
 *                  Surface surface = new Surface(mController.getTexture());
 *                  builder.addTarget(surface);
 *                  mController.getTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());
 *
 *                  device.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
 *          3>设置请求重复监听，并在监听完成调用：texture.requestRender()
 *            在onConfigured 里面加入重试请求监听
 *              session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {}，mHandler)
 *              在其监听器onCaptureCompleted 里面调用texture.requestRender()
 *
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Camera2Renderer implements GLRenderer {
    private com.opengles.book.es2_0.camera.camera2.Camera2Renderer mController;

    private final CameraManager mCameraManager;
    private final HandlerThread mThreadHandler;
    private final Handler mHandler;

    private CameraDevice mCameraDevice;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Size mPreviewSize;


    public Camera2Renderer(Context context, com.opengles.book.es2_0.camera.camera2.Camera2Renderer controller) {
        this.mController = controller;
        mCameraManager = (CameraManager) context.getSystemService(context.CAMERA_SERVICE);
        mThreadHandler = new HandlerThread("camera2");
        mThreadHandler.start();
        mHandler = new Handler(mThreadHandler.getLooper());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            if (mCameraDevice != null) {
                mCameraDevice.close();
                mCameraDevice = null;
            }

            //获取可用相机设备列表
            CameraCharacteristics c = mCameraManager.getCameraCharacteristics(cameraId + "");
            //在这里可以通过CameraCharacteristics设置配置图
            StreamConfigurationMap map = c.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Size[] sizes = map.getOutputSizes(SurfaceHolder.class);
            mPreviewSize = sizes[0];
            mController.setDataSize(mPreviewSize.getHeight(), mPreviewSize.getWidth());

            mCameraManager.openCamera(cameraId + "", new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice device) {
                    mCameraDevice = device;

                    try {
                        final CaptureRequest.Builder builder = device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);// 模板预览
                        Surface surface = new Surface(mController.getTexture());
                        builder.addTarget(surface);
                        mController.getTexture().setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

                        device.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession session) {
                                // 配置ok
                                try {
                                    session.setRepeatingRequest(builder.build(), new CameraCaptureSession.CaptureCallback() {
                                        @Override
                                        public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                            super.onCaptureCompleted(session, request, result);
                                            mController.requestRender();
                                        }
                                    }, mHandler);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession session) {
                            }
                        },mHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                    mCameraDevice = null;
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {
                }
            }, mHandler);
//            mController.setImageDirection(cameraId);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

    }

    @Override
    public void onDrawFrame(GL10 gl) {

    }
}
