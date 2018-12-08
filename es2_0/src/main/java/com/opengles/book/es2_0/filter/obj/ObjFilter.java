package com.opengles.book.es2_0.filter.obj;

import android.content.res.Resources;
import android.opengl.GLES20;

import com.opengles.book.es2_0.filter.AFilter;
import com.opengles.book.es2_0.obj.bean.Obj3D;

/**
 * author:  ycl
 * date:  2018/12/6 11:55
 * desc:
 */
public class ObjFilter extends AFilter {
    private int mHNormal;

    private int textureId;  // 不设置texture 就相当于未设置数据到纹理去，因为本来就是获取点花直线，跟纹理无关系

    private Obj3D obj;

    public ObjFilter(Resources mRes) {
        super(mRes);
    }

    public void setObj3D(Obj3D obj) {
        this.obj = obj;
    }

    @Override
    protected void onCreate() {
        // 开启深度检测
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        createProgramByAssetsFile("3dres/obj.vert", "3dres/obj.frag");
        mHNormal = GLES20.glGetAttribLocation(mProgram, "vNormal");

        // 此处代码不会执行 因为obj.mtl==null
       /* if (obj!=null&&obj.mtl != null) {
            try {
                textureId = EasyGlUtils.genTexturesWithParameter(1, 0,
                        BitmapFactory.decodeStream(mRes.getAssets().open("3dres/" + obj.mtl.map_Kd)))[0];
                setTextureId(textureId);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    public void onSizeChanged(int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    protected void onDraw() {
        GLES20.glEnableVertexAttribArray(mHPosition);
        GLES20.glVertexAttribPointer(mHPosition, 3, GLES20.GL_FLOAT, false, 0, obj.vert);

        GLES20.glEnableVertexAttribArray(mHNormal);
        GLES20.glVertexAttribPointer(mHNormal, 3, GLES20.GL_FLOAT, false, 0, obj.vertNorl);
//
        // 因为未用到  mHCoord 参数，所有此处不需要赋值即可
//        GLES20.glEnableVertexAttribArray(mHCoord);
//        GLES20.glVertexAttribPointer(mHCoord, 2, GLES20.GL_FLOAT, false, 0, mTexBuffer);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, obj.vertCount);// 此处应该是绘制的三角形
        GLES20.glDisableVertexAttribArray(mHPosition);
        GLES20.glDisableVertexAttribArray(mHNormal);
//        GLES20.glDisableVertexAttribArray(mHCoord);
    }
}
