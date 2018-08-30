package com.example.gl.opengles_demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gl.opengles_demo.day01.PointActivity;
import com.example.gl.opengles_demo.day01.RendererActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static {
        System.loadLibrary("native-lib");
    }

    private TextView mTvTitle;
    private Button mButton0;
    private Button mButton1;

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

        mButton0.setOnClickListener(this);
        mButton1.setOnClickListener(this);
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
        }
    }
}
