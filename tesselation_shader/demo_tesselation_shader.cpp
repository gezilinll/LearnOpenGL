#include <iostream>
#include <glad/glad.h>
#include <GLFW/glfw3.h>
#include "shader_s.h"
#include "stb_image.h"
#include "glm/detail/type_mat.hpp"
#include "glm/glm.hpp"
#include "glm/gtc/matrix_transform.hpp"
#include "glm/gtc/type_ptr.hpp"
#include "camera.h"

void framebuffer_size_callback(GLFWwindow *window, int width, int height);

void processInput(GLFWwindow *window);

void checkCompileErrors(unsigned int shader, std::string type) {
    int success;
    char infoLog[1024];
    if (type != "PROGRAM") {
        glGetShaderiv(shader, GL_COMPILE_STATUS, &success);
        if (!success) {
            glGetShaderInfoLog(shader, 1024, NULL, infoLog);
            std::cout << "ERROR::SHADER_COMPILATION_ERROR of type: " << type << "\n" << infoLog
                      << "\n -- --------------------------------------------------- -- " << std::endl;
        }
    } else {
        glGetProgramiv(shader, GL_LINK_STATUS, &success);
        if (!success) {
            glGetProgramInfoLog(shader, 1024, NULL, infoLog);
            std::cout << "ERROR::PROGRAM_LINKING_ERROR of type: " << type << "\n" << infoLog
                      << "\n -- --------------------------------------------------- -- " << std::endl;
        }
    }
}

const unsigned int SCR_WIDTH = 800;
const unsigned int SCR_HEIGHT = 600;

GLuint program;
GLuint vao;

