package com.opengles.book.es2_0.filter.obj;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.util.Log;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.obj.bean.Obj3D;
import com.opengles.book.es2_0.utils.EasyGlUtils;

import java.io.IOException;

/**
 * author:  ycl
 * date:  2018/12/6 11:55
 * desc:
 */
public class ObjFilter2 extends AFilter {
    private int mHNormal;
    private int mHKa;
    private int mHKd;
    private int mHKs;

    private int textureId;

    private Obj3D obj;

    public ObjFilter2(Resources mRes) {
        super(mRes);
    }

    public void setObj3D(Obj3D obj) {
        this.obj = obj;
    }

    @Override
    protected void onCreate() {
        // 开启深度检测
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        createProgramByAssetsFile("3dres/obj2.vert", "3dres/obj2.frag");
        mHNormal = GLES20.glGetAttribLocation(mProgram, "vNormal");
        mHKa = GLES20.glGetUniformLocation(mProgram, "vKa");
        mHKd = GLES20.glGetUniformLocation(mProgram, "vKd");
        mHKs = GLES20.glGetUniformLocation(mProgram, "vKs");


        if (obj != null && obj.mtl != null) {
            Log.d("onCreate:", " map_Kd:" + obj.mtl.map_Kd);
            try {
                textureId = EasyGlUtils.genTexturesWithParameter(1, 0,
                        BitmapFactory.decodeStream(mRes.getAssets().open("3dres/" + obj.mtl.map_Kd)))[0];
                setTextureId(textureId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 异常原因，控制不要清除纹理就不会美颜脸显示出来了
    @Override
    protected void onClear() {
//        super.onClear();// 控制不要清除纹理
    }



    @Override
    public void onSizeChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }
    @Override
    protected void onSetExpandData() {
        super.onSetExpandData();
        if(obj!=null&&obj.mtl!=null){
            GLES20.glUniform3fv(mHKa,1,obj.mtl.Ka,0);
            GLES20.glUniform3fv(mHKd,1,obj.mtl.Kd,0);
            GLES20.glUniform3fv(mHKs,1,obj.mtl.Ks,0);
        }
    }
    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 3, GLES20.GL_FLOAT, false, 0, obj.vert);

        GLES20.glEnableVertexAttribArray(mHNormal);
        GLES20.glVertexAttribPointer(mHNormal, 3, GLES20.GL_FLOAT, false, 0, obj.vertNorl);
//
        GLES20.glEnableVertexAttribArray(mHCoord);
        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, obj.vertTexture);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.vertCount);// 此处应该是绘制的三角形
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHNormal);
        GLES20.glDisableVertexAttribArray(mHCoord);
    }
}
