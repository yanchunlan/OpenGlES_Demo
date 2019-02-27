cmake_minimum_required(VERSION 3.4.1)

add_library( # Sets the name of the library.
             native-lib
             SHARED
             src/main/cpp/native-lib.cpp )


find_library( # Sets the name of the path variable.
              log-lib
              log )

target_link_libraries( # Specifies the target library.
                       native-lib
                        EGL
                        GLESv2
                        android
                       ${log-lib} )