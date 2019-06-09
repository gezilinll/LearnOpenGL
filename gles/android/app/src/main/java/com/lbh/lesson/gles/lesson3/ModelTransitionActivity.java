package com.lbh.lesson.gles.lesson3;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lbh.lesson.gles.R;
import com.lbh.lesson.gles.lesson2.TextureRender;

public class ModelTransitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        final GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
        // 创建OpenGL ES 2.0的上下文
        glSurfaceView.setEGLContextClientVersion(2);
        //设置Renderer用于绘图
        final ModelTransitionRender render = new ModelTransitionRender(this);
        glSurfaceView.setRenderer(render);
        //只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
        //该种模式下当需要重绘时需要我们手动调用glSurfaceView.requestRender();
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        new Thread(new Runnable() {
            @Override
            public void run() {
                int rotation = 0;
                while (true) {
                    rotation++;
                    render.setRotation(rotation % 360);
                    glSurfaceView.requestRender();
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
