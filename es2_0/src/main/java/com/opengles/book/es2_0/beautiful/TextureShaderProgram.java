package com.opengles.book.es2_0.beautiful;

import android.content.Context;
import android.opengl.GLES20;

import com.opengles.book.es2_0.R;


/**
 * Created by WangShuo on 2016/7/5.
 */
public class TextureShaderProgram extends ShaderProgram {

  protected static final String U_MATRIX = "vMatrix";
  protected static final String U_TEXTURE_UNIT = "vTexture";

  protected static final String A_POSITION = "vPosition";
  protected static final String A_COLOR = "a_Color";
  protected static final String A_TEXTURE_COORDINATES = "vCoordinate";


  private final int uMatrixLocation;
  private final int uTextureUnitLocation;

  private final int aPositionLocation;
  private final int aTextureCoordnatesLocation;

  public TextureShaderProgram(Context context) {
    super(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader);
    uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
    uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

    aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    aTextureCoordnatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
  }

  public TextureShaderProgram(Context context,int vertex_shader ,int fragment_shader ) {
    super(context, vertex_shader, fragment_shader);
    uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
    uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

    aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    aTextureCoordnatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
  }

  public TextureShaderProgram(String vertex_shader ,String fragment_shader ) {
    super(vertex_shader, fragment_shader);
    uMatrixLocation = GLES20.glGetUniformLocation(program, U_MATRIX);
    uTextureUnitLocation = GLES20.glGetUniformLocation(program, U_TEXTURE_UNIT);

    aPositionLocation = GLES20.glGetAttribLocation(program, A_POSITION);
    aTextureCoordnatesLocation = GLES20.glGetAttribLocation(program, A_TEXTURE_COORDINATES);
  }

  @Override public void useProgram() {
    super.useProgram();
  }

  public void setUniforms(float[] matrix, int textureId) {
    GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0);//传递矩阵给它的 uniform
    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);//把活动的纹理单元设置为纹理单元 0
    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);//把纹理绑定到这个单元
    GLES20.glUniform1i(uTextureUnitLocation, 0);//把被选定的纹理单元传递给片段着色器中的 u_TextureUnit 。
  }

  public int getPositionAttributeLocation() {
    return aPositionLocation;
  }

  public int getTextureCoordinatesAttributeLocation() {
    return aTextureCoordnatesLocation;
  }
}
