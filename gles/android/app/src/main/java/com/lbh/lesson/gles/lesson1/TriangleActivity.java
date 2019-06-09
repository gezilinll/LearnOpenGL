package com.lbh.lesson.gles.lesson1;

import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lbh.lesson.gles.R;

public class TriangleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triangle);

        GLSurfaceView glSurfaceView = findViewById(R.id.gl_surface);
        // 创建OpenGL ES 2.0的上下文
        glSurfaceView.setEGLContextClientVersion(2);
        //设置Renderer用于绘图
        glSurfaceView.setRenderer(new TriangleRender());
        //只有在绘制数据改变时才绘制view，可以防止GLSurfaceView帧重绘
        //该种模式下当需要重绘时需要我们手动调用glSurfaceView.requestRender();
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
