#ifndef CAMERA_H
#define CAMERA_H

#include <glad/glad.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#include <vector>

// 定义相机可移动的类型。
enum Camera_Movement {
    FORWARD,
    BACKWARD,
    LEFT,
    RIGHT
};

// 相机默认参数
const float YAW = -90.0f; //偏航角，初始化为-90度因为最后于俯仰角结合生成cameraFront，cameraFront需要看向z的负半轴
const float PITCH = 0.0f; //俯仰角
const float SPEED = 2.5f; //速度
const float SENSITIVITY = 0.1f; //敏感度
const float ZOOM = 45.0f; //缩放

//相机类，用于处理输入以及计算相应的用于OpenGL中的欧拉角，向量以及矩阵
class Camera {
public:
    //相机参数
    glm::vec3 position;
    glm::vec3 front;
    glm::vec3 up;
    glm::vec3 right;
    glm::vec3 worldUp;
    //欧拉角
    float yaw;
    float pitch;
    //相机操作
    float movementSpeed;
    float mouseSensitivity;
    float zoom;

    //以向量构造一个相机类
    Camera(glm::vec3 position = glm::vec3(0.0f, 0.0f, 0.0f), glm::vec3 up = glm::vec3(0.0f, 1.0f, 0.0f),
           float yaw = YAW, float pitch = PITCH) : front(glm::vec3(0.0f, 0.0f, -1.0f)), movementSpeed(SPEED),
                                                   mouseSensitivity(SENSITIVITY), zoom(ZOOM) {
        this->position = position;
        this->worldUp = up;
        this->yaw = yaw;
        this->pitch = pitch;
        updateCameraVectors();
    }

    //以标准值构造一个相机
    Camera(float posX, float posY, float posZ, float upX, float upY, float upZ, float yaw, float pitch) : front(
            glm::vec3(0.0f, 0.0f, -1.0f)), movementSpeed(SPEED), mouseSensitivity(SENSITIVITY), zoom(ZOOM) {
        this->position = glm::vec3(posX, posY, posZ);
        this->worldUp = glm::vec3(upX, upY, upZ);
        this->yaw = yaw;
        this->pitch = pitch;
        updateCameraVectors();
    }

    // 返回视图矩阵，该矩阵通过欧拉角和LookAt方式计算所得
    glm::mat4 GetViewMatrix() {
        //positon指相机的位置，front指相机头的朝向向量
        //第二个参数计算所得的方向向量与摄像机头所指向的方向刚好相反
        return glm::lookAt(position, position + front, up);
    }

    // 键盘输入事件
    void ProcessKeyboard(Camera_Movement direction, float deltaTime) {
        float velocity = movementSpeed * deltaTime;
        if (direction == FORWARD)
            position += front * velocity;
        if (direction == BACKWARD)
            position -= front * velocity;
        if (direction == LEFT)
            position -= right * velocity;
        if (direction == RIGHT)
            position += right * velocity;
    }

    // 鼠标事件
    // 第三个参数带来的限制：
    // 对于俯仰角，要让用户不能看向高于89度的地方（在90度时视角会发生逆转，所以我们把89度作为极限）
    // 同样也不允许小于-89度。这样能够保证用户只能看到天空或脚下，但是不能超越这个限制。
    void ProcessMouseMovement(float xoffset, float yoffset, GLboolean constrainPitch = true) {
        xoffset *= mouseSensitivity;
        yoffset *= mouseSensitivity;

        yaw += xoffset;
        pitch += yoffset;

        // 当俯仰角被限制在这个范围内时，屏幕就不会出现翻转的奇怪现象
        if (constrainPitch) {
            if (pitch > 89.0f)
                pitch = 89.0f;
            if (pitch < -89.0f)
                pitch = -89.0f;
        }

        updateCameraVectors();
    }

    // 鼠标滚轮事件，只接受垂直滚轮
    void ProcessMouseScroll(float yoffset) {
        if (zoom >= 1.0f && zoom <= 45.0f)
            zoom -= yoffset;
        if (zoom <= 1.0f)
            zoom = 1.0f;
        if (zoom >= 45.0f)
            zoom = 45.0f;
    }

private:
    // 从相机更新后的欧拉角计算相机头的前向向量
    void updateCameraVectors() {
        // Calculate the new Front vector
        glm::vec3 front;
        front.x = cos(glm::radians(yaw)) * cos(glm::radians(pitch));
        front.y = sin(glm::radians(pitch));
        front.z = sin(glm::radians(yaw)) * cos(glm::radians(pitch));
        this->front = glm::normalize(front);
        // 重新计算右向量与上向量
        this->right = glm::normalize(glm::cross(this->front,
                                                this->worldUp));  // 标准化向量，因为当你越往上或者越往下看时候向量的长度就越接近于0，这将导致越来越慢地移动效果。标准化后才能达到允许的效果
        this->up = glm::normalize(glm::cross(this->right, this->front));
    }
};

#endif