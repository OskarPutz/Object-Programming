cmake_minimum_required(VERSION 3.29)
project(labs)

set(CMAKE_CXX_STANDARD 20)

add_executable(labs main.cpp
        PlainFigure.h
        PlainFigure.cpp
        Rectangle.cpp
        Rectangle.h
        Triangle.h
        Triangle.cpp
        Circle.cpp
        Circle.h)
