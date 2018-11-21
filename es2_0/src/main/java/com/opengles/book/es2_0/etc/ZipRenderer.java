package com.opengles.book.es2_0.etc;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.ETC1;
import android.opengl.ETC1Util;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.opengles.book.es2_0.utils.BufferUtils;
import com.opengles.book.es2_0.utils.MatrixUtils;
import com.opengles.book.es2_0.utils.ShaderUtils;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * author: ycl
 * date: 2018-11-12 22:22
 * desc:
 *       异常2个：
 *          1> glGenTextures（n,） n代表的是纹理个数
 *          2> float[] coords  纹理坐标是float类型
 *          3> surface创建成功之后，才能创建着色器program
 *          4> 判断glLinkProgram 成功与否状态是 使用 GL_LINK_STATUS 来判断
 *
 */
public class ZipRenderer implements GLSurfaceView.Renderer {
    private static final String TAG = "ZipRenderer";
    private String vertexShaderCodes = "attribute vec4 vPosition;" +
            "attribute vec2 vCoord;" +
            "varying vec2 aCoord;" +
            "uniform mat4 vMatrix;" +
            "void main(){" +
            "gl_Position=vMatrix*vPosition;" +
            "aCoord=vCoord;" +
            "}";
    private String fragmentShaderCodes = "precision mediump float;" +
            "varying vec2 aCoord;" +
            "uniform sampler2D vTexture;" +
            "uniform sampler2D vTextureAlpha;" +
            "void main(){" +
            "vec4 color=texture2D(vTexture,aCoord);" +
            "color.a=texture2D(vTextureAlpha,aCoord).r;" + // 透明通道的r 赋值给color.a
            "gl_FragColor=color;" +
            "}";
    private float[] vertexCoords = {
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            1f, -1f
    };
    // ######  纹理需要的是float类型不是short类型 ######
    private float[] coords = {
            0, 0,
            0, 1,
            1, 0,
            1, 1
    };
    /*private float[] colors = {};   不需要color ,因为颜色是从 simple2D的texture2D生成*/
    private FloatBuffer vertexBuffer;
    private FloatBuffer coordBuffer;

    public static final int TYPE = 0x01;
    private StateChangeListener mChangeListener;

    private int program;
    private int glPosition;
    private int glMatrix;
    private int glCoords;
    private int glTexture;
    private int glTextureAlpha;

    // params
    private ZipPkmReader mPkmReader;
    private int[] texture;

    private boolean isPlay = false;
    private GLSurfaceView mView;
    private int timeStep = 50; // 延时
    private long time = 0;
    private float[] SM = MatrixUtils.getOriginalMatrix(); // 单位矩阵
    private int type = MatrixUtils.TYPE_CENTERINSIDE;
    private ByteBuffer emptyBuffer;
    private int width, height;


    public static final float[] OM= MatrixUtils.getOriginalMatrix();
    private float[] mvpMatrix = Arrays.copyOf(OM, 16);

