#include "Triangle.h"
#include <iostream>
#include <cmath>

using namespace std;
Triangle::Triangle(double a, double b, double c)
: a(a), b(b), c(c)
{
    cout << "Constructor Triangle(" << a <<"," << b << "," << c << ")" << endl;
}
double Triangle::GetA() const {
    return a;
}
double Triangle::GetB() const {
    return b;
}
double Triangle::GetC() const {
    return c;
}
void Triangle::SetA(double a) {
    this->a = a;
}
void Triangle::SetB(double b) {
    this->b = b;
}
void Triangle::SetC(double c) {
    this->c = c;
}
double Triangle::Perimeter() {
    return a + b + c;
}

double Triangle::Area() {
    double s = (a + b + c) / 2;
    return sqrt(s * (s - a) * (s - b) * (s - c));
}
void Triangle::Print(std::ostream& out) const {
    cout << "Triangle: a = " << a << ", b = " << b << ", c = " << c << endl;
}
Triangle::~Triangle() {
    cout << "Destructor Triangle" << endl;
}