package com.opengles.book.es2_0.vr_video;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.opengles.book.es2_0.R;
import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.EasyGlUtils;
import com.opengles.book.es2_0.utils.MatrixHelper;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-12-03 22:08
 * desc:
 */
public class VRVideoRenderer implements GLSurfaceView.Renderer,
        SurfaceTexture.OnFrameAvailableListener,
        MediaPlayer.OnVideoSizeChangedListener {
    private String vertexShaderCodes =
            "uniform mat4 projectMatrix;" +
                    "uniform mat4 viewMatrix;" +
                    "uniform mat4 mvpMatrix;" +
                    "uniform mat4 rotateMatrix;" +
                    "uniform mat4 uSTMatrix;" + // st纹理坐标矩阵
                    "attribute vec3 vPosition;" +
                    "attribute vec4 vCoordinate;" +
                    "varying vec2 aCoordinate;" +
                    "void main(){" +
                    "gl_Position=projectMatrix*rotateMatrix*viewMatrix*mvpMatrix*vec4(vPosition,1);" +// 注意此处是rotateMatrix 在view之前，目的是相机的变换
                    "aCoordinate=(uSTMatrix * vCoordinate).xy;" +
                    "}";
    private String fragmentShaderCodes =
            "#extension GL_OES_EGL_image_external:require\n" +   // 注意此处必须加上\n换行
                    "precision mediump float;" +
                    "uniform samplerExternalOES  uTexture;" +
                    "varying vec2 aCoordinate;" +
                    "void main(){" +
                    "gl_FragColor=texture2D(uTexture,aCoordinate);" +
                    "}";
    private Context mContext;
    private int mHProgram;
    private int mHProjMatrix;
    private int mHViewMatrix;
    private int mHModelMatrix;
    private int mHRotateMatrix;
    private int mHSTMatrix;
    private int mHUTexture;
    private int mHPosition;
    private int mHCoordinate;

    private int textureId;


    private float[] mViewMatrix = new float[16];
    private float[] mProjectMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];
    private float[] mRotateMatrix = new float[16];
    private float[] mSTMatrix = new float[16];

    private FloatBuffer posBuffer;
    private FloatBuffer cooBuffer;
    private int count;

    private SurfaceTexture surfaceTexture;
    private MediaPlayer mediaPlayer;

    private boolean updateSurface;
    private boolean playerPrepared;
    private int screenWidth, screenHeight;


    public VRVideoRenderer(Context context) {
        this.mContext = context;
        playerPrepared = false;
        synchronized (this) {
            updateSurface = false;
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(true);
        mediaPlayer.setOnVideoSizeChangedListener(this);
    }

    public void setPath(String videoPath) {
        try {
            mediaPlayer.setDataSource(mContext, Uri.parse(videoPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!playerPrepared) {
            try {
                mediaPlayer.prepare();
                playerPrepared = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.start();
            playerPrepared = true;
        }
    }

    public void destroy() {
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        mHProgram = ShaderUtils.createProgram(vertexShaderCodes, fragmentShaderCodes);
        mHProjMatrix = GLES20.glGetUniformLocation(mHProgram, "projectMatrix");
        mHViewMatrix = GLES20.glGetUniformLocation(mHProgram, "viewMatrix");
        mHModelMatrix = GLES20.glGetUniformLocation(mHProgram, "mvpMatrix");
        mHRotateMatrix = GLES20.glGetUniformLocation(mHProgram, "rotateMatrix");
        mHSTMatrix = GLES20.glGetUniformLocation(mHProgram, "uSTMatrix");
        mHUTexture = GLES20.glGetUniformLocation(mHProgram, "uTexture");
        mHPosition = GLES20.glGetAttribLocation(mHProgram, "vPosition");
        mHCoordinate = GLES20.glGetAttribLocation(mHProgram, "vCoordinate");

        calculatingBall();

        textureId = createTextureId();
        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);

        Surface surface = new Surface(surfaceTexture);
        mediaPlayer.setSurface(surface);
        surface.release();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenWidth = width;
        screenHeight = height;


        float ratio = (float) width / height;
        MatrixHelper.perspectiveM(mProjectMatrix, 0, 90, ratio, 1, 500);

        // 注意设置相机在球心，才能在球心观看角度
        //第3-5个参数为相机位置，第6-8个参数为相机视线方向，第9-11个参数为相机的up方向
        Matrix.setLookAtM(mViewMatrix, 0,
                0, 0, 0,    //  0,0,0 就是在物体的中心
                0, 0, -1, // 镜头朝向
                0, -1, 0);   // 眼球向上的向量
        // 设置模型矩阵
        Matrix.setIdentityM(mMVPMatrix, 0);

        // x轴旋转180度
        Matrix.rotateM(mMVPMatrix, 0, 180, 1, 0, 0);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //        GLES20.glClearColor(1, 1, 1, 1); // 每次绘制之前都须清除
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        synchronized (this) {
            if (updateSurface) {
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }

        GLES20.glUseProgram(mHProgram);
        GLES20.glUniformMatrix4fv(mHProjMatrix, 1, false, mProjectMatrix, 0);
        GLES20.glUniformMatrix4fv(mHViewMatrix, 1, false, mViewMatrix, 0);
        GLES20.glUniformMatrix4fv(mHModelMatrix, 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(mHRotateMatrix, 1, false, mRotateMatrix, 0);
        GLES20.glUniformMatrix4fv(mHSTMatrix, 1, false, mSTMatrix, 0);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0); // 激活0通道
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        GLES20.glUniform1i(mHUTexture, 0);

        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 3, GLES20.GL_FLOAT, false, 0, posBuffer);
        GLES20.glEnableVertexAttribArray(mHCoordinate);
        GLES20.glVertexAttribPointer(mHCoordinate, 2, GLES20.GL_FLOAT, false, 0, cooBuffer);


        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, count); // 绘制线段

        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHCoordinate);
    }

    // 传感器传入的矩阵
    public void setMatrix(float[] matrix) {
        // copy一个数组
        System.arraycopy(matrix, 0, mRotateMatrix, 0, 16);
    }


    private void calculatingBall() {
        float radius = 2f;
        double angleSpan = Math.PI / 90f;
        ArrayList<Float> alVertix = new ArrayList<>();
        ArrayList<Float> textureVertix = new ArrayList<>(); // 纹理集合
        for (double angle = 0; angle < Math.PI; angle += angleSpan) {
            for (double hAngle = 0; hAngle < Math.PI * 2; hAngle += angleSpan) {
                float x0 = (float) (radius * Math.sin(angle) * Math.cos(hAngle));
                float y0 = (float) (radius * Math.sin(angle) * Math.sin(hAngle));
                float z0 = (float) (radius * Math.cos(angle));

                float x1 = (float) (radius * Math.sin(angle) * Math.cos(hAngle + angleSpan));
                float y1 = (float) (radius * Math.sin(angle) * Math.sin(hAngle + angleSpan));
                float z1 = (float) (radius * Math.cos(angle));

                float x2 = (float) (radius * Math.sin(angle + angleSpan) * Math.cos(hAngle + angleSpan));
                float y2 = (float) (radius * Math.sin(angle + angleSpan) * Math.sin(hAngle + angleSpan));
                float z2 = (float) (radius * Math.cos(angle + angleSpan));

                float x3 = (float) (radius * Math.sin(angle + angleSpan) * Math.cos(hAngle));
                float y3 = (float) (radius * Math.sin(angle + angleSpan) * Math.sin(hAngle));
                float z3 = (float) (radius * Math.cos(angle + angleSpan));

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x0);
                alVertix.add(y0);
                alVertix.add(z0);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);

                // 因为纹理在0-1之间，所以此处hAngle应该是0-2pi，angle是0-pi,3维变st2维坐标
                // s代表是圆的一个平面，t代表是经的一个平面
                float s0 = (float) (hAngle / Math.PI / 2);
                float s1 = (float) ((hAngle + angleSpan) / Math.PI / 2);

                float t0 = (float) (angle / Math.PI);
                float t1 = (float) ((angle + angleSpan) / Math.PI);


                // 103 --------  1->s1，t0  ;   0 -> s0,t0 ;  3 -> s0,t1
                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x0 y0对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);

                alVertix.add(x1);
                alVertix.add(y1);
                alVertix.add(z1);
                alVertix.add(x3);
                alVertix.add(y3);
                alVertix.add(z3);
                alVertix.add(x2);
                alVertix.add(y2);
                alVertix.add(z2);

                // 132 ------ 1->s1，t0  ;   3 -> s0,t1 ;	 0 -> s1,t1 ;
                textureVertix.add(s1);// x1 y1对应纹理坐标
                textureVertix.add(t0);
                textureVertix.add(s0);// x3 y3对应纹理坐标
                textureVertix.add(t1);
                textureVertix.add(s1);// x2 y3对应纹理坐标
                textureVertix.add(t1);
            }
        }
        count = alVertix.size() / 3;
        posBuffer = BufferUtils.list2FloatBuffer(alVertix);
        cooBuffer = BufferUtils.list2FloatBuffer(textureVertix);
    }

    private int createTextureId() {
        int[] texture = new int[1];
        GLES20.glGenTextures(1, texture, 0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture[0]);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST); // GL_NEAREST
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        return texture[0];
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }
}