    public ZipRenderer(Context context) {
        mPkmReader = new ZipPkmReader(context);

        vertexBuffer = BufferUtils.arr2FloatBuffer(vertexCoords);
        coordBuffer = BufferUtils.arr2FloatBuffer(coords);
    }


    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0, 0, 0, 0);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        GLES20.glEnable(GLES20.GL_TEXTURE_2D);

        program = createProgram(vertexShaderCodes, fragmentShaderCodes);

        glPosition = GLES20.glGetAttribLocation(program, "vPosition");
        glCoords = GLES20.glGetAttribLocation(program, "vCoord");
        glMatrix = GLES20.glGetUniformLocation(program, "vMatrix");
        glTexture = GLES20.glGetUniformLocation(program, "vTexture");
        glTextureAlpha = GLES20.glGetUniformLocation(program, "vTextureAlpha");

        texture = new int[2];
        createTextureId(texture);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        // 因为不同的宽高，创建ETC1Texture 需要的buffer不同，所有在此处设置
        this.emptyBuffer = ByteBuffer.allocateDirect(ETC1.getEncodedDataSize(width, height));
        this.width = width;
        this.height = height;
        // 开启混合
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
       /* GLES20.glViewport(0, 0, width, height);
        int w = mBitmap.getWidth();
        int h = mBitmap.getHeight();
        float sWH = w / (float) h;
        float sWidthHeight = width / (float) height;
        if (width > height) {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0, -sWH * sWidthHeight, sWH * sWidthHeight,
                        -1, 1, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0, -sWidthHeight / sWH, sWidthHeight / sWH,
                        -1, 1, 3, 7);
            }
        } else {
            if (sWH > sWidthHeight) {
                Matrix.orthoM(projectMatrix, 0,
                        -1, 1, -1 / sWidthHeight * sWH, 1 / sWidthHeight * sWH, 3, 7);
            } else {
                Matrix.orthoM(projectMatrix, 0,
                        -1, 1, -sWH / sWidthHeight, sWH / sWidthHeight, 3, 7);
            }
        }
        Matrix.setLookAtM(viewMatrix, 0, 0, 0, 5, 0, 0, 0, 0, 1, 0);
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, viewMatrix, 0);*/
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (time != 0) {
            // 2次执行的耗时
            Log.e(TAG, "onDrawFrame: time-->" + (System.currentTimeMillis() - time));
        }
        time = System.currentTimeMillis();

        long startTime = System.currentTimeMillis();
        draw();
        long end = System.currentTimeMillis() - startTime;
        if (isPlay) {
            // 执行时间间隔在计划的时间间隔，就开启计时器，目的是要求计时始终在 timeStep 间隔内
            if (end < timeStep) {
                try {
                    Thread.sleep(timeStep - end);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 第一次创建的时候，请求重绘，再休眠时间到了之后就继续要求重绘
                mView.requestRender();
            }
        } else {
            // 绘制结束了，更改状态
            changeState(StateChangeListener.PLAYING, StateChangeListener.STOP);
        }
    }

    private void draw() {
        //图片需要每次绘制之前清空所有颜色
        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        GLES20.glUseProgram(program);

//        GLES20.glUniformMatrix4fv(glMatrix,1,false,mvpMatrix,0);
        bindTextureId();

        GLES20.glEnableVertexAttribArray(glPosition);
        GLES20.glVertexAttribPointer(glPosition, 2,// 2维坐标
                GLES20.GL_FLOAT, false,
                0, vertexBuffer);

        GLES20.glEnableVertexAttribArray(glCoords);
        GLES20.glVertexAttribPointer(glCoords, 2,// 2维坐标
                GLES20.GL_FLOAT, false,
                0, coordBuffer);

        // 绘制正方形，不是绘制纹理的坐标
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(glPosition);
        GLES20.glDisableVertexAttribArray(glCoords);
    }

    private void bindTextureId() {
        ETC1Util.ETC1Texture t = mPkmReader.getNextTexture();
        ETC1Util.ETC1Texture tAlpha = mPkmReader.getNextTexture();
        if (t != null && tAlpha != null) {
            // 此处因为图片转换为ETC1Texture 了，所有直接获取其宽高就代表bitmap的宽高了
            //根据不同的type设置不同的矩阵变换，显示不同的图片样式
            MatrixUtils.getMatrix(SM, type, t.getWidth(), t.getHeight(), width, height);

            mvpMatrix = SM;
            GLES20.glUniformMatrix4fv(glMatrix, 1, false, mvpMatrix, 0);

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, t);
            GLES20.glUniform1i(glTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, tAlpha);
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
        } else {
            // 如果是null，则不需要设置matrix，且赋值空对象的 ETC1Util.ETC1Texture
            mvpMatrix = OM;
            GLES20.glUniformMatrix4fv(glMatrix, 1, false, mvpMatrix, 0);

            // bind  texture
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width, height, emptyBuffer));
            GLES20.glUniform1i(glTexture, getTextureType());

            // bind  textureAlpha
            GLES20.glActiveTexture(GLES20.GL_TEXTURE1 + getTextureType()); // 设置texture0通道
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[1]);
            // etc需要的load加载
            ETC1Util.loadTexture(GLES20.GL_TEXTURE_2D, 0, 0, GLES20.GL_RGB,
                    GLES20.GL_UNSIGNED_SHORT_5_6_5, new ETC1Util.ETC1Texture(width, height, emptyBuffer));
            GLES20.glUniform1i(glTextureAlpha, 1 + getTextureType());
            isPlay = false;
        }
    }

    public final int getTextureType() {
        return 0;
    }

    // 此处创建2个，是因为一个是texture，一个是透明度的，总结就是2个
    private void createTextureId(int[] texture) {
        // 此处注意： n代表个数，target代表类型，一般是GLES20.GL_TEXTURE_2D 类型
        GLES20.glGenTextures(2, texture, 0);
        for (int i = 0; i < texture.length; i++) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[i]);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

//            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0); // 2维图形就直接GLUtils.texImage2D 设置bitmap到 里面去
        }
    }

    //  ----------------------  可封装方法 start -------------------
    // 重新写一遍，熟悉代码
    private int createProgram(String vertexShaderCodes, String fragmentShaderCodes) {
        int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCodes);
        if (vertexShader == 0) return 0;
        int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodes);
        if (fragmentShader == 0) return 0;
        int program = GLES20.glCreateProgram();
        if(program!=0){
            GLES20.glAttachShader(program, vertexShader);
            GLES20.glAttachShader(program, fragmentShader);
            GLES20.glLinkProgram(program);
            // 成功之后获取状态判断是否ok
            int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);  // 此处是 GL_LINK_STATUS
            if (linkStatus[0] != GLES20.GL_TRUE) {
                Log.e(TAG, "loadShader: " + GLES20.glGetProgramInfoLog(program));
                GLES20.glDeleteProgram(program);
                program = 0;
            }
        }
        return program;
    }

    private int loadShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        if (0 != shader) {
            GLES20.glShaderSource(shader, source);
            GLES20.glCompileShader(shader);
            // 成功之后获取状态判断是否ok
            int[] compiled = new int[1];
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compiled, 0);
            if(compiled[0]==0){
                Log.e(TAG, "loadShader: " + type);
                Log.e(TAG, "loadShader: " + GLES20.glGetShaderInfoLog(shader));
                GLES20.glDeleteShader(shader);
                shader = 0;
            }
        }
        return shader;
    }

    //  -------------------------  other start --------------------------

    public void setScaleType(int type, int... params) {
        if (type == TYPE) {
            this.type = params[0];
        }
    }

    public void setAnimation(ZipGlSurfaceView zipGlSurfaceView, String path, int timeStep) {
        this.mView = zipGlSurfaceView;
        this.timeStep = timeStep;
        this.mPkmReader.setPath(path);
    }

    public void start() {
        if (!isPlay) {
            stop();
            isPlay = true;
            // 状态改变为start
            changeState(StateChangeListener.STOP, StateChangeListener.START);
            if (mPkmReader != null) {
                mPkmReader.open();
            }
            // 请求重绘 ,就会调用图形轮训处理的代码
            mView.requestRender();
        }
    }

    public void stop() {
        if (mPkmReader != null) {
            mPkmReader.close();
        }
        isPlay = false;
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setChangeListener(StateChangeListener changeListener) {
        mChangeListener = changeListener;
    }

    private void changeState(int lastState, int nowState) {
        if (mChangeListener != null) {
            mChangeListener.onStateChanged(lastState, nowState);
        }
    }
}
