cmake_minimum_required(VERSION 3.10)
project(LearnOpenGL)

set(CMAKE_CXX_STANDARD 11)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -v -stdlib=libc++ -framework AppKit -framework CoreVideo -framework IOKit -framework CoreGraphics -framework CoreFoundation")

include_directories(/usr/include)

include_directories(/Users/linbinghe/CProjects/glad/include)

include_directories(/Users/linbinghe/Downloads/ogl-2.1_branch/external/glm-0.9.7.1/glm/)

include_directories("${PROJECT_SOURCE_DIR}/include/")


LINK_DIRECTORIES( /usr/local/lib )

set(SOURCE_FILES glad.c tutorial02_red_triangle.cpp std_image.cpp /Users/linbinghe/Downloads/ogl-2.1_branch/external/glm-0.9.7.1/glm/ include/shader_s.h include/stb_image.h)

# Tutorial 1[入门-第一个窗口]
add_executable(tutorial01_first_window
        glad.c tutorial01_first_window.cpp)
target_link_libraries(tutorial01_first_window libglfw3.a)

# Tutorial 2[入门-三角形]
add_executable(tutorial02_red_triangle ${SOURCE_FILES})
target_link_libraries(tutorial02_red_triangle libglfw3.a)

# getting_started_transformation[入门-变换]
set(SOURCE_FILES_GETTING_STARTED_TRANSFORMATION
        glad.c getting_started_transformation.cpp std_image.cpp
        /Users/linbinghe/Downloads/ogl-2.1_branch/external/glm-0.9.7.1/glm/ include/shader_s.h include/stb_image.h)
add_executable(getting_started_transformation ${SOURCE_FILES_GETTING_STARTED_TRANSFORMATION})
target_link_libraries(getting_started_transformation libglfw3.a)

# getting_started_coordinate_systems[入门-坐标系]
set(SOURCE_FILES_GETTING_STARTED_COORDINATE_SYSTEMS
        glad.c getting_started_coordinate_systems.cpp std_image.cpp
        /Users/linbinghe/Downloads/ogl-2.1_branch/external/glm-0.9.7.1/glm/ include/shader_s.h include/stb_image.h)
add_executable(getting_started_coordinate_systems ${SOURCE_FILES_GETTING_STARTED_COORDINATE_SYSTEMS})
target_link_libraries(getting_started_coordinate_systems libglfw3.a)