package com.opengles.book.es2_0.beautiful;

import android.opengl.GLES20;



/**
 * Created by WangShuo on 2016/7/5.
 */
public class Picture {
  private static final int POSITION_COMPONENT_COUNT = 2;
  private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
  private static final int STRIDE =
      (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * 4;

  private static final float[] VERTEX_DATA = { //X Y S T

          -1.0f,1.0f,0.0f,0.0f,
          -1.0f,-1.0f,0.0f,1.0f,
          1.0f,1.0f,1.0f,0.0f,
          1.0f,-1.0f,1.0f,1.0f
  };

  private final VertexArray vertexArray;

  public Picture() {
    //构造函数 把数据复制到内存
    vertexArray = new VertexArray(VERTEX_DATA);
  }
  //从内存中读出绑定数据到着色器
  public void bindData(TextureShaderProgram textureProgram) {
    //从着色器程序获取每个属性的位置。
    vertexArray.setVertexAttribPointer(0, textureProgram.getPositionAttributeLocation(),
        POSITION_COMPONENT_COUNT, STRIDE);
//把位置数据绑定到被引用的着色器属性上
    vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
        textureProgram.getTextureCoordinatesAttributeLocation(),//把纹理坐标数据绑定到被引用的着色器属性上
        TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
  }

  public void draw() {
    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP,0,4);
  }
}
