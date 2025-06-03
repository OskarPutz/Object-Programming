from organism import Organism
import random

class Plant(Organism):
    WIDTH = 40
    HEIGHT = 20
    
    def __init__(self, strength, x, y, world):
        super().__init__(strength, 0, x, y, world)
    
    def action(self):
        # Plants have a 10% chance to spread to adjacent cells
        if random.randint(0, 9) == 0:
            nx = self.x + (random.randint(0, 2) - 1)
            ny = self.y + (random.randint(0, 2) - 1)
            
            if (0 <= nx < self.WIDTH and 0 <= ny < self.HEIGHT and 
                self.world.grid[ny][nx] is None):
                # Create a new plant of the same type
                new_plant = self.__class__(nx, ny, self.world)
                self.world.queue_organism(new_plant)
        
        self.age += 1
    
    def collision(self, other):
        self.alive = False
    
    def draw(self):
        return 'P'
