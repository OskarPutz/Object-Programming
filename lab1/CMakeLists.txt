cmake_minimum_required(VERSION 3.29)
project(lab1)

set(CMAKE_CXX_STANDARD 14)

include_directories(.)

add_executable(lab1
    Circle.cpp
    Circle.h
    main.cpp
    PlainFigure.cpp
    PlainFigure.h
    Rectangle.cpp
    Rectangle.h
    Triangle.cpp
    Triangle.h)
