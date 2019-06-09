package com.lbh.lesson.gles.lesson3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.lbh.lesson.gles.R;

import java.util.Random;

public class CoordinateSystemsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        final GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
        // 创建OpenGL ES 2.0的上下文
        glSurfaceView.setEGLContextClientVersion(2);
        //设置Renderer用于绘图
        final CoordinateSystemsRender render = new CoordinateSystemsRender(this);
        glSurfaceView.setRenderer(render);
        //只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
        //该种模式下当需要重绘时需要我们手动调用glSurfaceView.requestRender();
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        final float[] rotation = new float[10];
        final long startTime = System.currentTimeMillis();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    for (int i = 0; i < rotation.length; i++) {
                        float result = 20 * (i + 1);
                        result =
                                (float) ((System.currentTimeMillis() - startTime) / 50f
                                        * Math.toRadians(result));
                        rotation[i] = result;
                    }
                    render.setRotation(rotation);
                    glSurfaceView.requestRender();

                }
            }
        }).start();
    }
}
