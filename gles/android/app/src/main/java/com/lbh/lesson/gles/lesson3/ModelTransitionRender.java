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

public class ModelTransitionRender implements GLSurfaceView.Renderer {

    private static final String VERTEX_SHADER =
            "//设置的顶点坐标数据\n" +
                    "attribute vec4 vPosition;" +
                    "//设置的纹理坐标数据\n" +
                    "attribute vec4 inputTextureCoordinate;\n" +
                    "//输出到片元着色器的纹理坐标数据\n" +
                    "varying vec2 textureCoordinate;\n" +
                    "uniform mat4 transform;\n" +
                    "void main() {" +
                    "//设置最终坐标\n" +
                    "  gl_Position = transform * vPosition;" +
                    "  textureCoordinate = inputTextureCoordinate.xy;\n" +
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


    private int mProgramId; //GL程序对象句柄
    private int mPositionId; //顶点位置句柄
    private int mTexture; //纹理句柄
    private int mTexture2; //纹理2句柄
    private int mInputTextureCoordinate; //纹理坐标句柄

    private int[] mTextureId = new int[]{OpenGLUtil.NO_TEXTURE, OpenGLUtil.NO_TEXTURE}; //用于绘制的纹理ID

    private FloatBuffer mVertexBuffer; //顶点坐标数据
    private FloatBuffer mTextureBuffer; //纹理坐标数据

    private Context mContext;

    private int mTransformMatrixId; //变换矩阵句柄
    private float[] mTransformMatrix; //变换矩阵
    private int mRotation = 0; //旋转角度

    public ModelTransitionRender(Context mContext) {
        this.mContext = mContext;

        //初始化变换矩阵
        mTransformMatrix = new float[16];
        Matrix.setIdentityM(mTransformMatrix, 0);
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
        mTransformMatrixId = GLES20.glGetUniformLocation(mProgramId, "transform");
        //通过OpenGL程序句柄查找获取片元着色器中的纹理句柄
        mTexture = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture");
        mTexture2 = GLES20.glGetUniformLocation(mProgramId, "inputImageTexture2");

        //获取顶点数据
        mVertexBuffer = CoordinateUtil.getVertexCoord();
        //获取与顶点数据顺序相同的纹理坐标数据
//        mTextureBuffer = CoordinateUtil.getTextureCoord();
        //获取与顶点数据顺序垂直翻转的纹理坐标数据
        mTextureBuffer = CoordinateUtil.getTextureVerticalFlipCoord();

        //加载纹理
        loadTexture(0, "container.jpg");
        loadTexture(1, "awesomeface.png");
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
    }

    public void setRotation(int rotation) {
        mRotation = rotation;
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        //这里网上很多博客说是设置背景色，其实更严格来说是通过所设置的颜色来清空颜色缓冲区，改变背景色只是其作用之一
        GLES20.glClearColor(0.2f, 0.3f, 0.3f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

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

        //设置变换矩阵
        Matrix.setIdentityM(mTransformMatrix, 0);
        //代码上的设置顺序与实际想要的顺序需要相反
        //在世界坐标系中，首先先以原点即屏幕中心为锚点缩放0.5，然后以屏幕中心为锚点旋转角度，最后平移
        //如果旋转和平移反过来，那么就会先做平移，然后以屏幕中心为锚点进行旋转，效果不同
        Matrix.translateM(mTransformMatrix, 0, 0.5f, -0.5f, 0.0f);
        Matrix.rotateM(mTransformMatrix, 0, mRotation, 0.0f, 0.0f, 1.0f);
        Matrix.scaleM(mTransformMatrix, 0, 0.5f, 0.5f, 1.0f);
        //绑定变换矩阵
        GLES20.glUniformMatrix4fv(mTransformMatrixId, 1, false, mTransformMatrix, 0);

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

        //使用GL_TRIANGLE_FAN方式绘制纹理
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, CoordinateUtil.VERTEX_COUNT);

        //禁用顶点数据对象
        GLES20.glDisableVertexAttribArray(mPositionId);
        //禁用纹理坐标数据对象
        GLES20.glDisableVertexAttribArray(mInputTextureCoordinate);
        //解绑纹理
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
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
