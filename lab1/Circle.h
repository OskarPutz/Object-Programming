#include "PlainFigure.h"
class Circle : public PlainFigure {
private:
    double r;
protected:
    void Print(std::ostream& out) const override;
public:
    Circle(double r);
    double GetR() const;
    void SetR(double r);
    double Perimeter() override;
    double Area() override;
    ~Circle() override;
};