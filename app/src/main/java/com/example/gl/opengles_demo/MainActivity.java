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
import com.example.gl.opengles_demo.day01.triangle.RendererActivity;
import com.example.gl.opengles_demo.day01.triangle.TriangleActivity;

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

        mButton0.setOnClickListener(this);
        mButton1.setOnClickListener(this);
        mButton2.setOnClickListener(this);
        mButton3.setOnClickListener(this);
        mButton4.setOnClickListener(this);
        mButton5.setOnClickListener(this);
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

        }
    }
}
