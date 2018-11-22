precision mediump float;
varying vec2 textureCoordinate;
uniform sampler2D vTexture;
void main() {
    vec4 color=texture2D( vTexture, textureCoordinate);
    float rgb=color.g;
    vec4 c=vec4(rgb,rgb,rgb,color.a);
    gl_FragColor = c; // 用一个颜色g,来代表color的 r g b,设置相机片元着色器的颜色，让其统一
}