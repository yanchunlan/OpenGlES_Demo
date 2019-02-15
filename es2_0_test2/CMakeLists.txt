cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")


add_library( # Sets the name of the library.
        native-lib
        SHARED
        src/main/cpp/native-lib.cpp

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
        native-lib

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib} )