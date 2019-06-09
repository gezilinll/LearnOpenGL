package com.lbh.lesson.gles.lesson3;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import com.lbh.lesson.gles.util.CoordinateUtil;
import com.lbh.lesson.gles.util.ImageUtil;
import com.lbh.lesson.gles.util.OpenGLUtil;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by linbinghe on 2018/3/18.
 */

public class CoordinateSystemsRender implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER =
            "//设置的顶点坐标数据\n" +
                    "attribute vec4 vPosition;" +
                    "//设置的纹理坐标数据\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "//输出到片元着色器的纹理坐标数据\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform mat4 model;\n" +
                    "uniform mat4 view;\n" +
                    "uniform mat4 projection;" +
                    "void main() {" +
                    "//设置最终坐标\n" +
                    "  gl_Position = projection * view * model * vPosition;" +
                    "  textureCoordinate = vec2(inputTextureCoordinate.x, 1.0 - "
                    + "inputTextureCoordinate.y);\n"
                    +
                    "}";
    private static final String FRAGMENT_SHADER =
            "//设置float类型默认精度，顶点着色器默认highp，片元着色器需要用户声明\n" +
                    "precision mediump float;" +
                    "//从顶点着色器传入的纹理坐标数据\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "//用户传入的纹理数据\n" +
                    "uniform sampler2D inputImageTexture;\n" +
                    "uniform sampler2D inputImageTexture2;\n" +
                    "void main() {" +
                    "//该片元最终颜色值，80%的inputImageTexture，20%的inputImageTexture2\n" +
                    "  gl_FragColor = mix(texture2D(inputImageTexture, textureCoordinate), " +
                    "texture2D(inputImageTexture2, textureCoordinate), 0.2);" +
                    "}";

    //6个面，每个面2个三角形，每个三角形3个顶点
    private static final float VERTICES[] = {
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,

            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,

            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,

            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,

            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f
    };
    private static final float TEXTURE[] = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,

            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            1.0f, 0.0f,
            1.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f,

            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            1.0f, 0.0f,
            0.0f, 0.0f,
            0.0f, 1.0f
    };
    // 通过模型变换将物体放置到世界坐标系中的不同位置
    private final static float[][] CUBE_POSITIONS = {
            new float[]{0.0f, 0.0f, 0.0f},
            new float[]{2.0f, 5.0f, -15.0f},
            new float[]{-1.5f, -2.2f, -2.5f},
            new float[]{-3.8f, -2.0f, -12.3f},
            new float[]{2.4f, -0.4f, -3.5f},
            new float[]{-1.7f, 3.0f, -7.5f},
            new float[]{1.3f, -2.0f, -2.5f},
            new float[]{1.5f, 2.0f, -2.5f},
            new float[]{1.5f, 0.2f, -1.5f},
            new float[]{-1.3f, 1.0f, -1.5f}
    };

    private int mProgramId; //GL程序对象句柄
    private int mPositionId; //顶点位置句柄
    private int mTexture; //纹理句柄
    private int mTexture2; //纹理2句柄
    private int mInputTextureCoordinate; //纹理坐标句柄

    private int[] mTextureId = new int[]{OpenGLUtil.NO_TEXTURE, OpenGLUtil.NO_TEXTURE}; //用于绘制的纹理ID

    private FloatBuffer mVertexBuffer; //顶点坐标数据
    private FloatBuffer mTextureBuffer; //纹理坐标数据

    private Context mContext;

    //模型、视图、投影矩阵参数
    private int mModelMatrixId;
    private int mViewMatrixId;
    private int mProjectionMatrixId;
    private float[] mModelMatrix; //变换矩阵
    private float[] mViewMatrix; //变换矩阵
    private float[] mProjectionMatrix; //变换矩阵
    private float[] mRotation = new float[10]; //旋转角度

    private int mVPWidth = 0;
    private int mVPHeight = 0;

    public CoordinateSystemsRender(Context mContext) {
        this.mContext = mContext;

        //初始化矩阵
        mModelMatrix = new float[16];
        Matrix.setIdentityM(mModelMatrix, 0);
        mViewMatrix = new float[16];
        Matrix.setIdentityM(mViewMatrix, 0);
        mProjectionMatrix = new float[16];
        Matrix.setIdentityM(mProjectionMatrix, 0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        //编译着色器并链接顶点与片元着色器生成OpenGL程序句柄
        mProgramId = OpenGLUtil.loadProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        //通过OpenGL程序句柄查找获取顶点着色器中的顶点坐标句柄
        mPositionId = GLES20.glGetAttribLocation(mProgramId, "vPosition");
        //通过OpenGL程序句柄查找获取顶点着色器中的纹理坐标句柄
        mInputTextureCoordinate = GLES20.glGetAttribLocation(mProgramId, "inputTextureCoordinate");
        //通过OpenGL程序句柄查找获取顶点着色器中的变换矩阵句柄
        mModelMatrixId = GLES20.glGetUniformLocation(mProgramId, "model");
        mViewMatrixId = GLES20.glGetUniformLocation(mProgramId, "view");
        mProjectionMatrixId = GLES20.glGetUniformLocation(mProgramId, "projection");
        //通过OpenGL程序句柄查找获取片元着色器中的纹理句柄
        mTexture = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");
        mTexture2 = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture2");

        //获取顶点数据
        mVertexBuffer = CoordinateUtil.getFloatBuffer(VERTICES);
        //获取与顶点数据顺序相同的纹理坐标数据
        mTextureBuffer = CoordinateUtil.getFloatBuffer(TEXTURE);

        //加载纹理
        loadTexture(0, "container.jpg");
        loadTexture(1, "awesomeface.png");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        mVPWidth = width;
        mVPHeight = height;
    }

    public void setRotation(float[] rotation) {
        mRotation = rotation;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
//        drawFloorPic();
        drawMultiBox();
    }

    private void drawFloorPic() {
        //获取顶点数据
        mVertexBuffer = CoordinateUtil.getVertexCoord();
        //获取与顶点数据顺序相同的纹理坐标数据
        mTextureBuffer = CoordinateUtil.getTextureCoord();

        //开启opengl深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //这里网上很多博客说是设置背景色，其实更严格来说是通过所设置的颜色来清空颜色缓冲区，改变背景色只是其作用之一
        GLES20.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId);

        //启用指向渲染图形所处的顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId);
        //绑定渲染图形所处的顶点数据
        GLES20.glVertexAttribPointer(mPositionId, CoordinateUtil.PER_COORDINATE_SIZE,
                GLES20.GL_FLOAT, false,
                CoordinateUtil.COORDINATE_STRIDE, mVertexBuffer);

        //启用指向纹理坐标数据的句柄
        GLES20.glEnableVertexAttribArray(mInputTextureCoordinate);
        //绑定纹理坐标数据
        GLES20.glVertexAttribPointer(mInputTextureCoordinate, CoordinateUtil.PER_COORDINATE_SIZE,
                GLES20.GL_FLOAT, false, CoordinateUtil.COORDINATE_STRIDE, mTextureBuffer);

        //激活GL_TEXTURE0，该纹理单元默认激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理数据到GL_TEXTURE0纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        //将纹理对象设置到0号单元上
        GLES20.glUniform1i(mTexture, 0);

        //激活GL_TEXTURE1，该纹理单元默认激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //绑定纹理数据到GL_TEXTURE1纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[1]);
        //将纹理对象设置到1号单元上
        GLES20.glUniform1i(mTexture2, 1);

        //设置模型变换矩阵
        Matrix.setIdentityM(mModelMatrix, 0);
        //代码上的设置顺序与实际想要的顺序需要相反
        //在世界坐标系中，首先先以原点即屏幕中心为锚点缩放0.5，然后以屏幕中心为锚点旋转角度，最后平移
        //如果旋转和平移反过来，那么就会先做平移，然后以屏幕中心为锚点进行旋转，效果不同
        Matrix.translateM(mModelMatrix, 0, mModelMatrix, 0, 0.5f, -0.5f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, -75f, 1.0f, 0f, 0f);
        Matrix.scaleM(mModelMatrix, 0, 0.5f, 0.5f, 1.0f);
        //绑定变换矩阵
        GLES20.glUniformMatrix4fv(mModelMatrixId, 1, false, mModelMatrix, 0);

        //设置视图矩阵
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.translateM(mViewMatrix, 0, 0, 0, -3.0f);
        GLES20.glUniformMatrix4fv(mViewMatrixId, 1, false, mViewMatrix, 0);

        //设置投影矩阵
        Matrix.setIdentityM(mProjectionMatrix, 0);
        Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, (float) mVPWidth / (float) mVPHeight,
                0.1f, 100.0f);
        GLES20.glUniformMatrix4fv(mProjectionMatrixId, 1, false, mProjectionMatrix, 0);

        //使用GL_TRIANGLE_FAN方式绘制纹理
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, CoordinateUtil.VERTEX_COUNT);

        //禁用顶点数据对象
        GLES20.glDisableVertexAttribArray(mPositionId);
        //禁用纹理坐标数据对象
        GLES20.glDisableVertexAttribArray(mInputTextureCoordinate);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //关闭opengl深度测试
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    private void drawMultiBox() {
        //开启opengl深度测试
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        //这里网上很多博客说是设置背景色，其实更严格来说是通过所设置的颜色来清空颜色缓冲区，改变背景色只是其作用之一
        GLES20.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        //告知OpenGL所要使用的Program
        GLES20.glUseProgram(mProgramId);

        //启用指向渲染图形所处的顶点数据的句柄
        GLES20.glEnableVertexAttribArray(mPositionId);
        //绑定渲染图形所处的顶点数据
        GLES20.glVertexAttribPointer(mPositionId, 3,
                GLES20.GL_FLOAT, false,
                3 * Float.BYTES, mVertexBuffer);

        //启用指向纹理坐标数据的句柄
        GLES20.glEnableVertexAttribArray(mInputTextureCoordinate);
        //绑定纹理坐标数据
        GLES20.glVertexAttribPointer(mInputTextureCoordinate, 2,
                GLES20.GL_FLOAT, false, 2 * Float.BYTES, mTextureBuffer);

        //激活GL_TEXTURE0，该纹理单元默认激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        //绑定纹理数据到GL_TEXTURE0纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[0]);
        //将纹理对象设置到0号单元上
        GLES20.glUniform1i(mTexture, 0);

        //激活GL_TEXTURE1，该纹理单元默认激活
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        //绑定纹理数据到GL_TEXTURE1纹理单元上
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[1]);
        //将纹理对象设置到1号单元上
        GLES20.glUniform1i(mTexture2, 1);

        for (int i = 0; i < CUBE_POSITIONS.length; i++) {
            //设置模型变换矩阵
            Matrix.setIdentityM(mModelMatrix, 0);
            //代码上的设置顺序与实际想要的顺序需要相反
            //在世界坐标系中，首先先以原点即屏幕中心为锚点缩放0.5，然后以屏幕中心为锚点旋转角度，最后平移
            //如果旋转和平移反过来，那么就会先做平移，然后以屏幕中心为锚点进行旋转，效果不同
            Matrix.translateM(mModelMatrix, 0, CUBE_POSITIONS[i][0], CUBE_POSITIONS[i][1],
                    CUBE_POSITIONS[i][2]);
            Matrix.rotateM(mModelMatrix, 0, mRotation[i], 1.0f, 0.3f, 0.5f);
            //绑定变换矩阵
            GLES20.glUniformMatrix4fv(mModelMatrixId, 1, false, mModelMatrix, 0);

            //设置视图矩阵
            Matrix.setIdentityM(mViewMatrix, 0);
            Matrix.translateM(mViewMatrix, 0, 0, 0, -3.0f);
            GLES20.glUniformMatrix4fv(mViewMatrixId, 1, false, mViewMatrix, 0);

            //设置投影矩阵
            Matrix.setIdentityM(mProjectionMatrix, 0);
            Matrix.perspectiveM(mProjectionMatrix, 0, 45.0f, (float) mVPWidth / (float) mVPHeight,
                    0.1f, 100.0f);
            GLES20.glUniformMatrix4fv(mProjectionMatrixId, 1, false, mProjectionMatrix, 0);

            //使用GL_TRIANGLES方式绘制纹理
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
        }

        //禁用顶点数据对象
        GLES20.glDisableVertexAttribArray(mPositionId);
        //禁用纹理坐标数据对象
        GLES20.glDisableVertexAttribArray(mInputTextureCoordinate);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);

        //关闭opengl深度测试
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
    }

    private void loadTexture(int index, String bitmapName) {
        //生成原始纹理图片
        Bitmap texture = getTextureBitmap(bitmapName);
        if (mTextureId[index] == OpenGLUtil.NO_TEXTURE) {
            //若尚未创建过纹理对象
            //创建一个纹理对象，存放于mTextureId数组内
            GLES20.glGenTextures(1, mTextureId, index);
            //将该纹理绑定到GL_TEXTURE_2D目标，代表这是一个二维纹理
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[index]);
            //设置其放大缩小滤波方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
            //设置其WRAP方式
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

            //将图片绑定到当前glBindTexture的纹理对象上
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, texture, 0);
        } else {
            //若创建过纹理对象，则重复利用
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId[index]);
            //更新已存在的纹理对象中的纹理数据
            GLUtils.texSubImage2D(GLES20.GL_TEXTURE_2D, 0, 0, 0, texture);
        }
        //绑定完纹理后可直接回收图片资源
        texture.recycle();
    }

    private Bitmap getTextureBitmap(String bitmapName) {
        return ImageUtil.getBitmapFromAssets(mContext, bitmapName);
    }

    public void destroy() {
        if (mTextureId[0] != OpenGLUtil.NO_TEXTURE) {
            GLES20.glDeleteTextures(1, mTextureId, 0);
        }
        GLES20.glDeleteProgram(mProgramId);
    }
}
