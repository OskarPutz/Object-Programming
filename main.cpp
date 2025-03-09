#include <iostream>
#include "Rectangle.h"
#include "Triangle.h"
#include "Circle.h"

using namespace std;

int main()
{
    cout << "local variable:" << endl;
    Rectangle rect1(4, 5);
    cout << rect1;
    cout << "Area: " << rect1.Area() << ", Perimeter: " << rect1.Perimeter() << endl;

    cout << "\npointer:" << endl;
    Rectangle* rect2 = new Rectangle(6, 7);
    cout << *rect2;
    cout << "Area: " << rect2->Area() << ", Perimeter: " << rect2->Perimeter() << endl;

    cout << "\nlocal variable:" << endl;
    Triangle tri1(3, 4, 5);
    cout << tri1;
    cout << "Area: " << tri1.Area() << ", Perimeter: " << tri1.Perimeter() << endl;

    cout << "\npointer:" << endl;
    Triangle* tri2 = new Triangle(7, 8, 9);
    cout << *tri2;
    cout << "Area: " << tri2->Area() << ", Perimeter: " << tri2->Perimeter() << endl;

    cout << "\nlocal variable:" << endl;
    Circle cir1(6);
    cout << cir1;
    cout << "Area: " << cir1.Area() << ", Perimeter: " << cir1.Perimeter() << endl;

    cout << "\npointer:" << endl;
    Circle* cir2 = new Circle(6);
    cout << *cir2;
    cout << "Area: " << cir2->Area() << ", Perimeter: " << cir2->Perimeter() << endl;
    cout << "\n";
}
