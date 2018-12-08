package com.opengles.book.es2_0.beautiful;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by WangShuo on 2016/7/5.
 */
public class VertexArray {
  private final FloatBuffer floatBuffer;
//封装储存顶点矩阵数据
  public VertexArray(float[] vertexData) {
    floatBuffer = ByteBuffer.allocateDirect(vertexData.length *4)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
        .put(vertexData);
  }
//着色器属性与数据关联的方法
  public void setVertexAttribPointer(int dataOffset , int attributeLocation,int componentCount,int stride){
    floatBuffer.position(dataOffset);
    GLES20.glVertexAttribPointer(attributeLocation,componentCount,GLES20.GL_FLOAT,false,stride,floatBuffer);
    GLES20.glEnableVertexAttribArray(attributeLocation);
    floatBuffer.position(0);
  }


}
