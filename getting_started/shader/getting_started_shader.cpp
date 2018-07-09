#include <iostream>
#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include "shader_s.h"

void framebuffer_size_callback(GLFWwindow* window, int width, int height);
void processInput(GLFWwindow *window);

void framebuffer_size_callback(GLFWwindow* window, int width, int height) {
    //前两个参数控制窗口左下角的位置
    //OpenGL幕后使用glViewport中定义的位置和宽高进行2D坐标的转换，将OpenGL中的位置坐标转换为你的屏幕坐标。
    //处理过的OpenGL坐标范围只为-1到1，因此我们事实上将(-1到1)范围内的坐标映射到(0, 800)和(0, 600)
    glViewport(0, 0, width, height);
}

void processInput(GLFWwindow *window) {
    //如果没有按下，glfwGetKey将会返回GLFW_RELEASE
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, true);
    }
}

int main() {
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

    GLFWwindow *window = glfwCreateWindow(800, 600, "LearnOpenGL", NULL, NULL);
    if (window == NULL) {
        std::cout << "Failed to create GLFW window." << std::endl;
        glfwTerminate();
        return -1;
    }

    glfwMakeContextCurrent(window);
    glfwSetFramebufferSizeCallback(window, framebuffer_size_callback);

    if (!gladLoadGLLoader((GLADloadproc) glfwGetProcAddress)) {
        std::cout << "Failed to initialize GLAD" << std::endl;
        return -1;
    }

    //构建着色器程序
    Shader ourShader("/Users/linbinghe/CProjects/LearnOpenGL/getting_started/shader/getting_started_vertex_shader.glsl",
                     "/Users/linbinghe/CProjects/LearnOpenGL/getting_started/shader/getting_started_fragment_shader.glsl");

    // 设置顶点数据和属性等
    // ------------------------------------------------------------------
    float vertices[] = {
            // positions         // colors
            0.5f, -0.5f, 0.0f,  1.0f, 0.0f, 0.0f,  // bottom right
            -0.5f, -0.5f, 0.0f,  0.0f, 1.0f, 0.0f,  // bottom left
            0.0f,  0.5f, 0.0f,  0.0f, 0.0f, 1.0f   // top
    };

    unsigned int VBO, VAO;
    glGenVertexArrays(1, &VAO);
    glGenBuffers(1, &VBO);
    //首先绑定顶点数组对象VAO，然后绑定和设置顶点缓冲，之后配置顶点属性
    glBindVertexArray(VAO);

    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, sizeof(vertices), vertices, GL_STATIC_DRAW);

    // 位置属性
    glVertexAttribPointer(0, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(float), (void*)0);
    glEnableVertexAttribArray(0);
    // 颜色属性
    glVertexAttribPointer(1, 3, GL_FLOAT, GL_FALSE, 6 * sizeof(float), (void*)(3 * sizeof(float)));
    glEnableVertexAttribArray(1);

    // 你可以在最后解绑VAO以防止其他VAO操作影响到了当前这个VAO，但是这很少发生。
    // 如果要修改其他VAO，必须先调用glBindVertexArray绑定对应的VAO，所以如果不是必须的话我们一般不去解绑VAO或者VBO
    glBindVertexArray(0);

    while (!glfwWindowShouldClose(window)) {

        processInput(window);

        //调用了glClearColor来设置清空屏幕所用的颜色，此处设置为暗蓝色背景
        //当调用glClear函数，清除颜色缓冲之后，整个颜色缓冲都会被填充为glClearColor里所设置的颜色。
        //glClearColor函数就是一个状态设置函数，而glClear函数则是一个状态使用的函数，它使用了当前的状态来获取应该清除为的颜色。
        glClearColor(0.0f, 0.0f, 0.4f, 0.0f);
        //清空屏幕，在第二篇教程前没有提到，但是如果不加上，那么就会导致屏幕闪烁，所以在这里仍然加上
        //在每个新的渲染迭代开始的时候我们总是希望清屏，否则我们仍能看见上一次迭代的渲染结果（这可能是你想要的效果，但通常这不是）
        //通过调用glClear函数来清空屏幕的颜色缓冲，它接受一个缓冲位(Buffer Bit)来指定要清空的缓冲，可能的缓冲位有GL_COLOR_BUFFER_BIT，GL_DEPTH_BUFFER_BIT和GL_STENCIL_BUFFER_BIT。
        glClear( GL_COLOR_BUFFER_BIT );

        ourShader.use();
        glBindVertexArray(VAO);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        //实际上不需要每次都解绑
        glBindVertexArray(0);

        //glfwSwapBuffers函数会交换颜色缓冲（它是一个储存着GLFW窗口每一个像素颜色值的大缓冲），它在这一迭代中被用来绘制，并且将会作为输出显示在屏幕上。
        //生成的图像不是一下子被绘制出来的，而是按照从左到右，由上而下逐像素地绘制而成的。
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    //可选操作：删除所有资源
    glDeleteVertexArrays(1, &VAO);
    glDeleteBuffers(1, &VBO);

    //删除之前创建的GLFW资源
    glfwTerminate();

    return 0;
}

/**
 * 练习：
 * 使用out关键字把顶点位置输出到片段着色器，并将片段的颜色设置为与顶点位置相等（来看看连顶点位置值都在三角形中被插值的结果）。
 * 做完这些后，尝试回答下面的问题：为什么在三角形的左下角是黑的? 答案:https://learnopengl.com/code_viewer.php?code=getting-started/shaders-exercise3
 * */