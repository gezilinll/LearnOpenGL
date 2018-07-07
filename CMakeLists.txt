cmake_minimum_required(VERSION 3.10)
project(LearnOpenGL)

set(CMAKE_CXX_STANDARD 11)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -v -stdlib=libc++ -framework AppKit -framework CoreVideo -framework IOKit -framework CoreGraphics -framework CoreFoundation -I/usr/local/include -L/usr/local/lib/ -I/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/include -L/Applications/Xcode.app/Contents/Developer/Platforms/MacOSX.platform/Developer/SDKs/MacOSX.sdk/usr/lib/")

include_directories(/usr/include)

include_directories(/Users/linbinghe/CProjects/glad/include)

include_directories("${PROJECT_SOURCE_DIR}/include/")

LINK_DIRECTORIES( /usr/local/lib )

set(SOURCE_FILES glad.c tutorial02_red_triangle.cpp include/shader_s.h)

# Tutorial 1
add_executable(tutorial01_first_window
        glad.c tutorial01_first_window.cpp)
target_link_libraries(tutorial01_first_window libglfw3.a)

# Tutorial 2
add_executable(tutorial02_red_triangle ${SOURCE_FILES})
target_link_libraries(tutorial02_red_triangle libglfw3.a)