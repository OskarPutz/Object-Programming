#include "Circle.h"
#include <iostream>

using namespace std;
Circle::Circle(double r)
: r(r)
{
    cout << "Constructor Circle(" << r << ")" << endl;
}
double Circle::GetR() const {
    return r;
}
void Circle::SetR(double r) {
    this->r = r;
}
double Circle::Perimeter() {
    return (2*M_PI*r);
}

double Circle::Area() {
    return M_PI*r*r;
}
void Circle::Print(std::ostream& out) const {
    cout << "Circle: radius = " << r  << endl;
}
Circle::~Circle() {
    cout << "Destructor Circle" << endl;
}