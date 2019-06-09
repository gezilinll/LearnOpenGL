package com.lbh.lesson.gles.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class CoordinateUtil {
    //默认顶点坐标
    public static final float[] VERTEX_COORD = new float[]{
            -1f, 1f,
            -1f, -1f,
            1f, -1f,
            1f, 1f,
    };
    //与默认顶点坐标顺序一致的纹理坐标，会出现纹理垂直翻转的问题
    public static final float[] TEXTURE_COORD = {
            0.0f, 1.0f,
            0.0f, 0.0f,
            1.0f, 0.0f,
            1.0f, 1.0f,
    };
    //垂直翻转后的纹理坐标
    public static final float[] TEXTURE_COORD_VERTICAL_FLIP = {
            0.0f, 0.0f,
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
    };

    //FLOAT所占字节数
    public static final int BYTES_PER_FLOAT = 4;
    //设置每个顶点的坐标数
    public static final int PER_COORDINATE_SIZE = 2;
    //下一个顶点与上一个顶点之间的步长，以字节为单位，每个float类型变量为4字节
    public static final int COORDINATE_STRIDE = PER_COORDINATE_SIZE * CoordinateUtil.BYTES_PER_FLOAT;
    //顶点个数
    public static final int VERTEX_COUNT = VERTEX_COORD.length / PER_COORDINATE_SIZE;

    public static FloatBuffer getVertexCoord() {
        return getFloatBuffer(VERTEX_COORD);
    }

    public static FloatBuffer getTextureCoord() {
        return getFloatBuffer(TEXTURE_COORD);
    }

    public static FloatBuffer getTextureVerticalFlipCoord() {
        return getFloatBuffer(TEXTURE_COORD_VERTICAL_FLIP);
    }

    public static FloatBuffer getFloatBuffer(float[] data) {
        //初始化顶点字节缓冲区，用于存放三角的顶点数据
        ByteBuffer coordBuffer = ByteBuffer.allocateDirect(
                //(每个浮点数占用4个字节
                data.length * BYTES_PER_FLOAT);
        //设置使用设备硬件的原生字节序
        coordBuffer.order(ByteOrder.nativeOrder());
        //从ByteBuffer中创建一个浮点缓冲区
        FloatBuffer result = coordBuffer.asFloatBuffer();
        //把坐标都添加到FloatBuffer中
        result.put(data);
        //设置buffer从第一个位置开始读
        //因为在每次调用put加入数据后position都会加1，因此要将position重置为0
        result.position(0);
        return result;
    }
}
