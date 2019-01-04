package com.opengles.book.es2_0_test2.muti.surface;

import android.content.Context;
import android.opengl.GLES20;

import com.opengles.book.es2_0_test2.R;
import com.opengles.book.es2_0_test2.eglUtils.EglSurfaceView;
import com.opengles.book.es2_0_test2.utils.BufferUtils;
import com.opengles.book.es2_0_test2.utils.EasyGlUtils;
import com.opengles.book.es2_0_test2.utils.ShaderUtils;

import java.nio.FloatBuffer;

/**
 * author: ycl
 * date: 2019-01-04 10:57
 * desc:
 */
public class MyRender implements EglSurfaceView.EglRenderer {
    private Context context;
    private float[] vertexData = {
            -1f, -1f,
            1f, -1f,
            -1f, 1f,
            1f, 1f,

            -0.5f, -0.5f,
            0.5f, -0.5f,
            -0.5f, 0.5f,
            0.5f, 0.5f

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
    private int textureid;
    private int sampler;

    private int umatrix;
    private float[] matrix = new float[16];

    private int vboId;
    private int fboId;

    private int imgTextureId;
    private int imgTextureId2;

    private OnRenderCreateListener onRenderCreateListener;


    public MyRender(Context context) {
        this.context = context;
        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexData);
        fragmentBuffer = BufferUtils.arr2FloatBuffer(fragmentData);
    }

    @Override
    public void onSurfaceCreated() {
        program = ShaderUtils.createProgram(ShaderUtils.readRawTextFile(context, R.raw.vertex_shader_m),
                ShaderUtils.readRawTextFile(context, R.raw.fragment_shader));

        vPosition = GLES20.glGetAttribLocation(program, "v_Position");
        fPosition = GLES20.glGetAttribLocation(program, "f_Position");
        sampler = GLES20.glGetUniformLocation(program, "sTexture");
        umatrix = GLES20.glGetUniformLocation(program, "u_Matrix");

        vboId = EasyGlUtils.getVboId(vertexData, fragmentData, vertexBuffer, fragmentBuffer);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {

    }

    @Override
    public void onDrawFrame() {

    }


    public void setOnRenderCreateListener(OnRenderCreateListener onRenderCreateListener) {
        this.onRenderCreateListener = onRenderCreateListener;
    }

    public interface OnRenderCreateListener {
        void onCreate(int textureId);
    }
}
