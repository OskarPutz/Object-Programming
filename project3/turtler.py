from animal import Animal
import random

class Turtler(Animal):
    def __init__(self, x, y, world):
        super().__init__(2, 1, x, y, world)
    
    def action(self):
        # Turtle has a 75% chance to stay in place
        if random.randint(0, 3) < 3:
            self.age += 1
            return
        
        # Otherwise, perform normal animal movement
        super().action()
    
    def draw(self):
        return 'T'
    
    def clone(self, x, y):
        return Turtler(x, y, self.world)
