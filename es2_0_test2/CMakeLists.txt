cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

# cmakelists 设置c++11  因为在类中对非静态的初始化需要用到
include(CheckCXXCompilerFlag)
CHECK_CXX_COMPILER_FLAG("-std=c++11" COMPILER_SUPPORTS_CXX11)
CHECK_CXX_COMPILER_FLAG("-std=c++0x" COMPILER_SUPPORTS_CXX0X)
if(COMPILER_SUPPORTS_CXX11)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")
elseif(COMPILER_SUPPORTS_CXX0X)
    set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++0x")
else()
    message(STATUS "The compiler ${CMAKE_CXX_COMPILER} has no C++11 support. Please use a different C++ compiler.")

endif()
# cmakelists 设置c++11

add_library( # Sets the name of the library.
        push-lib
        SHARED
        src/main/cpp/Push.cpp
        src/main/cpp/Queue.cpp
        src/main/cpp/RtmpPush.cpp
        src/main/cpp/CallJava.cpp

        # .c 文件加入里面一起生成so文件
        src/main/cpp/librtmp/amf.c
        src/main/cpp/librtmp/hashswf.c
        src/main/cpp/librtmp/log.c
        src/main/cpp/librtmp/parseurl.c
        src/main/cpp/librtmp/rtmp.c

        )

find_library( # Sets the name of the path variable.
        log-lib
        log )
target_link_libraries( # Specifies the target library.
        push-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )