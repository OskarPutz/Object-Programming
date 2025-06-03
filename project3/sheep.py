from animal import Animal

class Sheep(Animal):
    def __init__(self, x, y, world):
        super().__init__(4, 4, x, y, world)
    
    def draw(self):
        return 'S'
    
    def clone(self, x, y):
        return Sheep(x, y, self.world)
