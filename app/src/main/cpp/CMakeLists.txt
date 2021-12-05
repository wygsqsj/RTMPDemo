cmake_minimum_required(VERSION 3.10.2)

project("rtmpdemo")
#添指向librtmp文件夹，编译该文件夹下的文件
add_subdirectory(librtmp)

add_library(
        native-lib
        SHARED
        native-lib.cpp)

find_library(
        log-lib
        log)

#添加动态链接库
target_link_libraries(
        native-lib
        rtmp
        ${log-lib})