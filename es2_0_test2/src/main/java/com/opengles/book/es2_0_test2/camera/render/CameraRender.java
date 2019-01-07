package com.opengles.book.es2_0_test2.camera.render;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.DisplayUtil;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;
import com.opengles.book.es2_0_test2.utils.TextureUtils;

import java.nio.FloatBuffer;

/**
 * author:  ycl
 * date:  2019/1/7 9:49
 * desc:  这边的顶点，像素数据存储的是相机fbo里面的数据，所以需要有vertexData，fragmentData
 * 真实绘制的数据是在 CameraTextureRender 里面绘制的
 */
public class CameraRender implements EglSurfaceView.EglRenderer {

    private Context context;

    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

    };
    private FloatBuffer vertexBuffer;

    private float[] fragmentData = {
            0f, 1f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };
    private FloatBuffer fragmentBuffer;

    private int program;
    private int vPosition;
    private int fPosition;
    private int vboId;
    private int fboId;

    private int fboTextureid; //  GL_TEXTURE_2D
    private int cameraTextureid;  // GL_TEXTURE_EXTERNAL_OES 相机oes

    private int umatrix;
    private float[] matrix = new float[16];

    private SurfaceTexture surfaceTexture;
    private OnSurfaceCreateListener onSurfaceCreateListener;

    private CameraTextureRender mTextureRender;


    private int screenWidth;
    private int screenHeight;

    private int width;
    private int height;

    public CameraRender(Context context) {
        this.context = context;
        screenWidth = DisplayUtil.getScreenWidth(context);
        screenHeight = DisplayUtil.getScreenHeight(context);

        mTextureRender = new CameraTextureRender(context);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }

    @Override
    public void onSurfaceCreated() {
        mTextureRender.onCreate();

        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader_m),
                ShaderUtils.readRawTextFile(context, R.raw.fragment_shader_oes));

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);
        // 缓冲区
        fboTextureid = TextureUtils.genTexturesWithParameter(1, 0, GLES20.GL_RGBA, 720, 1280)[0];
        fboId = EasyGlUtils.getFboId(fboTextureid);

        // 默认是激活了一个通道，绘制图片即可
        /*  GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glUniform1i(sampler, 0);*/

        // oes
        cameraTextureid = TextureUtils.genOESTexturesWithParameter(0);
        surfaceTexture = new SurfaceTexture(cameraTextureid);
        // 放出去的都是缓冲区的textureId , 相机预览需要 setPreviewTexture surfaceTexture
        if (onSurfaceCreateListener != null) {
            onSurfaceCreateListener.onSurfaceCreate(surfaceTexture);
        }
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 其实在下面方法中不需要：
     *      GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureid);
     *      GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
     *  因为 updateTexImage 就已经把相机获取的数据直接绘制到纹理去了
     */
    @Override
    public void onDrawFrame() {
        surfaceTexture.updateTexImage();
//        surfaceTexture.getTransformMatrix(matrix);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glClearColor(1f, 0f, 0f, 1f);

        GLES20.glUseProgram(program);

        // 绘制当前界面，仅仅是绘制oes ，并且绘制oes到缓冲区fbo
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        GLES20.glUniformMatrix4fv(umatrix, 1, false, matrix, 0);

        // 开启fbo ,vbo  , 吧相机绘制的内容填充到fbo里面去
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, fboId);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vboId);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, cameraTextureid);
        GLES20.glEnableVertexAttribArray(vPosition);
        GLES20.glVertexAttribPointer(vPosition, 2, GLES20.GL_FLOAT, false, 8,
                0);
        GLES20.glEnableVertexAttribArray(fPosition);
        GLES20.glVertexAttribPointer(fPosition, 2, GLES20.GL_FLOAT, false, 8,
                vertexData.length * 4);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // 释放资源
        GLES20.glDisableVertexAttribArray(vPosition);
        GLES20.glDisableVertexAttribArray(fPosition);

        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        // 绘制缓冲区fbo的texture
        mTextureRender.onChange(width, height);
        mTextureRender.onDraw(fboTextureid);
    }

    // 重置
    public void resetMatrix() {
        Matrix.setIdentityM(matrix, 0);
    }

    // 旋转
    public void setAngle(float angle, float x, float y, float z) {
        Matrix.rotateM(matrix, 0, angle, x, y, z);
    }

    public int getFboTextureId() {
        return fboTextureid;
    }


    public void setOnSurfaceCreateListener(OnSurfaceCreateListener onSurfaceCreateListener) {
        this.onSurfaceCreateListener = onSurfaceCreateListener;
    }

    public interface OnSurfaceCreateListener {
        void onSurfaceCreate(SurfaceTexture surfaceTexture);
    }
}
