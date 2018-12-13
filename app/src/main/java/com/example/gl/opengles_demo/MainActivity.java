package com.example.gl.opengles_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gl.opengles_demo.day01.line.LineActivity;
import com.example.gl.opengles_demo.day01.line.LineStripActivity;
import com.example.gl.opengles_demo.day01.point.PointActivity;
import com.example.gl.opengles_demo.day01.point.PointSizeActivity;
import com.example.gl.opengles_demo.day01.scissor.ScissorActivity;
import com.example.gl.opengles_demo.day01.triangle.RendererActivity;
import com.example.gl.opengles_demo.day01.triangle.TriangleActivity;
import com.example.gl.opengles_demo.day01.triangle.TriangleConeActivity;
import com.example.gl.opengles_demo.day02.StencilActivity;
import com.example.gl.opengles_demo.day02.cube.ColorCubeActivity;
import com.example.gl.opengles_demo.day02.cube.CubeActivity;
import com.example.gl.opengles_demo.day02.light.LightActivity;
import com.example.gl.opengles_demo.day02.ring.RingActivity;
import com.example.gl.opengles_demo.day02.sphere.SphereActivity;
import com.example.gl.opengles_demo.day03.antalias.AntliaActivity;
import com.example.gl.opengles_demo.day03.antalias.AntliaActivity2;
import com.example.gl.opengles_demo.day03.blend.BlendActivity;
import com.example.gl.opengles_demo.day03.fog.FogActivity;
import com.example.gl.opengles_demo.day03.light.LightReviewActivity;
import com.example.gl.opengles_demo.day03.tex.TexActivity;
import com.opengles.book.es2_0.ES2_0MainActivity;
import com.opengles.book.es2_0_test.ES2_0TestMainActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("native-lib");
    }

    private TextView mTvTitle;
    private Button mButton0;
    private Button mButton1;
    private Button mButton2;
    private Button mButton3;
    private Button mButton4;
    private Button mButton5;
    private Button mButton6;
    private Button mButton7;
    private Button mButton8;
    private Button mButton9;
    private Button mButton10;
    private Button mButton11;
    private Button mButton12;
    private Button mButton13;
    private Button mButton14;
    private Button mButton15;
    private Button mButton16;
    private Button mButton17;
    private Button mButton18;
    private Button mButton19;
    private Button mButton20;
    private Button mButton21;

    public native String stringFromJNI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(stringFromJNI());
        mButton0 = (Button) findViewById(R.id.button0);
        mButton1 = (Button) findViewById(R.id.button1);
        mButton2 = (Button) findViewById(R.id.button2);
        mButton3 = (Button) findViewById(R.id.button3);
        mButton4 = (Button) findViewById(R.id.button4);
        mButton5 = (Button) findViewById(R.id.button5);
        mButton6 = (Button) findViewById(R.id.button6);
        mButton7 = (Button) findViewById(R.id.button7);
        mButton8 = (Button) findViewById(R.id.button8);
        mButton9 = (Button) findViewById(R.id.button9);
        mButton10 = (Button) findViewById(R.id.button10);
        mButton11 = (Button) findViewById(R.id.button11);
        mButton12 = (Button) findViewById(R.id.button12);
        mButton13 = (Button) findViewById(R.id.button13);
        mButton14 = (Button) findViewById(R.id.button14);
        mButton15 = (Button) findViewById(R.id.button15);
        mButton16 = (Button) findViewById(R.id.button16);
        mButton17 = (Button) findViewById(R.id.button17);
        mButton18 = (Button) findViewById(R.id.button18);
        mButton19 = (Button) findViewById(R.id.button19);
        mButton20 = (Button) findViewById(R.id.button20);
        mButton21 = (Button) findViewById(R.id.button21);

        mButton0.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
        mButton6.setOnClickListener(this);
        mButton7.setOnClickListener(this);
        mButton8.setOnClickListener(this);
        mButton9.setOnClickListener(this);
        mButton10.setOnClickListener(this);
        mButton11.setOnClickListener(this);
        mButton12.setOnClickListener(this);
        mButton13.setOnClickListener(this);
        mButton14.setOnClickListener(this);
        mButton15.setOnClickListener(this);
        mButton16.setOnClickListener(this);
        mButton17.setOnClickListener(this);
        mButton18.setOnClickListener(this);
        mButton19.setOnClickListener(this);
        mButton20.setOnClickListener(this);
        mButton21.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button0: // 三角形
                startActivity(new Intent(this, RendererActivity.class));
                break;
            case R.id.button1: // 点
                startActivity(new Intent(this, PointActivity.class));
                break;
            case R.id.button2:// 点大
                startActivity(new Intent(this, PointSizeActivity.class));
                break;
            case R.id.button3:// 线
                startActivity(new Intent(this, LineActivity.class));
                break;
            case R.id.button4:// 线带
                startActivity(new Intent(this, LineStripActivity.class));
                break;
            case R.id.button5:// 三角形带,正方形
                startActivity(new Intent(this, TriangleActivity.class));
                break;
            case R.id.button6: // 棱锥
                startActivity(new Intent(this, TriangleConeActivity.class));
                break;
            case R.id.button7: // 剪裁
                startActivity(new Intent(this, ScissorActivity.class));
                break;
            case R.id.button8: // 模板
                startActivity(new Intent(this, StencilActivity.class));
                break;
            case R.id.button9:// 球
                startActivity(new Intent(this, SphereActivity.class));
                break;
            case R.id.button10: // 圆环
                startActivity(new Intent(this, RingActivity.class));
                break;
            case R.id.button11: // 立方体
                startActivity(new Intent(this, CubeActivity.class));
                break;
            case R.id.button12: // 颜色立方体
                startActivity(new Intent(this, ColorCubeActivity.class));
                break;
            case R.id.button13: // 光照
                startActivity(new Intent(this, LightActivity.class));
                break;
            case R.id.button14: // 光照(复习)
                startActivity(new Intent(this, LightReviewActivity.class));
                break;
            case R.id.button15: // 混合
                startActivity(new Intent(this, BlendActivity.class));
                break;
            case R.id.button16: // 抗锯齿
                startActivity(new Intent(this, AntliaActivity.class));
                break;
            case R.id.button17:// 抗锯齿2
                startActivity(new Intent(this, AntliaActivity2.class));
                break;
            case R.id.button18://雾
                startActivity(new Intent(this, FogActivity.class));
                break;
            case R.id.button19:// 纹理
                startActivity(new Intent(this, TexActivity.class));
                break;
            case R.id.button20: // es 2.0 huGuangWuWang
                startActivity(new Intent(this, ES2_0MainActivity.class));
                break;
            case R.id.button21:// es 2.0
                startActivity(new Intent(this, ES2_0TestMainActivity.class));
                break;
        }
    }
}
