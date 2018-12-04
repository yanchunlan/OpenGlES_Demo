package com.opengles.book.es2_0.beautiful;

import android.content.Context;
import android.opengl.GLES20;

import com.opengles.book.es2_0.utils.ShaderUtils;

public class ShaderProgram {


  protected final int program;

  protected ShaderProgram(Context context, int vertexShaderResourceId,
      int fragmentShaderResourceId) {
    program = ShaderUtils.createProgram(//
        ShaderUtils.readRawTextFile(context, vertexShaderResourceId),
            ShaderUtils.readRawTextFile(context, fragmentShaderResourceId));
  }

  protected ShaderProgram(String vertexShaderResource,
                          String fragmentShaderResource) {
    program = ShaderUtils.createProgram(vertexShaderResource,fragmentShaderResource);
  }

  public void useProgram() {
    GLES20.glUseProgram(program);
  }

  public int getProgram() {
   return program;
  }

}