int main() {
    glfwInit();
    glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
    glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 1);
    glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
    glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);

    GLFWwindow *window = glfwCreateWindow(SCR_WIDTH, SCR_HEIGHT, "LearnOpenGL", NULL, NULL);
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

    static const char * vs_source[] =
            {
                    "#version 410 core                                                 \n"
                    "                                                                  \n"
                    "void main(void)                                                   \n"
                    "{                                                                 \n"
                    "    const vec4 vertices[] = vec4[](vec4( 0.25, -0.25, 0.5, 1.0),  \n"
                    "                                   vec4(-0.25, -0.25, 0.5, 1.0),  \n"
                    "                                   vec4( 0.25,  0.25, 0.5, 1.0)); \n"
                    "                                                                  \n"
                    "    gl_Position = vertices[gl_VertexID];                          \n"	// 根据当前处理的顶点 ID 为顶点位置赋值
                    "}                                                                 \n"
            };

    // http://www.cnblogs.com/zenny-chen/p/4280100.html
    static const char * tcs_source[] =
            {
                    "#version 410 core                                                                 \n"
                    "                                                                                  \n"
                    "layout (vertices = 3) out;                                                        \n"	// out-patch 的顶点个数，细分控制着色器将会被执行3次
                    "                                                                                  \n"
                    "void main(void)                                                                   \n"
                    "{                                                                                 \n"	// 仅在第一次执行（第一个顶点）时赋值（控制细分程度）
                    "    if (gl_InvocationID == 0)                                                     \n"
                    "    {                                                                             \n"
                    "        gl_TessLevelInner[0] = 5.0;                                               \n"	// 内部划分5个区域（新增4排顶点，见下图）
                    "        gl_TessLevelOuter[0] = 5.0;                                               \n"	// 左边划分5段
                    "        gl_TessLevelOuter[1] = 5.0;                                               \n"	// 右边划分5段
                    "        gl_TessLevelOuter[2] = 5.0;                                               \n"	// 下边划分5段
                    "    }                                                                             \n"
                    "    gl_out[gl_InvocationID].gl_Position = gl_in[gl_InvocationID].gl_Position;     \n"	// 通常直接将 in-patch 顶点赋给 out-patch 顶点（也可以新建或移除）
                    "}                                                                                 \n"
            };

    static const char * tes_source[] =
            {
                    "#version 410 core                                                                 \n"
                    "                                                                                  \n"
                    "layout (triangles, equal_spacing, cw) in;                                         \n"	 // 指定图元生成域、细分坐标空间、图元的面朝向
                    //"layout (triangles, equal_spacing, cw, point_mode) in;                             \n" // 输出点模式
                    "                                                                                  \n"
                    "void main(void)                                                                   \n"
                    "{                                                                                 \n"
                    "    gl_Position = (gl_TessCoord.x * gl_in[0].gl_Position) +                       \n"	// gl_TessCoord：细分后的新增坐标（插值比例）
                    "                  (gl_TessCoord.y * gl_in[1].gl_Position) +                       \n"	// 根据 input-patch 生成输出顶点的位置
                    "                  (gl_TessCoord.z * gl_in[2].gl_Position);                        \n"  // 每个细分坐标都会让 TES 的执行一次
                    "}                                                                                 \n"
            };

    static const char * fs_source[] =
            {
                    "#version 410 core                                                 \n"
                    "                                                                  \n"
                    "out vec4 color;                                                   \n"
                    "                                                                  \n"
                    "void main(void)                                                   \n"
                    "{                                                                 \n"
                    "    color = vec4(0.0, 0.8, 1.0, 1.0);                             \n"
                    "}                                                                 \n"
            };

    program = glCreateProgram();
    GLuint vs = glCreateShader(GL_VERTEX_SHADER);
    glShaderSource(vs, 1, vs_source, NULL);
    glCompileShader(vs);
    checkCompileErrors(vs, "Vertex");

    // Tesselation Control Shader
    GLuint tcs = glCreateShader(GL_TESS_CONTROL_SHADER);
    glShaderSource(tcs, 1, tcs_source, NULL);
    glCompileShader(tcs);
    checkCompileErrors(tcs, "Tesselation-Control");

    // Tesselation Evaluation Shader
    GLuint tes = glCreateShader(GL_TESS_EVALUATION_SHADER);
    glShaderSource(tes, 1, tes_source, NULL);
    glCompileShader(tes);
    checkCompileErrors(tes, "Tesselation-Evaluation");

    GLuint fs = glCreateShader(GL_FRAGMENT_SHADER);
    glShaderSource(fs, 1, fs_source, NULL);
    glCompileShader(fs);
    checkCompileErrors(fs, "Fragment");

    glAttachShader(program, vs);
    glAttachShader(program, tcs);
    glAttachShader(program, tes);
    glAttachShader(program, fs);

    glLinkProgram(program);
    checkCompileErrors(program, "PROGRAM");

    glGenVertexArrays(1, &vao);
    glBindVertexArray(vao);

    glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);

    while (!glfwWindowShouldClose(window)) {

        // 键盘输入处理
        processInput(window);

        static const GLfloat green[] = { 0.0f, 0.25f, 0.0f, 1.0f };
        glClearBufferfv(GL_COLOR, 0, green);

        glUseProgram(program);
        glDrawArrays(GL_PATCHES, 0, 3);

        //glfwSwapBuffers函数会交换颜色缓冲（它是一个储存着GLFW窗口每一个像素颜色值的大缓冲），它在这一迭代中被用来绘制，并且将会作为输出显示在屏幕上。
        //生成的图像不是一下子被绘制出来的，而是按照从左到右，由上而下逐像素地绘制而成的。
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    glDeleteVertexArrays(1, &vao);
    glDeleteProgram(program);

    //删除之前创建的GLFW资源
    glfwTerminate();

    return 0;
}


void framebuffer_size_callback(GLFWwindow *window, int width, int height) {
    //前两个参数控制窗口左下角的位置
    //OpenGL幕后使用glViewport中定义的位置和宽高进行2D坐标的转换，将OpenGL中的位置坐标转换为你的屏幕坐标。
    //处理过的OpenGL坐标范围只为-1到1，因此我们事实上将(-1到1)范围内的坐标映射到(0, 800)和(0, 600)
    glViewport(0, 0, width, height);
}

void processInput(GLFWwindow *window) {
    //如果没有按下，glfwGetKey将会返回GLFW_RELEASE
    if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
        glfwSetWindowShouldClose(window, true);
    } else if (glfwGetKey(window, GLFW_KEY_M) == GLFW_PRESS) {
        static GLenum mode = GL_FILL;
        mode = (mode == GL_FILL ? GL_LINE : GL_FILL);
        glPolygonMode(GL_FRONT_AND_BACK, mode);
    }
}


/**
 * 练习：
 *
 * 看看你是否能够修改摄像机类，使得其能够变成一个真正的FPS摄像机（也就是说不能够随意飞行）；你只能够呆在xz平面上。答案：https://learnopengl.com/code_viewer.php?code=getting-started/camera-exercise1
 *
 * 试着创建你自己的LookAt函数，其中你需要手动创建一个我们在一开始讨论的观察矩阵。用你的函数实现来替换GLM的LookAt函数，看看它是否还能一样地工作：
 * 答案：https://learnopengl.com/code_viewer.php?code=getting-started/camera-exercise2
 *
 * */