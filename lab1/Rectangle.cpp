#include "Rectangle.h"
#include <iostream>

using namespace std;
Rectangle::Rectangle(double a, double b)
: a(a), b(b)
{
    cout << "Constructor Rectangle(" << a <<"," << b << ")" << endl;
}
double Rectangle::GetA() const {
    return a;
}
double Rectangle::GetB() const {
    return b;
}
void Rectangle::SetA(double a) {
    this->a = a;
}
void Rectangle::SetB(double b) {
    this->b = b;
}
double Rectangle::Perimeter() {
    return 2 * (a + b);
}

double Rectangle::Area() {
    return a * b;
}
void Rectangle::Print(std::ostream& out) const {
    cout << "Rectangle: width = " << a << ", height =  " << b  << endl;
}
Rectangle::~Rectangle() {
    cout << "Destructor Rectangle" << endl;
}