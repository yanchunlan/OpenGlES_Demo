package com.opengles.book.es2_0.camera;

import android.content.Context;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import com.opengles.book.es2_0.camera.camera.KitkatCamera;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-10 15:34
 * desc:  此类只是一个壳，代理了renderer，真是的renderer是CameraRenderer
 * <p>
 * 原理就是camera获取到得数据通过setPreviewTexture 设置给 surfaceTexture
 * （surfaceTexture是专门针对openGlES的预览的类，其创建也是需要createTextureId 相关联）
 * -> surfaceTexture再不断setOnFrameAvailableListener监听，
 * 调用requestRender，去渲染openGl得方法并 surfaceTexture.updateTexImage()，
 * （updateTexImage()方法会将ImageStream的图片数据更新到GL_OES_EGL_image_external类型的纹理中，
 * 每当使用该类纹理对纹理对象进行绑定时，需使用GL_TEXTURE_EXTERNAL_OES）
 *      glSurfaceView->camera->glTexture->texture
 * <p>
 * <p>
 * 目的是预览作用：疑惑如下：
 * 1.区别与原生得是什么？？？
 * 2.相机矩阵变换怎么变得原理？
 * 3.
 */
public class CameraGlSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {
    private KitkatCamera mCamera;
    private CameraRenderer mRenderer;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private Runnable mRunnable;

    public CameraGlSurfaceView(Context context) {
        this(context, null);
    }

    public CameraGlSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setEGLContextClientVersion(2);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        mCamera = new KitkatCamera();
        mRenderer = new CameraRenderer(getResources());
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        mRenderer.onSurfaceCreated(gl, config);
        if (mRunnable != null) {
            mRunnable.run();
            mRunnable = null;
        }

        mCamera.open(cameraId);
        mRenderer.setCameraId(cameraId);

        Point point = mCamera.getPreviewSize();
        // point.x = height , point.y = width
        mRenderer.setDataSize(point.x, point.y);

        mCamera.setPreviewTexture(mRenderer.getSurfaceTexture());
        // Texture 得监听方法 ，监听到了之后就传给绘制层去绘制
        mRenderer.getSurfaceTexture().setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
            @Override
            public void onFrameAvailable(SurfaceTexture surfaceTexture) {
                requestRender();
            }
        });

        // 开始预览
        mCamera.preview();
    }

    // 不知道什么时候执行runable得方法
    public void switchCamera() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                mCamera.close();
                cameraId = cameraId == 1 ? 0 : 1;
            }
        };
        onPause();
        onResume();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mRenderer.setViewSize(width, height);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        mRenderer.onDrawFrame(gl);
    }

    @Override
    public void onPause() {
        super.onPause();
        mCamera.close();
    }
}
